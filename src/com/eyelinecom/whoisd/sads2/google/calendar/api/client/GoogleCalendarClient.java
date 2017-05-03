package com.eyelinecom.whoisd.sads2.google.calendar.api.client;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;

import java.io.IOException;

/**
 * author: Denis Enenko
 * date: 19.04.17
 */
public class GoogleCalendarClient {

  private final Calendar calendar;


  public GoogleCalendarClient(Calendar calendar) {
    this.calendar = calendar;
  }

  public Event addEvent(String calendarId, Event event) throws IOException {
    return calendar.events().insert(calendarId, event).execute();
  }

  public FreeBusyResponse getFreeBusy(FreeBusyRequest request) throws IOException {
    return calendar.freebusy().query(request).execute();
  }

}
