package com.eyelinecom.whoisd.sads2.google.calendar.web.servlets;

import com.eyelinecom.whoisd.sads2.google.calendar.service.calendar.*;
import com.eyelinecom.whoisd.sads2.google.calendar.service.calendar.Period;
import com.eyelinecom.whoisd.sads2.google.calendar.service.datastore.UserDataStoreService;
import com.eyelinecom.whoisd.sads2.google.calendar.web.bot.pages.DateTimeFormat;
import com.eyelinecom.whoisd.sads2.google.calendar.web.bot.pages.TimeSlot;
import com.eyelinecom.whoisd.sads2.google.calendar.service.datastore.UserData;
import com.eyelinecom.whoisd.sads2.google.calendar.web.bot.pages.*;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.*;

/**
 * author: Denis Enenko
 * date: 21.04.17
 */
public class GoogleCalendarServlet extends HttpServlet {

  private final static Logger log = Logger.getLogger("GOOGLE_CALENDAR_PLUGIN");
  private final static DateTimeFormatter WORK_HOURS_FORMAT = DateTimeFormatter.ofPattern("HH");
  private final static String CALENDAR_ID = "primary";

  @Inject
  private GoogleCalendarServiceFactory calendarServiceFactory;

  @Inject
  private UserDataStoreService storage;


  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    handleRequest(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    handleRequest(request, response);
  }

  private void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    logRequest(request);

    try {
      String userId = getRequiredParameter(request, "user_id");
      int pid = getPid(request);
      Locale locale = getLocale(request);
      Map<String, String> params = getParameters(request);

      String userInput = pid == 1 ? request.getParameter("user_input") : getRequiredParameter(request, "user_input");

      ZoneId timeZone = ZoneId.of(params.get("time_zone"));
      List<DayOfWeek> workDays = getWorkDays(params.get("work_days"));
      int timeSlot = getTimeSlot(params.get("time_slot"));

      UserData userData = storage.getData(userId);
      if(userData == null) {
        userData = new UserData();
        storage.setData(userId, userData);
      }

      if(log.isInfoEnabled()) {
        log.info("user ID: " + userId);
        log.info("page ID: " + pid);
        log.info("user input: '" + userInput + "'");
        log.info("user data: " + userData);
      }

      LocalDate selectedDay = pid == 3 || pid == 4 ? getSelectedDay(userInput, timeZone) : null;
      if(pid == 6) {
        selectedDay = userData.getDate();
      }

      List<TimeSlot> availableTimeSlots = selectedDay != null
          ? getAvailableTimeSlots(selectedDay, timeZone, params.get("work_hours"), timeSlot, locale, params.get("access_token"), params.get("refresh_token"))
          : Collections.emptyList();

      PageContext pageCtx = new PageCtx(timeZone, workDays, params, availableTimeSlots, userData, request.getContextPath());

      if(pid == 1 && userInput == null) {
        sendResponse(response, new AskNamePage(locale).toXml(pageCtx));
        return;
      }

      BotPage curr = getPage(pid, locale);
      if(curr == null)
        throw new HttpServletRequestException("Page not found. Invalid \"pid\" parameter: " + pid, HttpServletResponse.SC_BAD_REQUEST);

      saveUserData(pid, userData, userInput, selectedDay, availableTimeSlots);

      BotPage next = curr.getNextPage(userInput, pageCtx);

      if(next instanceof ExitMarkerPage) {
        String exitUrl = params.get("on_exit_url");
        if(log.isInfoEnabled()) {
          log.info("Exit from bot to: " + exitUrl);
        }
        sendRedirect(response, exitUrl);
        return;
      }

      if(next instanceof EventAddMarkerPage) {
        try {
          addCalendarEvent(userData, params.get("access_token"), params.get("refresh_token"), locale, timeZone);
          if(log.isInfoEnabled()) {
            log.info("Event added to calendar: " + userData);
          }
          storage.removeData(userId);
          sendRedirect(response, params.get("on_add_event_url"));
          return;
        }
        catch(GoogleCalendarServiceException e) {
          log.error(e.getMessage(), e);
          sendResponse(response, new ErrorPage(locale).toXml(pageCtx));
          return;
        }
      }

      sendResponse(response, next.toXml(pageCtx));
    }
    catch(HttpServletRequestException e) {
      log.error(e.getMessage(), e);
      response.sendError(e.getHttpStatus(), e.getMessage());
    }
    catch(Exception e) {
      log.error(e.getMessage(), e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  private static BotPage getPage(int pageId, Locale locale) {
    switch(pageId) {
      case 1:
        return new AskNamePage(locale);
      case 2:
        return new AskPhonePage(locale);
      case 3:
        return new AskDayPage(locale);
      case 4:
        return new AskDayAnotherPage(locale);
      case 5:
        return new AskDayHolidayPage(locale);
      case 6:
        return new AskTimePage(locale);
      case 7:
        return new AskTimeBusyPage(locale);
      case 8:
        return new AskCommentPage(locale);
      case 9:
        return new ConfirmationPage(locale);
      case 10:
        return new ErrorPage(locale);
      default:
        return null;
    }
  }

  private static void saveUserData(int pageId, UserData userData, String userInput, LocalDate selectedDay, List<TimeSlot> availableTimeSlots) {
    switch(pageId) {
      case 1:
        userData.setName(userInput);
        break;
      case 2:
        userData.setPhone(userInput);
        break;
      case 3:
      case 4:
        userData.setDate(selectedDay);
        break;
      case 6:
        TimeSlot t = getTime(availableTimeSlots, userInput);
        if(t != null) {
          userData.setTimeStart(t.getStart());
          userData.setTimeEnd(t.getEnd());
        }
        break;
      case 8:
        if(!userInput.equals("skip")) {
          userData.setComment(userInput);
        }
        break;
    }
  }

  private static TimeSlot getTime(List<TimeSlot> availableTimeSlots, String userInput) {
    for(TimeSlot timeSlot : availableTimeSlots) {
      if(timeSlot.getValue().equals(userInput))
        return timeSlot;
    }
    return null;
  }

  private void addCalendarEvent(UserData data, String accessToken, String refreshToken, Locale locale, ZoneId timeZone) throws GoogleCalendarServiceException {
    GoogleCalendarService calendar = calendarServiceFactory.createService(accessToken, refreshToken);
    ResourceBundle bundle = ResourceBundle.getBundle(getClass().getName(), locale);

    String summary = data.getName() + " (" + bundle.getString("phone") + ": " + data.getPhone() + ")";
    String description = data.getComment() != null ? data.getComment() : "";

    ZonedDateTime start = ZonedDateTime.of(data.getDate(), data.getTimeStart(), timeZone);
    ZonedDateTime end = ZonedDateTime.of(data.getDate(), data.getTimeEnd(), timeZone);

    calendar.addEvent(CALENDAR_ID, summary, description, start, end);
  }

  private static void sendResponse(HttpServletResponse response, String xmlPage) throws IOException {
    if(log.isInfoEnabled())
      log.info("Send response: " + xmlPage);

    response.setContentType("text/xml; charset=utf-8");
    response.setCharacterEncoding("UTF-8");
    response.setStatus(HttpServletResponse.SC_OK);

    PrintWriter out = response.getWriter();
    out.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
    out.write(xmlPage);
    out.close();
  }

  private static void sendRedirect(HttpServletResponse response, String url) throws IOException {
    if(log.isInfoEnabled())
      log.info("Send redirect: " + url);

    response.sendRedirect(url);
  }

  private static LocalDate getSelectedDay(String dayValue, ZoneId timeZone) {
    try {
      AskDayPage.Day day = AskDayPage.Day.valueOf(dayValue.toUpperCase());
      switch(day) {
        case TODAY:
          return LocalDate.now(timeZone);
        case TOMORROW:
          return LocalDate.now(timeZone).plusDays(1);
        case ANOTHER:
          return null;
      }
    }
    catch(IllegalArgumentException ignored) {
    }

    try {
      TemporalAccessor ta = DateTimeFormat.DATE_FORMAT.parse(dayValue);
      return LocalDate.from(ta);
    }
    catch(RuntimeException e) {
      return null;
    }
  }

  private static Locale getLocale(HttpServletRequest request) {
    String lang = request.getParameter("lang");

    if(lang == null)
      lang = "en";

    return new Locale(lang);
  }

  private static int getPid(HttpServletRequest request) throws HttpServletRequestException {
    String pid = request.getParameter("pid");

    if(pid == null)
      return 1;

    try {
      return Integer.parseInt(pid);
    }
    catch(NumberFormatException e) {
      throw new HttpServletRequestException("Invalid \"pid\" parameter: " + pid, e, HttpServletResponse.SC_BAD_REQUEST);
    }
  }

  private static int getTimeSlot(String timeSlotParam) throws HttpServletRequestException {
    try {
      return Integer.parseInt(timeSlotParam);
    }
    catch(NumberFormatException e) {
      throw new HttpServletRequestException("Invalid \"time_slot\" parameter: " + timeSlotParam, HttpServletResponse.SC_BAD_REQUEST);
    }
  }

  private static List<DayOfWeek> getWorkDays(String workDaysParam) throws HttpServletRequestException {
    List<DayOfWeek> workDays = new ArrayList<>();

    try {
      for(char workDay : workDaysParam.toCharArray()) {
        int dayOfWeek = Character.getNumericValue(workDay);
        workDays.add(DayOfWeek.of(dayOfWeek));
      }
    }
    catch(RuntimeException e) {
      throw new HttpServletRequestException("Invalid \"work_days\" parameter: " + workDaysParam, HttpServletResponse.SC_BAD_REQUEST);
    }

    return workDays;
  }

  private List<TimeSlot> getAvailableTimeSlots(LocalDate selectedDay, ZoneId timeZone, String workHoursParam, int timeSlot,
                                               Locale locale, String accessToken, String refreshToken) throws HttpServletRequestException, GoogleCalendarServiceException {
    List<TimeSlot> freeTimeSlots = new ArrayList<>();

    ZonedDateTime startTime, endTime;

    try {
      int idx = workHoursParam.indexOf('-');
      String start = workHoursParam.substring(0, idx);
      String end = workHoursParam.substring(idx + 1);

      startTime = selectedDay.atStartOfDay(timeZone).withHour(WORK_HOURS_FORMAT.parse(start).get(ChronoField.HOUR_OF_DAY));
      endTime = selectedDay.atStartOfDay(timeZone).withHour(WORK_HOURS_FORMAT.parse(end).get(ChronoField.HOUR_OF_DAY));
    }
    catch(RuntimeException e) {
      throw new HttpServletRequestException("Invalid \"work_hours\" parameter: " + workHoursParam, HttpServletResponse.SC_BAD_REQUEST);
    }

    GoogleCalendarService calendar = calendarServiceFactory.createService(accessToken, refreshToken);
    FreeBusy freeBusy = calendar.getFreeBusy(CALENDAR_ID, startTime, endTime);
    List<Period> freePeriods = getFreePeriods(freeBusy, startTime, endTime, timeSlot);

    ZonedDateTime now = ZonedDateTime.now(timeZone);

    for(Period freePeriod : freePeriods) {
      if(freePeriod.getStart().isAfter(now))
        freeTimeSlots.add(new TimeSlot(LocalTime.from(freePeriod.getStart()), LocalTime.from(freePeriod.getEnd()), locale));
    }

    return freeTimeSlots;
  }

  private static List<Period> getFreePeriods(FreeBusy freeBusy, ZonedDateTime startTime, ZonedDateTime endTime, int timeSlot) {
    List<Period> freePeriods = new ArrayList<>();

    ZonedDateTime periodStart = startTime;
    ZonedDateTime periodEnd;

    while(periodStart.isBefore(endTime)) {
      periodEnd = periodStart.plusMinutes(timeSlot);
      Period period = new Period(periodStart, periodEnd);
      if(freeBusy.isFree(period)) {
        freePeriods.add(period);
      }
      periodStart = periodEnd;
    }

    return freePeriods;
  }

  private static Map<String, String> getParameters(HttpServletRequest request) throws HttpServletRequestException {
    String accessToken = getRequiredParameter(request, "access_token");
    String refreshToken = getRequiredParameter(request, "refresh_token");
    String timeZone = getRequiredParameter(request, "time_zone");
    String timeSlot = getRequiredParameter(request, "time_slot");
    String workDays = getRequiredParameter(request, "work_days");
    String workHours = getRequiredParameter(request, "work_hours");
    String onAddEventUrl = getRequiredParameter(request, "on_add_event_url");
    String onExitUrl = getRequiredParameter(request, "on_exit_url");

    Map<String, String> params = new HashMap<>();
    params.put("access_token", accessToken);
    params.put("refresh_token", refreshToken);
    params.put("time_zone", timeZone); //GMT+7
    params.put("time_slot", timeSlot); //30
    params.put("work_days", workDays); //12345
    params.put("work_hours", workHours); //09-18
    params.put("on_add_event_url", onAddEventUrl);
    params.put("on_exit_url", onExitUrl);
    return params;
  }

  private static String getRequiredParameter(HttpServletRequest request, String name) throws HttpServletRequestException {
    String value = request.getParameter(name);

    if(value == null)
      throw new HttpServletRequestException("No \"" + name + "\" parameter", HttpServletResponse.SC_BAD_REQUEST);

    if(value.isEmpty())
      throw new HttpServletRequestException("Empty \"" + name + "\" parameter", HttpServletResponse.SC_BAD_REQUEST);

    return value;
  }

  private static void logRequest(HttpServletRequest request) {
    if(log.isInfoEnabled()) {
      String requestUrl = request.getRequestURL().toString();
      String query = request.getQueryString();

      if(query != null && !query.isEmpty())
        requestUrl += "?" + query;

      log.info("Requested URL: " + requestUrl);
    }
  }

}