package com.tdb.backend.common;

import com.tdb.backend.parking.domain.InvalidVisitException;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidationFailure(MethodArgumentNotValidException exception) {
    ProblemDetail problem =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Request validation failed");
    problem.setTitle("Invalid request");
    problem.setProperty("errors", fieldErrors(exception));
    return problem;
  }

  @ExceptionHandler(InvalidVisitException.class)
  public ProblemDetail handleInvalidVisit(InvalidVisitException exception) {
    ProblemDetail problem =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    problem.setTitle("Invalid visit");
    return problem;
  }

  private static Map<String, String> fieldErrors(MethodArgumentNotValidException exception) {
    Map<String, String> errors = new TreeMap<>();
    for (FieldError error : exception.getBindingResult().getFieldErrors()) {
      errors.merge(
          error.getField(),
          String.valueOf(error.getDefaultMessage()),
          (first, second) -> first + "; " + second);
    }
    return errors;
  }
}
