package com.eyelinecom.whoisd.sads2.google.calendar.web.bot.pages;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * author: Denis Enenko
 * date: 28.04.17
 */
public class ErrorPage extends BotPage {

  public ErrorPage(Locale locale) {
    super(10, ResourceBundle.getBundle(ErrorPage.class.getName(), locale));
  }

  @Override
  public BotPage getNextPage(String value, PageContext ctx) {
    if("cancel".equals(value))
      return new ExitMarkerPage();

    return new AskNamePage(bundle.getLocale());
  }

  @Override
  public String toXml(PageContext ctx) throws IOException {
    return pageStart()
        + divStart()
        + bundle.getString("text")
        + br()
        + divEnd()
        + buttonsStart()
        + button(1, "start_again", bundle.getString("start.again"), ctx.getParameters(), ctx.getContextPath())
        + button(2, "cancel", bundle.getString("cancel"), ctx.getParameters(), ctx.getContextPath())
        + buttonsEnd()
        + pageEnd();
  }

}