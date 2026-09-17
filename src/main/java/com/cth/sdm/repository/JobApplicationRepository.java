package com.cth.sdm.repository;

import com.cth.sdm.model.entity.JobApplication;
import com.cth.sdm.model.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findByStatus(ApplicationStatus status);

    Page<JobApplication> findAllByOrderByPostedAtDesc(Pageable pageable);

    List<JobApplication> findAllByOrderByPostedAtDesc();

    long countByStatus(ApplicationStatus status);

    long countByViewedTrue();

    @Query("SELECT COUNT(j) FROM JobApplication j WHERE j.appliedAt >= :since")
    long countAppliedSince(LocalDateTime since);

    boolean existsByJobUrl(String jobUrl);
}
