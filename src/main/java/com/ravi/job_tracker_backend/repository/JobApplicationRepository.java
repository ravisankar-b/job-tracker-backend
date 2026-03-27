package com.ravi.job_tracker_backend.repository;

import com.ravi.job_tracker_backend.entity.JobApplication;
import com.ravi.job_tracker_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    // to fetch all applications using derived method
    List<JobApplication> findByUser(User user);
}
