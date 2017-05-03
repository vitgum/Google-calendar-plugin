package com.eyelinecom.whoisd.sads2.google.calendar.web.bot.pages;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * author: Denis Enenko
 * date: 25.04.17
 */
public class AskTimePage extends BotPage {

  public AskTimePage(Locale locale) {
    super(6, ResourceBundle.getBundle(AskTimePage.class.getName(), locale));
  }

  @Override
  public BotPage getNextPage(String value, PageContext ctx) {
    if("cancel".equals(value))
      return new ExitMarkerPage();

    List<TimeSlot> timeSlots = ctx.getAvailableTimeSlots();

    for(TimeSlot timeSlot : timeSlots) {
      if(timeSlot.getValue().equals(value))
        return new AskCommentPage(bundle.getLocale());
    }

    return new AskTimePage(bundle.getLocale());
  }

  @Override
  public String toXml(PageContext ctx) throws IOException {
    List<TimeSlot> timeSlots = ctx.getAvailableTimeSlots();

    if(timeSlots.isEmpty())
      return new AskTimeBusyPage(bundle.getLocale()).toXml(ctx);

    StringBuilder sb = new StringBuilder();
    int i = 0;

    sb.append(pageStart());
    sb.append(divStart());
    sb.append(bundle.getString("text"));
    sb.append(br());
    sb.append(divEnd());
    sb.append(buttonsStart());
    for(TimeSlot timeSlot : timeSlots) {
      sb.append(button(++i, timeSlot.getValue(), timeSlot.getLabel(), ctx.getParameters(), ctx.getContextPath()));
    }
    sb.append(button(++i, "cancel", bundle.getString("cancel"), ctx.getParameters(), ctx.getContextPath()));
    sb.append(buttonsEnd());
    sb.append(pageEnd());

    return sb.toString();
  }

}