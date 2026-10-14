package com.tdb.backend.parking.domain;

import java.time.LocalTime;

public record ParkingVisit(String carNumber, LocalTime entry, LocalTime exit) {}
