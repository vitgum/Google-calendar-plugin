package com.eyelinecom.whoisd.sads2.google.calendar.web.bot.pages;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * author: Denis Enenko
 * date: 27.04.17
 */
public class AskCommentPage extends BotPage {

  public AskCommentPage(Locale locale) {
    super(8, ResourceBundle.getBundle(AskCommentPage.class.getName(), locale));
  }

  @Override
  public BotPage getNextPage(String value, PageContext ctx) {
    if("cancel".equals(value))
      return new ExitMarkerPage();

    return new ConfirmationPage(bundle.getLocale());
  }

  @Override
  public String toXml(PageContext ctx) throws IOException {
    return pageStart()
        + divStart()
        + bundle.getString("text")
        + br()
        + divEnd()
        + buttonsStart()
        + button(1, "skip", bundle.getString("skip"), ctx.getParameters(), ctx.getContextPath())
        + button(2, "cancel", bundle.getString("cancel"), ctx.getParameters(), ctx.getContextPath())
        + buttonsEnd()
        + anyInput(ctx.getParameters(), ctx.getContextPath())
        + pageEnd();
  }

}