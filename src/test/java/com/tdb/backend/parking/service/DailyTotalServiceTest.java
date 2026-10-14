package com.tdb.backend.parking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tdb.backend.parking.domain.InvalidVisitException;
import com.tdb.backend.parking.domain.ParkingVisit;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class DailyTotalServiceTest {

  private final DailyTotalService service = new DailyTotalService(new ParkingFeeService());

  @Test
  void sumsFeesOfDifferentCars() {
    assertThat(
            service.totalCollected(
                List.of(visit("KL01", "09:00", "10:00"), visit("KL02", "09:00", "09:20"))))
        .isEqualTo(20);
  }

  @Test
  void capsSameCarAtHundredAcrossVisits() {
    assertThat(
            service.totalCollected(
                List.of(visit("KL01", "08:00", "12:00"), visit("KL01", "13:00", "16:00"))))
        .isEqualTo(100);
  }

  @Test
  void returnsZeroForNoVisits() {
    assertThat(service.totalCollected(List.of())).isZero();
  }

  @Test
  void appliesCapPerCarNotGlobally() {
    assertThat(
            service.totalCollected(
                List.of(
                    visit("KL01", "08:00", "12:00"),
                    visit("KL01", "13:00", "16:00"),
                    visit("KL02", "08:00", "12:00"),
                    visit("KL02", "13:00", "16:00"))))
        .isEqualTo(200);
  }

  @Test
  void sumsVisitsOfSameCarBelowCap() {
    assertThat(
            service.totalCollected(
                List.of(visit("KL01", "09:00", "10:00"), visit("KL01", "11:00", "12:01"))))
        .isEqualTo(60);
  }

  @Test
  void treatsCarNumbersCaseAndWhitespaceInsensitively() {
    assertThat(
            service.totalCollected(
                List.of(visit("KL01", "08:00", "12:00"), visit(" kl01 ", "13:00", "16:00"))))
        .isEqualTo(100);
  }

  @Test
  void rejectsVisitWithExitBeforeEntry() {
    assertThatThrownBy(() -> service.totalCollected(List.of(visit("KL01", "10:00", "09:00"))))
        .isInstanceOf(InvalidVisitException.class);
  }

  private static ParkingVisit visit(String carNumber, String entry, String exit) {
    return new ParkingVisit(carNumber, LocalTime.parse(entry), LocalTime.parse(exit));
  }
}
