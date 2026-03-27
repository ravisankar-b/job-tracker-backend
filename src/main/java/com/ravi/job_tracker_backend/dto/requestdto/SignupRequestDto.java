package com.ravi.job_tracker_backend.dto.requestdto;

import lombok.Data;

@Data
public class SignupRequestDto {
    private String username;
    private String password;
    private String email;

}
