package com.ravi.job_tracker_backend.dto.responsedto;

import com.ravi.job_tracker_backend.entity.RefreshToken;
import lombok.Data;

@Data
public class AuthResponseDto {
    private String accessToken;
    private String refreshToken;
    private String username;
    private String email;
}
