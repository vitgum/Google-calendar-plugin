package com.eyelinecom.whoisd.sads2.google.calendar.service;

import com.eyeline.utils.config.ConfigException;
import com.eyeline.utils.config.xml.XmlConfig;
import com.eyeline.utils.config.xml.XmlConfigSection;
import com.eyelinecom.whoisd.sads2.google.calendar.service.calendar.GoogleCalendarServiceFactory;
import com.eyelinecom.whoisd.sads2.google.calendar.service.datastore.UserDataStoreService;

/**
 * author: Denis Enenko
 * date: 21.04.17
 */
public class Services {

  private final GoogleCalendarServiceFactory googleCalendarServiceFactory;
  private final UserDataStoreService userDataStoreService;


  public Services(XmlConfig config) throws ServicesException {
    this.googleCalendarServiceFactory = initGoogleCalendarServiceFactory(config);
    this.userDataStoreService = initUserDataStoreService(config);
  }

  private GoogleCalendarServiceFactory initGoogleCalendarServiceFactory(XmlConfig config) throws ServicesException {
    try {
      XmlConfigSection section = config.getSection("google.calendar.plugin").getSection("google.calendar.api");

      String clientId = section.getString("client.id");
      String clientSecret = section.getString("client.secret");
      String applicationName = section.getString("application.name");

      return new GoogleCalendarServiceFactory(clientId, clientSecret, applicationName);
    }
    catch(ConfigException e) {
      throw new ServicesException("Error during GoogleCalendarServiceFactory initialization", e);
    }
  }

  private UserDataStoreService initUserDataStoreService(XmlConfig config) throws ServicesException {
    try {
      XmlConfigSection section = config.getSection("google.calendar.plugin").getSection("memory.data.store");

      int clearDataTimeoutMin = section.getInt("clear.data.timeout.min");
      return new UserDataStoreService(clearDataTimeoutMin * 60 * 1000L);
    }
    catch(ConfigException e) {
      throw new ServicesException("Error during UserDataStoreService initialization", e);
    }
  }

  public GoogleCalendarServiceFactory getGoogleCalendarServiceFactory() {
    return googleCalendarServiceFactory;
  }

  public UserDataStoreService getUserDataStoreService() {
    return userDataStoreService;
  }

}