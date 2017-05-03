package com.eyelinecom.whoisd.sads2.google.calendar.web.bot.pages;

import com.eyelinecom.whoisd.sads2.google.calendar.service.datastore.UserData;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * author: Denis Enenko
 * date: 27.04.17
 */
public class ConfirmationPage extends BotPage {

  public ConfirmationPage(Locale locale) {
    super(9, ResourceBundle.getBundle(ConfirmationPage.class.getName(), locale));
  }

  @Override
  public BotPage getNextPage(String value, PageContext ctx) {
    switch(value) {
      case "add_event":
        return new EventAddMarkerPage();
      case "start_again":
        return new AskNamePage(bundle.getLocale());
      case "cancel":
        return new ExitMarkerPage();
      default:
        return new ConfirmationPage(bundle.getLocale());
    }
  }

  @Override
  public String toXml(PageContext ctx) throws IOException {
    UserData data = ctx.getUserData();

    if(!data.isConsistent())
      return new ErrorPage(bundle.getLocale()).toXml(ctx);

    return pageStart()
        + divStart()
        + buildPageText(data)
        + br()
        + divEnd()
        + buttonsStart()
        + button(1, "add_event", bundle.getString("add.event"), ctx.getParameters(), ctx.getContextPath())
        + button(2, "start_again", bundle.getString("start.again"), ctx.getParameters(), ctx.getContextPath())
        + button(3, "cancel", bundle.getString("cancel"), ctx.getParameters(), ctx.getContextPath())
        + buttonsEnd()
        + pageEnd();
  }

  private String buildPageText(UserData data) {
    return bundle.getString("text")
        + br()
        + bundle.getString("event.name") + ": "
        + data.getName()
        + br()
        + bundle.getString("event.phone") + ": "
        + data.getPhone()
        + br()
        + bundle.getString("event.date") + ": "
        + DateTimeFormat.DATE_FORMAT.format(data.getDate())
        + br()
        + bundle.getString("event.time") + ": "
        + new TimeSlot(data.getTimeStart(), data.getTimeEnd(), bundle.getLocale()).getLabel()
        + br()
        + bundle.getString("event.comment") + ": "
        + (data.getComment() != null ? data.getComment() : "");
  }

}