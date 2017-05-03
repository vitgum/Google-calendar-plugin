package com.eyelinecom.whoisd.sads2.google.calendar.service.calendar;

import com.google.api.services.calendar.model.FreeBusyCalendar;
import com.google.api.services.calendar.model.TimePeriod;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * author: Denis Enenko
 * date: 19.04.17
 */
public class FreeBusy {

  private final List<Period> busyPeriods;


  FreeBusy(FreeBusyCalendar calendar, ZoneId timeZone) {
    if(calendar == null)
      throw new NullPointerException("FreeBusyCalendar is null");

    this.busyPeriods = parseBusyCalendar(calendar, timeZone);
  }

  private static List<Period> parseBusyCalendar(FreeBusyCalendar calendar, ZoneId timeZone) {
    if(calendar.getBusy() == null || calendar.getBusy().isEmpty())
      return Collections.emptyList();

    List<Period> periods = new ArrayList<>();

    for(TimePeriod period : calendar.getBusy()) {
      ZonedDateTime start = ZonedDateTime.ofInstant(Instant.ofEpochMilli(period.getStart().getValue()), timeZone);
      ZonedDateTime end = ZonedDateTime.ofInstant(Instant.ofEpochMilli(period.getEnd().getValue()), timeZone);
      periods.add(new Period(start, end));
    }

    return periods;
  }

  public List<Period> getBusyPeriods() {
    return busyPeriods;
  }

  public boolean isFree(Period period) {
    for(Period busyPeriod : busyPeriods) {
      long busyStart = busyPeriod.getStart().toEpochSecond();
      long busyEnd = busyPeriod.getEnd().toEpochSecond();

      long start = period.getStart().toEpochSecond();
      long end = period.getEnd().toEpochSecond();

      if(busyStart <= start && start < busyEnd)
        return false;

      if(busyStart < end && end <= busyEnd)
        return false;

      if(start <= busyStart && busyEnd <= end)
        return false;
    }

    return true;
  }

}
