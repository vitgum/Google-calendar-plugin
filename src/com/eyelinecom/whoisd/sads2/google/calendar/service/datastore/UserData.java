package com.eyelinecom.whoisd.sads2.google.calendar.service.datastore;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * author: Denis Enenko
 * date: 27.04.17
 */
public class UserData {

  private String name;
  private String phone;
  private LocalDate date;
  private LocalTime timeStart;
  private LocalTime timeEnd;
  private String comment;
  private long lastUpdate;


  public boolean isConsistent() {
    return name != null && phone != null && date != null && timeStart != null && timeEnd != null;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
    lastUpdate = System.currentTimeMillis();
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
    lastUpdate = System.currentTimeMillis();
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
    lastUpdate = System.currentTimeMillis();
  }

  public LocalTime getTimeStart() {
    return timeStart;
  }

  public void setTimeStart(LocalTime timeStart) {
    this.timeStart = timeStart;
    lastUpdate = System.currentTimeMillis();
  }

  public LocalTime getTimeEnd() {
    return timeEnd;
  }

  public void setTimeEnd(LocalTime timeEnd) {
    this.timeEnd = timeEnd;
    lastUpdate = System.currentTimeMillis();
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
    lastUpdate = System.currentTimeMillis();
  }

  long getLastUpdate() {
    return lastUpdate;
  }

  @Override
  public String toString() {
    return "UserData { " +
        "name='" + name + '\'' +
        ", phone='" + phone + '\'' +
        ", date=" + date +
        ", timeStart=" + timeStart +
        ", timeEnd=" + timeEnd +
        ", comment='" + comment + '\'' +
        ", lastUpdate=" + Instant.ofEpochMilli(lastUpdate) +
        " }";
  }

}