package com.tdb.backend.parking.api;

import com.tdb.backend.parking.domain.ParkingVisit;
import com.tdb.backend.parking.service.DailyTotalService;
import jakarta.validation.Valid;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/parking")
@RequiredArgsConstructor
public class DailyTotalController {

  private final DailyTotalService dailyTotalService;

  @PostMapping("/daily-total")
  public DailyTotalResponse calculateDailyTotal(@Valid @RequestBody DailyTotalRequest request) {
    List<ParkingVisit> visits =
        request.visits().stream().map(DailyTotalController::toVisit).toList();
    return new DailyTotalResponse(dailyTotalService.totalCollected(visits));
  }

  private static ParkingVisit toVisit(VisitRequest visit) {
    return new ParkingVisit(
        visit.carNumber(), LocalTime.parse(visit.entry()), LocalTime.parse(visit.exit()));
  }
}
