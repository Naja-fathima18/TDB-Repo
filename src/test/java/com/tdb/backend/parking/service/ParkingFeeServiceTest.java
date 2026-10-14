package com.tdb.backend.parking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tdb.backend.parking.domain.InvalidVisitException;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ParkingFeeServiceTest {

  private final ParkingFeeService service = new ParkingFeeService();

  @ParameterizedTest(name = "{0}-{1} costs {2}")
  @CsvSource({"09:00, 09:30, 0", "09:00, 10:00, 20", "09:00, 10:01, 40", "08:00, 17:00, 100"})
  void chargesDocumentedExamples(String entry, String exit, int expectedFee) {
    assertThat(service.feeFor(LocalTime.parse(entry), LocalTime.parse(exit)))
        .isEqualTo(expectedFee);
  }

  @ParameterizedTest(name = "{0} minutes costs {1}")
  @CsvSource({
    "0, 0",
    "1, 0",
    "30, 0",
    "31, 20",
    "60, 20",
    "61, 40",
    "120, 40",
    "121, 60",
    "240, 80",
    "241, 100",
    "300, 100",
    "301, 100",
    "1439, 100"
  })
  void chargesTwentyPerStartedHourAfterFreeHalfHourCappedAtHundred(int minutes, int expectedFee) {
    LocalTime entry = LocalTime.MIDNIGHT;
    assertThat(service.feeFor(entry, entry.plusMinutes(minutes))).isEqualTo(expectedFee);
  }

  @Test
  void rejectsExitBeforeEntry() {
    assertThatThrownBy(() -> service.feeFor(LocalTime.of(10, 0), LocalTime.of(9, 59)))
        .isInstanceOf(InvalidVisitException.class);
  }
}
