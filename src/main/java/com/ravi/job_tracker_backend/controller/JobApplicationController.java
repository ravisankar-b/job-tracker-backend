package com.ravi.job_tracker_backend.controller;

import com.ravi.job_tracker_backend.dto.requestdto.JobApplicationRequestDto;
import com.ravi.job_tracker_backend.dto.responsedto.JobApplicationResponseDto;
import com.ravi.job_tracker_backend.service.JobApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/application")
public class JobApplicationController {
    @Autowired
    JobApplicationService jobApplicationService;

    // add job application
    @PostMapping("/addJob")
    public ResponseEntity<JobApplicationResponseDto> addJob(
            @RequestBody JobApplicationRequestDto jobApplicationRequestDto)
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.status(201).body(jobApplicationService.insertNewJob(jobApplicationRequestDto,
                email));
    }
    // get job applications
    @GetMapping("getJob")
    public ResponseEntity<List<JobApplicationResponseDto>> fetchJob()
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(jobApplicationService.fetchAllJobApplications(email));
    }
    // update job applications
    @PutMapping("/{jid}")
    public ResponseEntity<JobApplicationResponseDto> updateJob(@PathVariable Long jid,
                                                               @RequestBody JobApplicationRequestDto
                                                                       jobApplicationRequestDto)
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(jobApplicationService.updateJobApplication(jid,
                jobApplicationRequestDto, email));
    }
    // delete job application
    @DeleteMapping("/{jid}")
    public ResponseEntity<String> deleteJob(@PathVariable Long jid)
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(jobApplicationService.deleteJobApplication(jid, email));
    }
}
