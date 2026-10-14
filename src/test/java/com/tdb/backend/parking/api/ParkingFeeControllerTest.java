package com.tdb.backend.parking.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tdb.backend.common.ApiExceptionHandler;
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

@WebMvcTest(ParkingFeeController.class)
@Import({ParkingFeeService.class, ApiExceptionHandler.class})
class ParkingFeeControllerTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void returnsFeeForVisit() throws Exception {
    postFee("{\"entry\":\"09:00\",\"exit\":\"10:01\"}")
        .andExpect(status().isOk())
        .andExpect(content().json("{\"fee\":40}", true));
  }

  @Test
  void returnsZeroFeeWhenExitEqualsEntry() throws Exception {
    postFee("{\"entry\":\"09:00\",\"exit\":\"09:00\"}")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.fee").value(0));
  }

  @ParameterizedTest
  @ValueSource(strings = {"24:00", "9:00", "", " ", "09:60", "0900", "09:00:00"})
  void rejectsMalformedEntryWithFieldError(String entry) throws Exception {
    postFee("{\"entry\":\"" + entry + "\",\"exit\":\"10:00\"}")
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.errors.entry").isNotEmpty())
        .andExpect(jsonPath("$.errors.exit").doesNotExist());
  }

  @Test
  void rejectsMissingTimesWithFieldErrors() throws Exception {
    postFee("{}")
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.errors.entry").isNotEmpty())
        .andExpect(jsonPath("$.errors.exit").isNotEmpty());
  }

  @Test
  void rejectsMalformedExitWithFieldError() throws Exception {
    postFee("{\"entry\":\"09:00\",\"exit\":\"24:00\"}")
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors.exit").isNotEmpty())
        .andExpect(jsonPath("$.errors.entry").doesNotExist());
  }

  @Test
  void rejectsExitBeforeEntry() throws Exception {
    postFee("{\"entry\":\"10:00\",\"exit\":\"09:59\"}")
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.title").value("Invalid visit"))
        .andExpect(jsonPath("$.detail").value("Exit time must not be before entry time"));
  }

  private ResultActions postFee(String body) throws Exception {
    return mockMvc.perform(
        post("/api/parking/fee").contentType(MediaType.APPLICATION_JSON).content(body));
  }
}
