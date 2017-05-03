package com.eyelinecom.whoisd.sads2.google.calendar.web.servlets;

/**
 * author: Denis Enenko
 * date: 24.04.17
 */
public class HttpServletRequestException extends Exception {

  private final int httpStatus;


  public HttpServletRequestException(String message, int httpStatus) {
    super(message);
    this.httpStatus = httpStatus;
  }

  public HttpServletRequestException(String message, Throwable cause, int httpStatus) {
    super(message, cause);
    this.httpStatus = httpStatus;
  }

  public int getHttpStatus() {
    return httpStatus;
  }

}