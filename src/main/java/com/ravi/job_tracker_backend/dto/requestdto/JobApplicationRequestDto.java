package com.ravi.job_tracker_backend.dto.requestdto;

import com.ravi.job_tracker_backend.entity.ApplicationStatus;
import lombok.Data;

@Data
public class JobApplicationRequestDto {
    private String company_name;
    private String role;
    private ApplicationStatus status;
    private String notes;
}
