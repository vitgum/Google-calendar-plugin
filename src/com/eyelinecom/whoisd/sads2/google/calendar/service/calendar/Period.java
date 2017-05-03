package com.eyelinecom.whoisd.sads2.google.calendar.service.calendar;

import java.time.ZonedDateTime;

/**
 * author: Denis Enenko
 * date: 20.04.17
 */
public class Period {

  private final ZonedDateTime start;
  private final ZonedDateTime end;


  public Period(ZonedDateTime start, ZonedDateTime end) {
    this.start = start;
    this.end = end;
  }

  public ZonedDateTime getStart() {
    return start;
  }

  public ZonedDateTime getEnd() {
    return end;
  }

  @Override
  public String toString() {
    return "Period { start = " + start + ", end = " + end + " }";
  }

}
