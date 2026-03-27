package com.ravi.job_tracker_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "JobApplication")
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "job_id")
    @SequenceGenerator(name = "job_id", sequenceName = "db_seq_job", initialValue = 1, allocationSize = 5)
    private Long jid;
    @Column(nullable = false)
    private String company_name;
    @Column(nullable = false)
    private String role;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;
    @Column(nullable = true)
    private String notes;
    @CreationTimestamp
    private Timestamp created_at;
    @UpdateTimestamp
    private Timestamp updated_at;
    @ManyToOne
    @JoinColumn(name = "user_id_fk")
    private User user;
}
