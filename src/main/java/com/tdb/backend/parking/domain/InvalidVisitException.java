package com.tdb.backend.parking.domain;

public class InvalidVisitException extends RuntimeException {

  public InvalidVisitException(String message) {
    super(message);
  }
}
