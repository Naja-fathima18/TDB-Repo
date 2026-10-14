package com.tdb.backend.parking.api;

import com.tdb.backend.parking.service.ParkingFeeService;
import jakarta.validation.Valid;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/parking")
@RequiredArgsConstructor
public class ParkingFeeController {

  private final ParkingFeeService parkingFeeService;

  @PostMapping("/fee")
  public ParkingFeeResponse calculateFee(@Valid @RequestBody ParkingFeeRequest request) {
    int fee =
        parkingFeeService.feeFor(LocalTime.parse(request.entry()), LocalTime.parse(request.exit()));
    return new ParkingFeeResponse(fee);
  }
}
