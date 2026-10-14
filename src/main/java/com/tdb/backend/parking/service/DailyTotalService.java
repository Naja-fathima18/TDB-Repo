package com.tdb.backend.parking.service;

import com.tdb.backend.parking.domain.ParkingVisit;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DailyTotalService {

  private static final long MAX_FEE_PER_CAR_PER_DAY = 100;

  private final ParkingFeeService parkingFeeService;

  public long totalCollected(List<ParkingVisit> visits) {
    Objects.requireNonNull(visits, "visits");
    Map<String, Long> feesByCar =
        visits.stream()
            .collect(
                Collectors.groupingBy(
                    visit -> normalizedCarNumber(visit.carNumber()),
                    Collectors.summingLong(this::feeFor)));
    return feesByCar.values().stream()
        .mapToLong(fee -> Math.min(fee, MAX_FEE_PER_CAR_PER_DAY))
        .sum();
  }

  private long feeFor(ParkingVisit visit) {
    return parkingFeeService.feeFor(visit.entry(), visit.exit());
  }

  private static String normalizedCarNumber(String carNumber) {
    return Objects.requireNonNull(carNumber, "carNumber").trim().toUpperCase(Locale.ROOT);
  }
}
