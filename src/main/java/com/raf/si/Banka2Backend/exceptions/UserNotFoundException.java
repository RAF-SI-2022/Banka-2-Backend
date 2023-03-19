package com.raf.si.Banka2Backend.exceptions;

public class UserNotFoundException extends RuntimeException {

  private String message;

  public UserNotFoundException(String message) {
    super(message);
  }
}
