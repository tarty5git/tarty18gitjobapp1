package com.cth.sdm.service;

import com.cth.sdm.dto.DashboardStatsDto;
import com.cth.sdm.model.enums.ApplicationStatus;
import com.cth.sdm.repository.JobApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final JobApplicationRepository jobApplicationRepository;

    public DashboardStatsDto getDashboardStats(boolean botRunning, String currentKeyword) {
        long total = jobApplicationRepository.count();
        long today = jobApplicationRepository.countAppliedSince(LocalDate.now().atStartOfDay());
        long viewed = jobApplicationRepository.countByViewedTrue();
        long offers = jobApplicationRepository.countByStatus(ApplicationStatus.OFFER_RECEIVED);
        long rejected = jobApplicationRepository.countByStatus(ApplicationStatus.REJECTED);
        var recent = jobApplicationRepository.findAllByOrderByPostedAtDesc();

        double avgScore = recent.isEmpty() ? 0.0 : recent.stream()
                .mapToDouble(j -> j.getMatchScore() != null ? j.getMatchScore() : 0.0)
                .average()
                .orElse(0.0);

        return DashboardStatsDto.builder()
                .totalApplications(total)
                .appliedToday(today)
                .jobsViewed(viewed)
                .offersReceived(offers)
                .rejected(rejected)
                .averageMatchScore(Math.round(avgScore * 100.0) / 100.0)
                .botRunning(botRunning)
                .currentKeyword(currentKeyword)
                .recentApplications(recent)
                .build();
    }
}
