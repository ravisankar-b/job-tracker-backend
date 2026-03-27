package com.ravi.job_tracker_backend.dto.responsedto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class ErrorResponseDto {

    private int status;
    private String message;
    private long timestamp;
    public ErrorResponseDto(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
}
