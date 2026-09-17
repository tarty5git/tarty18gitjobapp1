package com.cth.sdm.controller;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cth.sdm.model.entity.JobApplication;
import com.cth.sdm.model.enums.ApplicationStatus;
import com.cth.sdm.repository.JobApplicationRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class JobApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Test
    @DisplayName("GET /api/v1/jobs should return list of job applications")
    void testGetAllJobs() throws Exception {
        jobApplicationRepository.save(JobApplication.builder()
                .jobTitle("Full Stack Developer")
                .company("Tech Innovations")
                .location("Remote")
                .jobUrl("https://www.linkedin.com/jobs/view/555123456")
                .status(ApplicationStatus.APPLIED)
                .matchScore(88.0)
                .appliedAt(LocalDateTime.now())
                .build());

        mockMvc.perform(get("/api/v1/jobs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].jobTitle").value("Full Stack Developer"))
                .andExpect(jsonPath("$[0].company").value("Tech Innovations"));
    }

    @Test
    @DisplayName("POST /api/v1/bot/start should return success status message")
    void testStartBotEndpoint() throws Exception {
        mockMvc.perform(post("/api/v1/bot/start"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/reports/export/csv should return CSV file content")
    void testExportCsvReportEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/reports/export/csv"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String contentType = result.getResponse().getContentType();
                    org.junit.jupiter.api.Assertions.assertTrue(contentType.contains("text/csv"));
                });
    }

    @Test
    @DisplayName("POST /api/v1/jobs should reject an invalid job payload")
    void testCreateJobRejectsInvalidPayload() throws Exception {
        mockMvc.perform(post("/api/v1/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"jobTitle\":\"\",\"company\":\"Acme\",\"jobUrl\":\"javascript:alert(1)\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/jobs/paged should reject unsafe page sizes")
    void testPagedJobsRejectsUnsafeSize() throws Exception {
        mockMvc.perform(get("/api/v1/jobs/paged").param("size", "101"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/v1/jobs should clear all job listings")
    void testDeleteAllJobs() throws Exception {
        jobApplicationRepository.save(JobApplication.builder()
                .jobTitle("Backend Developer")
                .company("Acme")
                .jobUrl("https://www.linkedin.com/jobs/view/777123456")
                .status(ApplicationStatus.DISCOVERED)
                .appliedAt(LocalDateTime.now())
                .build());

        mockMvc.perform(delete("/api/v1/jobs"))
                .andExpect(status().isNoContent());

        org.assertj.core.api.Assertions.assertThat(jobApplicationRepository.count()).isZero();
    }
}
