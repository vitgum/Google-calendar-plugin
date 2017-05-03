package com.eyelinecom.whoisd.sads2.google.calendar.web.bot.pages;

import java.io.IOException;

/**
 * author: Denis Enenko
 * date: 27.04.17
 */
public class ExitMarkerPage extends BotPage {

  public ExitMarkerPage() {
    super(12, null);
  }

  @Override
  public BotPage getNextPage(String value, PageContext ctx) {
    return null;
  }

  @Override
  public String toXml(PageContext ctx) throws IOException {
    return null;
  }

}