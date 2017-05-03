package com.eyelinecom.whoisd.sads2.google.calendar.service.calendar;

import com.eyelinecom.whoisd.sads2.google.calendar.api.client.GoogleCalendarClient;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.*;

import java.time.ZonedDateTime;
import java.util.Collections;

/**
 * author: Denis Enenko
 * date: 19.04.17
 */
public class GoogleCalendarService {

  private final GoogleCalendarClient client;


  GoogleCalendarService(GoogleCalendarClient client) {
    this.client = client;
  }

  public void addEvent(String calendarId, String summary, String description, ZonedDateTime start, ZonedDateTime end) throws GoogleCalendarServiceException {
    EventDateTime eventStart = new EventDateTime()
        .setDateTime(new DateTime(start.toInstant().toEpochMilli()))
        .setTimeZone(start.getZone().getId());

    EventDateTime eventEnd = new EventDateTime()
        .setDateTime(new DateTime(end.toInstant().toEpochMilli()))
        .setTimeZone(end.getZone().getId());

    Event event = new Event()
        .setSummary(summary)
        .setDescription(description)
        .setStart(eventStart)
        .setEnd(eventEnd);

    try {
      client.addEvent(calendarId, event);
    }
    catch(Exception e) {
      throw new GoogleCalendarServiceException(e.getMessage(), e);
    }
  }

  public FreeBusy getFreeBusy(String calendarId, ZonedDateTime start, ZonedDateTime end) throws GoogleCalendarServiceException {
    FreeBusyRequestItem item = new FreeBusyRequestItem().setId(calendarId);

    FreeBusyRequest request = new FreeBusyRequest()
        .setItems(Collections.singletonList(item))
        .setTimeMin(new DateTime(start.toInstant().toEpochMilli()))
        .setTimeMax(new DateTime(end.toInstant().toEpochMilli()))
        .setTimeZone(start.getZone().getId());

    try {
      FreeBusyResponse response = client.getFreeBusy(request);

      if(response.getCalendars() == null || response.getCalendars().isEmpty())
        throw new GoogleCalendarServiceException("Not found calendar with ID: " + calendarId);

      FreeBusyCalendar freeBusyCalendar = response.getCalendars().get(calendarId);

      if(freeBusyCalendar == null)
        throw new GoogleCalendarServiceException("Not found calendar with ID: " + calendarId);

      return new FreeBusy(freeBusyCalendar, start.getZone());
    }
    catch(Exception e) {
      throw new GoogleCalendarServiceException(e.getMessage(), e);
    }
  }

}
