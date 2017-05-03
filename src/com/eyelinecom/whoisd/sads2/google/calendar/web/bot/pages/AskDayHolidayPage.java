package com.eyelinecom.whoisd.sads2.google.calendar.web.bot.pages;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * author: Denis Enenko
 * date: 25.04.17
 */
public class AskDayHolidayPage extends BotPage {

  public AskDayHolidayPage(Locale locale) {
    super(5, ResourceBundle.getBundle(AskDayHolidayPage.class.getName(), locale));
  }

  @Override
  public BotPage getNextPage(String value, PageContext ctx) {
    if("cancel".equals(value))
      return new ExitMarkerPage();

    return new AskDayAnotherPage(bundle.getLocale());
  }

  @Override
  public String toXml(PageContext ctx) throws IOException {
    return pageStart()
        + divStart()
        + bundle.getString("text")
        + br()
        + divEnd()
        + buttonsStart()
        + button(1, "choose_day", bundle.getString("choose.day"), ctx.getParameters(), ctx.getContextPath())
        + button(2, "cancel", bundle.getString("cancel"), ctx.getParameters(), ctx.getContextPath())
        + buttonsEnd()
        + pageEnd();
  }

}