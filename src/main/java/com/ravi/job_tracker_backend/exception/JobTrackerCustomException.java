package com.ravi.job_tracker_backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class JobTrackerCustomException extends RuntimeException {
    private final HttpStatus status;
    public JobTrackerCustomException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
