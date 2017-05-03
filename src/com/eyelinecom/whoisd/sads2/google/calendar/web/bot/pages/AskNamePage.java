package com.eyelinecom.whoisd.sads2.google.calendar.web.bot.pages;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * author: Denis Enenko
 * date: 24.04.17
 */
public class AskNamePage extends BotPage {

  public AskNamePage(Locale locale) {
    this(locale, false);
  }

  private AskNamePage(Locale locale, boolean error) {
    super(1, ResourceBundle.getBundle(AskNamePage.class.getName(), locale), error);
  }

  @Override
  public BotPage getNextPage(String value, PageContext ctx) {
    if("cancel".equals(value))
      return new ExitMarkerPage();

    boolean valueValid = value.length() >= 2;
    return valueValid ? new AskPhonePage(bundle.getLocale()) : new AskNamePage(bundle.getLocale(), true);
  }

  @Override
  public String toXml(PageContext ctx) throws IOException {
    return pageStart()
        + divStart()
        + (!error ? bundle.getString("text") : bundle.getString("error.text"))
        + br()
        + divEnd()
        + buttonsStart()
        + button(1, "cancel", bundle.getString("cancel"), ctx.getParameters(), ctx.getContextPath())
        + buttonsEnd()
        + anyInput(ctx.getParameters(), ctx.getContextPath())
        + pageEnd();
  }

}