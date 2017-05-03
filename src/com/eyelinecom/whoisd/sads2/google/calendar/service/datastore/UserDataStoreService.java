package com.eyelinecom.whoisd.sads2.google.calendar.service.datastore;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * author: Denis Enenko
 * date: 28.04.17
 */
public class UserDataStoreService {

  private final static Logger log = Logger.getLogger("GOOGLE_CALENDAR_DATA_STORE");

  private final Map<String, UserData> storage = new HashMap<>();


  public UserDataStoreService(long clearDataTimeoutMillis) {
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    executor.scheduleWithFixedDelay(new UserDataCleaner(clearDataTimeoutMillis), 10, 10, TimeUnit.MINUTES);
  }

  public void setData(String userId, UserData data) {
    if(log.isInfoEnabled())
      log.info("saveData: userId = " + userId + ", data = " + data);

    synchronized(storage) {
      storage.put(userId, data);
    }
  }

  public UserData getData(String userId) {
    UserData data;

    synchronized(storage) {
      data = storage.get(userId);
    }

    if(log.isInfoEnabled())
      log.info("getData: userId = " + userId + ", data = " + data);

    return data;
  }

  public void removeData(String userId) {
    UserData data;

    synchronized(storage) {
      data = storage.remove(userId);
    }

    if(log.isInfoEnabled() && data != null)
      log.info("removeData: userId = " + userId + ", data = " + data);
  }


  private class UserDataCleaner implements Runnable {

    private final long clearDataTimeoutMillis;


    private UserDataCleaner(long clearDataTimeoutMillis) {
      this.clearDataTimeoutMillis = clearDataTimeoutMillis;
    }

    @Override
    public void run() {
      if(log.isInfoEnabled())
        log.info("Starting user data cleaner...");

      long currTime = System.currentTimeMillis();
      Map<String, UserData> removedData = new HashMap<>();

      synchronized(storage) {
        Iterator<Map.Entry<String, UserData>> it = storage.entrySet().iterator();

        while(it.hasNext()) {
          Map.Entry<String, UserData> e = it.next();
          UserData data = e.getValue();

          if(data.getLastUpdate() + clearDataTimeoutMillis <= currTime) {
            removedData.put(e.getKey(), data);
            it.remove();
          }
        }
      }

      if(log.isDebugEnabled() && !removedData.isEmpty()) {
        log.debug("Removed data:");
        for(Map.Entry<String, UserData> e : removedData.entrySet()) {
          log.debug(" userId = " + e.getKey() + ", data = " + e.getValue());
        }
      }

      if(log.isInfoEnabled())
        log.info("Finished user data cleaner. Removed record count: " + removedData.size());
    }

  }

}