package com.tdb.backend.parking.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ParkingFeeRequest(
    @NotBlank
        @Pattern(regexp = ParkingFeeRequest.TIME_PATTERN, message = ParkingFeeRequest.TIME_MESSAGE)
        String entry,
    @NotBlank
        @Pattern(regexp = ParkingFeeRequest.TIME_PATTERN, message = ParkingFeeRequest.TIME_MESSAGE)
        String exit) {

  static final String TIME_PATTERN = "^([01]\\d|2[0-3]):[0-5]\\d$";
  static final String TIME_MESSAGE = "must be a 24-hour time in HH:MM format";
}
