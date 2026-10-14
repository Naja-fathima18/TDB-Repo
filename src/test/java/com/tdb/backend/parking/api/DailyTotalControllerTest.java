package com.tdb.backend.parking.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tdb.backend.common.ApiExceptionHandler;
import com.tdb.backend.parking.service.DailyTotalService;
import com.tdb.backend.parking.service.ParkingFeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(DailyTotalController.class)
@Import({DailyTotalService.class, ParkingFeeService.class, ApiExceptionHandler.class})
class DailyTotalControllerTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void returnsTotalForDifferentCars() throws Exception {
    postDailyTotal(
            "{\"visits\":[{\"carNumber\":\"KL01\",\"entry\":\"09:00\",\"exit\":\"10:00\"},"
                + "{\"carNumber\":\"KL02\",\"entry\":\"09:00\",\"exit\":\"09:20\"}]}")
        .andExpect(status().isOk())
        .andExpect(content().json("{\"total\":20}", true));
  }

  @Test
  void capsSameCarAtHundredIgnoringCase() throws Exception {
    postDailyTotal(
            "{\"visits\":[{\"carNumber\":\"KL01\",\"entry\":\"08:00\",\"exit\":\"12:00\"},"
                + "{\"carNumber\":\"kl01\",\"entry\":\"13:00\",\"exit\":\"16:00\"}]}")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.total").value(100));
  }

  @Test
  void returnsZeroForEmptyVisits() throws Exception {
    postDailyTotal("{\"visits\":[]}")
        .andExpect(status().isOk())
        .andExpect(content().json("{\"total\":0}", true));
  }

  @Test
  void rejectsMissingVisits() throws Exception {
    postDailyTotal("{}")
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.errors.visits").isNotEmpty());
  }

  @Test
  void rejectsNullVisitElement() throws Exception {
    postDailyTotal("{\"visits\":[null]}")
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.errors['visits[0]']").isNotEmpty());
  }

  @ParameterizedTest
  @ValueSource(strings = {"", " "})
  void rejectsBlankCarNumber(String carNumber) throws Exception {
    postDailyTotal(
            "{\"visits\":[{\"carNumber\":\""
                + carNumber
                + "\",\"entry\":\"09:00\",\"exit\":\"10:00\"}]}")
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.errors['visits[0].carNumber']").isNotEmpty());
  }

  @ParameterizedTest
  @ValueSource(strings = {"24:00", "9:00", "09:60", "0900", ""})
  void rejectsMalformedTimeOnTheOffendingVisit(String time) throws Exception {
    postDailyTotal(
            "{\"visits\":[{\"carNumber\":\"KL01\",\"entry\":\"09:00\",\"exit\":\"10:00\"},"
                + "{\"carNumber\":\"KL02\",\"entry\":\""
                + time
                + "\",\"exit\":\"10:00\"}]}")
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.errors['visits[1].entry']").isNotEmpty())
        .andExpect(jsonPath("$.errors['visits[0].entry']").doesNotExist());
  }

  @Test
  void rejectsExitBeforeEntry() throws Exception {
    postDailyTotal("{\"visits\":[{\"carNumber\":\"KL01\",\"entry\":\"10:00\",\"exit\":\"09:59\"}]}")
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.title").value("Invalid visit"));
  }

  private ResultActions postDailyTotal(String body) throws Exception {
    return mockMvc.perform(
        post("/api/parking/daily-total").contentType(MediaType.APPLICATION_JSON).content(body));
  }
}
