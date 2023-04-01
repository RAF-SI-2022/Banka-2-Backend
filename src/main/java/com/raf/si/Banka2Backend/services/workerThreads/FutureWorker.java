package com.raf.si.Banka2Backend.services.workerThreads;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FutureWorker extends Thread {

  private Map<String, String> map = new ConcurrentHashMap<>();

  @Override
  public void run() {
    while (true) {
      /*
      uzmi mi iz mape
      provri da li taj uzeti ima
      do

       */

    }
  }
}
