package com.ravi.job_tracker_backend.service;

import com.ravi.job_tracker_backend.dto.responsedto.JobApplicationResponseDto;
import com.ravi.job_tracker_backend.dto.requestdto.JobApplicationRequestDto;
import com.ravi.job_tracker_backend.entity.JobApplication;
import com.ravi.job_tracker_backend.entity.User;
import com.ravi.job_tracker_backend.exception.JobTrackerCustomException;
import com.ravi.job_tracker_backend.repository.JobApplicationRepository;
import com.ravi.job_tracker_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JobApplicationService {

    @Autowired
    JobApplicationRepository jobApplicationRepository;
    @Autowired
    UserRepository userRepository;

    // to insert new job application
    public JobApplicationResponseDto insertNewJob(
            JobApplicationRequestDto jobApplicationRequestDto, String email) {

        User user = userRepository.findByEmail(email).orElseThrow(()-> new JobTrackerCustomException(
                "Invalid User trying to add new job application", HttpStatus.FORBIDDEN
        ));
        JobApplication jobApplication = new JobApplication();
        jobApplication.setUser(user);
        jobApplication.setCompany_name(jobApplicationRequestDto.getCompany_name());
        jobApplication.setRole(jobApplicationRequestDto.getRole());
        jobApplication.setStatus(jobApplicationRequestDto.getStatus());
        jobApplication.setNotes(jobApplicationRequestDto.getNotes());
        // save into db
        JobApplication savedJobApplication = jobApplicationRepository.save(jobApplication);
        // convert to response dto
        JobApplicationResponseDto jobApplicationResponseDto = new JobApplicationResponseDto();
        jobApplicationResponseDto.setJid(savedJobApplication.getJid());
        jobApplicationResponseDto.setCompany_name(savedJobApplication.getCompany_name());
        jobApplicationResponseDto.setRole(savedJobApplication.getRole());
        jobApplicationResponseDto.setStatus(savedJobApplication.getStatus());
        jobApplicationResponseDto.setNotes(savedJobApplication.getNotes());
        jobApplicationResponseDto.setCreated_at(savedJobApplication.getCreated_at());

        return jobApplicationResponseDto;
    }

    // to fetch all job applications
    public List<JobApplicationResponseDto> fetchAllJobApplications(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new JobTrackerCustomException(
                "Invalid User trying to fetch job application", HttpStatus.FORBIDDEN
        ));
        List<JobApplication> jobApplications = jobApplicationRepository.findByUser(user);

        // convert to response dto
        List<JobApplicationResponseDto> jobApplicationResponseDto = new ArrayList<>();
        for (JobApplication jobApplication : jobApplications) {
            JobApplicationResponseDto responseDto = new JobApplicationResponseDto();
            responseDto.setJid(jobApplication.getJid());
            responseDto.setCompany_name(jobApplication.getCompany_name());
            responseDto.setRole(jobApplication.getRole());
            responseDto.setStatus(jobApplication.getStatus());
            responseDto.setNotes(jobApplication.getNotes());
            responseDto.setCreated_at(jobApplication.getCreated_at());

            jobApplicationResponseDto.add(responseDto);
        }
        return jobApplicationResponseDto;
    }

    // update job application method
    public JobApplicationResponseDto updateJobApplication(Long jid,
                                                          JobApplicationRequestDto jobApplicationRequestDto,
                                                          String email) {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new JobTrackerCustomException(
                "Invalid User trying to update job application", HttpStatus.FORBIDDEN));
        JobApplication jobApplication = jobApplicationRepository.findById(jid).orElseThrow(()->
               new JobTrackerCustomException("Invalid Application id", HttpStatus.NOT_FOUND));
        // check Authorized user or not
        if(!jobApplication.getUser().getUid().equals(user.getUid())){
            throw new JobTrackerCustomException("Unauthorized User", HttpStatus.UNAUTHORIZED);
        }
        jobApplication.setCompany_name(jobApplicationRequestDto.getCompany_name());
        jobApplication.setRole(jobApplicationRequestDto.getRole());
        jobApplication.setStatus(jobApplicationRequestDto.getStatus());
        jobApplication.setNotes(jobApplicationRequestDto.getNotes());
        JobApplication savedJobApplication = jobApplicationRepository.save(jobApplication);

        // convert to response dto
        JobApplicationResponseDto jobApplicationResponseDto = new JobApplicationResponseDto();
        jobApplicationResponseDto.setJid(savedJobApplication.getJid());
        jobApplicationResponseDto.setCompany_name(savedJobApplication.getCompany_name());
        jobApplicationResponseDto.setRole(savedJobApplication.getRole());
        jobApplicationResponseDto.setStatus(savedJobApplication.getStatus());
        jobApplicationResponseDto.setNotes(savedJobApplication.getNotes());
        jobApplicationResponseDto.setCreated_at(savedJobApplication.getCreated_at());
        return jobApplicationResponseDto;
    }

    // delete existing job application
    public String deleteJobApplication(Long jid,  String email) {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new JobTrackerCustomException(
                "Invalid User trying to delete job application", HttpStatus.FORBIDDEN));
        JobApplication jobApplication = jobApplicationRepository.findById(jid).orElseThrow(()->
                new JobTrackerCustomException("Invalid Application id", HttpStatus.NOT_FOUND));
        // check Authorized user or not
        if(!jobApplication.getUser().getUid().equals(user.getUid())){
            throw new JobTrackerCustomException("Unauthorized User", HttpStatus.UNAUTHORIZED);
        }

        jobApplicationRepository.deleteById(jid);

        return "Successfully Deleted Job Application";
    }
}
