package com.eyelinecom.whoisd.sads2.google.calendar.web.servlets;

import com.eyelinecom.whoisd.sads2.google.calendar.service.datastore.UserData;
import com.eyelinecom.whoisd.sads2.google.calendar.web.bot.pages.PageContext;
import com.eyelinecom.whoisd.sads2.google.calendar.web.bot.pages.TimeSlot;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

/**
 * author: Denis Enenko
 * date: 25.04.17
 */
public class PageCtx implements PageContext {

  private final ZoneId timeZone;
  private final List<DayOfWeek> workDays;
  private final Map<String, String> parameters;
  private final List<TimeSlot> availableTimeSlots;
  private final UserData userData;
  private final String contextPath;


  public PageCtx(ZoneId timeZone, List<DayOfWeek> workDays, Map<String, String> parameters, List<TimeSlot> availableTimeSlots, UserData userData, String contextPath) {
    this.timeZone = timeZone;
    this.workDays = workDays;
    this.parameters = parameters;
    this.availableTimeSlots = availableTimeSlots;
    this.userData = userData;
    this.contextPath = contextPath;
  }

  @Override
  public ZoneId getTimeZone() {
    return timeZone;
  }

  @Override
  public List<DayOfWeek> getWorkDays() {
    return workDays;
  }

  @Override
  public Map<String, String> getParameters() {
    return parameters;
  }

  @Override
  public List<TimeSlot> getAvailableTimeSlots() {
    return availableTimeSlots;
  }

  @Override
  public UserData getUserData() {
    return userData;
  }

  @Override
  public String getContextPath() {
    return contextPath;
  }

}