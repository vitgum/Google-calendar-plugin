package com.eyelinecom.whoisd.sads2.google.calendar.web.bot.pages;

import com.eyelinecom.whoisd.sads2.google.calendar.service.datastore.UserData;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

/**
 * author: Denis Enenko
 * date: 25.04.17
 */
public interface PageContext {

  ZoneId getTimeZone();

  List<DayOfWeek> getWorkDays();

  Map<String, String> getParameters();

  List<TimeSlot> getAvailableTimeSlots();

  UserData getUserData();

  String getContextPath();

}