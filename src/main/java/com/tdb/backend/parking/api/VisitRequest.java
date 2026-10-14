package com.tdb.backend.parking.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VisitRequest(
    @NotBlank String carNumber,
    @NotBlank
        @Pattern(regexp = ParkingFeeRequest.TIME_PATTERN, message = ParkingFeeRequest.TIME_MESSAGE)
        String entry,
    @NotBlank
        @Pattern(regexp = ParkingFeeRequest.TIME_PATTERN, message = ParkingFeeRequest.TIME_MESSAGE)
        String exit) {}
