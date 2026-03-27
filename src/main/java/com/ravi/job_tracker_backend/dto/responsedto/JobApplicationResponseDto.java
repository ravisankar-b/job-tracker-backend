package com.ravi.job_tracker_backend.dto.responsedto;

import com.ravi.job_tracker_backend.entity.ApplicationStatus;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class JobApplicationResponseDto {

    private Long jid;
    private String company_name;
    private String role;
    private ApplicationStatus status;
    private String notes;
    private Timestamp created_at;
}
