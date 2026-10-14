package com.tdb.backend.parking.service;

import com.tdb.backend.parking.domain.InvalidVisitException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class ParkingFeeService {

  private static final int FREE_MINUTES = 30;
  private static final int FEE_PER_STARTED_HOUR = 20;
  private static final int MAX_FEE_PER_VISIT = 100;
  private static final int MINUTES_PER_HOUR = 60;

  public int feeFor(LocalTime entry, LocalTime exit) {
    Objects.requireNonNull(entry, "entry");
    Objects.requireNonNull(exit, "exit");
    if (exit.isBefore(entry)) {
      throw new InvalidVisitException("Exit time must not be before entry time");
    }
    long minutes = Duration.between(entry, exit).toMinutes();
    if (minutes <= FREE_MINUTES) {
      return 0;
    }
    long startedHours = (minutes + MINUTES_PER_HOUR - 1) / MINUTES_PER_HOUR;
    return (int) Math.min(startedHours * FEE_PER_STARTED_HOUR, MAX_FEE_PER_VISIT);
  }
}
