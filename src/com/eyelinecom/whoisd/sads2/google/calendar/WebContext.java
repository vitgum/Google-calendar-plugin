package com.eyelinecom.whoisd.sads2.google.calendar;

import com.eyelinecom.whoisd.sads2.google.calendar.service.Services;
import com.eyelinecom.whoisd.sads2.google.calendar.service.calendar.GoogleCalendarServiceFactory;
import com.eyelinecom.whoisd.sads2.google.calendar.service.datastore.UserDataStoreService;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

/**
 * author: Denis Enenko
 * date: 21.04.17
 */
@ApplicationScoped
public class WebContext {

  private static Services services;


  static synchronized void init(Services services) {
    if(WebContext.services == null)
      WebContext.services = services;
  }

  @Produces
  public GoogleCalendarServiceFactory getGoogleCalendarServiceFactory() {
    return services.getGoogleCalendarServiceFactory();
  }

  @Produces
  public UserDataStoreService getUserDataStoreService() {
    return services.getUserDataStoreService();
  }

}