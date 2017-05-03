package com.eyelinecom.whoisd.sads2.google.calendar.web.bot.pages;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * author: Denis Enenko
 * date: 24.04.17
 */
public class AskDayAnotherPage extends BotPage {

  public AskDayAnotherPage(Locale locale) {
    this(locale, false);
  }

  private AskDayAnotherPage(Locale locale, boolean error) {
    super(4, ResourceBundle.getBundle(AskDayAnotherPage.class.getName(), locale), error);
  }

  @Override
  public BotPage getNextPage(String value, PageContext ctx) {
    if("cancel".equals(value))
      return new ExitMarkerPage();

    ZoneId timeZone = ctx.getTimeZone();
    List<DayOfWeek> workDays = ctx.getWorkDays();
    LocalDate todayDay = LocalDate.now(timeZone);
    LocalDate selectedDay = getDay(value);

    if(selectedDay == null || selectedDay.isBefore(todayDay))
      return new AskDayAnotherPage(bundle.getLocale(), true);

    if(!workDays.contains(selectedDay.getDayOfWeek()))
      return new AskDayHolidayPage(bundle.getLocale());

    return new AskTimePage(bundle.getLocale());
  }

  @Override
  public String toXml(PageContext ctx) throws IOException {
    return pageStart()
        + divStart()
        + MessageFormat.format(!error ? bundle.getString("text") : bundle.getString("error.text"), DateTimeFormat.DATE_FORMAT.format(LocalDate.now(ctx.getTimeZone())))
        + br()
        + divEnd()
        + buttonsStart()
        + button(1, "cancel", bundle.getString("cancel"), ctx.getParameters(), ctx.getContextPath())
        + buttonsEnd()
        + anyInput(ctx.getParameters(), ctx.getContextPath())
        + pageEnd();
  }

  private static LocalDate getDay(String value) {
    try {
      TemporalAccessor ta = DateTimeFormat.DATE_FORMAT.parse(value);
      return LocalDate.from(ta);
    }
    catch(DateTimeParseException e) {
      return null;
    }
  }

}