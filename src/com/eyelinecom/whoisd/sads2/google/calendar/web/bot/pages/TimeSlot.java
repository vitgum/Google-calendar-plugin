package com.eyelinecom.whoisd.sads2.google.calendar.web.bot.pages;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * author: Denis Enenko
 * date: 26.04.17
 */
public class TimeSlot {

  private final static DateTimeFormatter VALUE_TIME_FORMAT = DateTimeFormatter.ofPattern("HHmm");

  private final static DateTimeFormatter LABEL_TIME_FORMAT = DateTimeFormatter.ofPattern("h:mm a");
  private final static DateTimeFormatter LABEL_TIME_FORMAT_RU = DateTimeFormatter.ofPattern("HH:mm");

  private final LocalTime start;
  private final LocalTime end;
  private final DateTimeFormatter timeFormat;


  public TimeSlot(LocalTime start, LocalTime end, Locale locale) {
    this.start = start;
    this.end = end;
    this.timeFormat = locale.getLanguage().equals("ru") ? LABEL_TIME_FORMAT_RU : LABEL_TIME_FORMAT;
  }

  public LocalTime getStart() {
    return start;
  }

  public LocalTime getEnd() {
    return end;
  }

  public String getLabel() {
    return timeFormat.format(start) + " - " + timeFormat.format(end);
  }

  public String getValue() {
    return VALUE_TIME_FORMAT.format(start) + "_" + VALUE_TIME_FORMAT.format(end);
  }

}