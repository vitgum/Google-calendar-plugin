package com.eyelinecom.whoisd.sads2.google.calendar.web.bot.pages;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * author: Denis Enenko
 * date: 24.04.17
 */
public class AskDayPage extends BotPage {

  public AskDayPage(Locale locale) {
    super(3, ResourceBundle.getBundle(AskDayPage.class.getName(), locale));
  }

  @Override
  public BotPage getNextPage(String value, PageContext ctx) {
    if("cancel".equals(value))
      return new ExitMarkerPage();

    Day selectedDay = getDay(value);
    if(selectedDay == null)
      return new AskDayPage(bundle.getLocale());

    ZoneId timeZone = ctx.getTimeZone();
    List<DayOfWeek> workDays = ctx.getWorkDays();

    switch(selectedDay) {
      case TODAY:
        LocalDate today = LocalDate.now(timeZone);
        DayOfWeek todayDay = today.getDayOfWeek();
        if(!workDays.contains(todayDay)) {
          return new AskDayHolidayPage(bundle.getLocale());
        }
        return new AskTimePage(bundle.getLocale());
      case TOMORROW:
        LocalDate tomorrow = LocalDate.now(timeZone).plusDays(1);
        DayOfWeek tomorrowDay = tomorrow.getDayOfWeek();
        if(!workDays.contains(tomorrowDay)) {
          return new AskDayHolidayPage(bundle.getLocale());
        }
        return new AskTimePage(bundle.getLocale());
      case ANOTHER:
      default:
        return new AskDayAnotherPage(bundle.getLocale());
    }
  }

  @Override
  public String toXml(PageContext ctx) throws IOException {
    ZoneId timeZone = ctx.getTimeZone();
    List<DayOfWeek> workDays = ctx.getWorkDays();

    LocalDate today = LocalDate.now(timeZone);
    DayOfWeek todayDay = today.getDayOfWeek();
    DayOfWeek tomorrowDay = todayDay.plus(1);

    StringBuilder sb = new StringBuilder();

    sb.append(pageStart());
    sb.append(divStart());
    sb.append(bundle.getString("text"));
    sb.append(br());
    sb.append(divEnd());
    sb.append(buttonsStart());
    int i = 0;
    if(workDays.contains(todayDay)) {
      sb.append(button(++i, Day.TODAY.name().toLowerCase(), bundle.getString("today"), ctx.getParameters(), ctx.getContextPath()));
    }
    if(workDays.contains(tomorrowDay)) {
      sb.append(button(++i, Day.TOMORROW.name().toLowerCase(), bundle.getString("tomorrow"), ctx.getParameters(), ctx.getContextPath()));
    }
    sb.append(button(++i, Day.ANOTHER.name().toLowerCase(), bundle.getString("another.day"), ctx.getParameters(), ctx.getContextPath()));
    sb.append(button(++i, "cancel", bundle.getString("cancel"), ctx.getParameters(), ctx.getContextPath()));
    sb.append(buttonsEnd());
    sb.append(pageEnd());

    return sb.toString();
  }

  private static Day getDay(String value) {
    try {
      return Day.valueOf(value.toUpperCase());
    }
    catch(IllegalArgumentException e) {
      return null;
    }
  }

  public enum Day {
    TODAY, TOMORROW, ANOTHER
  }

}