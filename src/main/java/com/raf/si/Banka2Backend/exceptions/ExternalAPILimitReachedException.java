package com.raf.si.Banka2Backend.exceptions;

public class ExternalAPILimitReachedException extends RuntimeException {

  public ExternalAPILimitReachedException() {
    super("External API limit reached! (5 calls per minute");
  }
}
