package com.eyelinecom.whoisd.sads2.google.calendar.web.bot.pages;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * author: Denis Enenko
 * date: 24.04.17
 */
public class AskPhonePage extends BotPage {

  private final static Pattern PATTERN_DIGITS = Pattern.compile("^(\\d+)$");


  public AskPhonePage(Locale locale) {
    this(locale, false);
  }

  private AskPhonePage(Locale locale, boolean error) {
    super(2, ResourceBundle.getBundle(AskPhonePage.class.getName(), locale), error);
  }

  @Override
  public BotPage getNextPage(String value, PageContext ctx) {
    if("cancel".equals(value))
      return new ExitMarkerPage();

    boolean valueValid = value.length() >= 3 && value.length() <= 15 && PATTERN_DIGITS.matcher(value).matches();
    return valueValid ? new AskDayPage(bundle.getLocale()) : new AskPhonePage(bundle.getLocale(), true);
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