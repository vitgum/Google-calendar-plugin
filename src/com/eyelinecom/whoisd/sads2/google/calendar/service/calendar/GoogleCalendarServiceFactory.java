package com.eyelinecom.whoisd.sads2.google.calendar.service.calendar;

import com.eyelinecom.whoisd.sads2.google.calendar.api.client.GoogleCalendarClient;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * author: Denis Enenko
 * date: 19.04.17
 */
public class GoogleCalendarServiceFactory {

  private final static JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  private final String clientId;
  private final String clientSecret;
  private final String applicationName;


  public GoogleCalendarServiceFactory(String clientId, String clientSecret, String applicationName) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.applicationName = applicationName;
  }

  public GoogleCalendarService createService(String accessToken, String refreshToken) throws GoogleCalendarServiceException {
    GoogleCalendarClient client = createClient(accessToken, refreshToken);
    return new GoogleCalendarService(client);
  }

  private GoogleCalendarClient createClient(String accessToken, String refreshToken) throws GoogleCalendarServiceException {
    HttpTransport httpTransport;
    try {
      httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    }
    catch(GeneralSecurityException | IOException e) {
      throw new GoogleCalendarServiceException(e.getMessage(), e);
    }

    GoogleCredential credential = new GoogleCredential.Builder()
        .setTransport(httpTransport)
        .setJsonFactory(JSON_FACTORY)
        .setClientSecrets(clientId, clientSecret)
        .build()
        .setAccessToken(accessToken)
        .setRefreshToken(refreshToken);

    Calendar calendar = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
        .setApplicationName(applicationName)
        .build();

    return new GoogleCalendarClient(calendar);
  }

}
