package com.ravi.job_tracker_backend.dto.requestdto;

import lombok.Data;

@Data
public class SigninRequestDto {
    private String email;
    private String password;
}
