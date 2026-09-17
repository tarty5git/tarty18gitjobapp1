package com.cth.sdm.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cth.sdm.model.entity.JobApplication;
import com.cth.sdm.repository.JobApplicationRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
@Tag(name = "Job Listing Management", description = "APIs for tracking, adding, and viewing job listings")
public class JobApplicationController {

    private final JobApplicationRepository jobApplicationRepository;

    @GetMapping
    @Operation(summary = "Get all job listings", description = "Retrieves all job listings ordered by application date")
    public ResponseEntity<List<JobApplication>> getAllJobs() {
        return ResponseEntity.ok(jobApplicationRepository.findAllByOrderByPostedAtDesc(PageRequest.of(0, 100)).getContent());
    }

    @GetMapping("/paged")
    @Operation(summary = "Get paginated job listings", description = "Retrieves paginated list of job listings")
    public ResponseEntity<Page<JobApplication>> getPagedJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page must be non-negative and size must be between 1 and 100.");
        }
        return ResponseEntity.ok(jobApplicationRepository.findAllByOrderByPostedAtDesc(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get job listing by ID", description = "Returns a single job listing record")
    public ResponseEntity<JobApplication> getJobById(@PathVariable Long id) {
        return jobApplicationRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Manually record a job listing", description = "Create a new job listing record in the database")
    public ResponseEntity<JobApplication> createJob(@Valid @RequestBody JobApplication jobApplication) {
        JobApplication saved = jobApplicationRepository.save(jobApplication);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping
    @Operation(summary = "Clear all job listings", description = "Deletes all stored job listing records so a new search can start fresh")
    public ResponseEntity<Void> deleteAllJobs() {
        jobApplicationRepository.deleteAllInBatch();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete job listing", description = "Delete job listing record by ID")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        if (!jobApplicationRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        jobApplicationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/viewed")
    @Operation(summary = "Mark job as viewed", description = "Records that the job link was opened")
    public ResponseEntity<Long> markJobViewed(@PathVariable Long id) {
        return jobApplicationRepository.findById(id)
                .map(job -> {
                    if (!job.isViewed()) {
                        job.setViewed(true);
                        jobApplicationRepository.save(job);
                    }
                    return ResponseEntity.ok(jobApplicationRepository.countByViewedTrue());
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
