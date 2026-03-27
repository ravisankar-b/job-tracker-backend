package com.ravi.job_tracker_backend.exception;

import com.ravi.job_tracker_backend.dto.responsedto.ErrorResponseDto;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptinHandler {

    // to handle exceptions from User, Job Application, Refresh Token Service layers
    @ExceptionHandler(JobTrackerCustomException.class)
    public ResponseEntity<?> handleJobTrackerCustomException(JobTrackerCustomException ex) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                ex.getStatus().value(), ex.getMessage()
        );
        return new ResponseEntity<>(errorResponseDto,  ex.getStatus());
    }

    // To handle Database Connection Failures, crashes
    @ExceptionHandler(CannotGetJdbcConnectionException.class)
    public ResponseEntity<?> handleCannotGetJdbcConnectionException(CannotGetJdbcConnectionException ex) {
        return  new ResponseEntity<>(
                new ErrorResponseDto(503, "Database is temporarily " +
                        "down. Please try again later."), HttpStatus.SERVICE_UNAVAILABLE);
    }

    // To handle Integrity errors like Data Conflicts (SQL Constraint errors like duplicate emails)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return new ResponseEntity<>(new ErrorResponseDto(
                409, "This operation conflicts with existing data."),
                HttpStatus.CONFLICT);
    }

    // Tho handle Remaining Uncaught Exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllUncaughtException(Exception ex) {
        ex.printStackTrace();
        return new ResponseEntity<>(
                new ErrorResponseDto(500, "An unexpected internal error occurred."),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
