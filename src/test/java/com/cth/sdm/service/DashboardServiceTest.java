package com.cth.sdm.service;

import com.cth.sdm.dto.DashboardStatsDto;
import com.cth.sdm.model.entity.JobApplication;
import com.cth.sdm.model.enums.ApplicationStatus;
import com.cth.sdm.repository.JobApplicationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    @DisplayName("Should return accurate dashboard statistics")
    void testGetDashboardStats() {
        JobApplication job = JobApplication.builder()
                .id(1L)
                .jobTitle("Spring Boot Engineer")
                .company("Acme Corp")
                .jobUrl("https://linkedin.com/jobs/view/123")
                .matchScore(90.0)
                .status(ApplicationStatus.APPLIED)
                .appliedAt(LocalDateTime.now())
                .build();

        when(jobApplicationRepository.count()).thenReturn(10L);
        when(jobApplicationRepository.countAppliedSince(any())).thenReturn(3L);
        when(jobApplicationRepository.countByViewedTrue()).thenReturn(2L);
        when(jobApplicationRepository.countByStatus(ApplicationStatus.OFFER_RECEIVED)).thenReturn(1L);
        when(jobApplicationRepository.countByStatus(ApplicationStatus.REJECTED)).thenReturn(0L);
        when(jobApplicationRepository.findAllByOrderByPostedAtDesc()).thenReturn(List.of(job));

        DashboardStatsDto stats = dashboardService.getDashboardStats(true, "Java");

        assertThat(stats.getTotalApplications()).isEqualTo(10L);
        assertThat(stats.getAppliedToday()).isEqualTo(3L);
        assertThat(stats.getJobsViewed()).isEqualTo(2L);
        assertThat(stats.getOffersReceived()).isEqualTo(1L);
        assertThat(stats.getAverageMatchScore()).isEqualTo(90.0);
        assertThat(stats.isBotRunning()).isTrue();
    }
}
