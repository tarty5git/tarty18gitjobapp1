package com.cth.sdm.workflow;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.cth.sdm.dto.BotStatusDto;
import com.cth.sdm.model.entity.BotExecutionLog;
import com.cth.sdm.model.entity.JobApplication;
import com.cth.sdm.model.enums.ApplicationStatus;
import com.cth.sdm.repository.BotExecutionLogRepository;
import com.cth.sdm.repository.JobApplicationRepository;
import com.cth.sdm.service.BotConfigurationService;
import com.cth.sdm.service.DashboardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LinkedInBotService {

    private final JobApplicationRepository jobApplicationRepository;
    private final BotExecutionLogRepository botExecutionLogRepository;
    private final DashboardService dashboardService;
    private final SimpMessagingTemplate messagingTemplate;
    private final LinkedInPublicJobsClient jobsClient;
    private final BotConfigurationService configurationService;

    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicBoolean cycleRunning = new AtomicBoolean(false);

    @Value("${bot.default-target-location:Remote}")
    private String targetLocation;

    @Value("${bot.max-results-per-cycle:10}")
    private int maxResultsPerCycle;

    public BotStatusDto getStatus() {
        String searchKeywords = configurationService.getSearchKeywords();
        return BotStatusDto.builder()
                .running(isRunning.get())
                .statusMessage(isRunning.get() ? "Bot is active and discovering jobs..." : "Bot is idle.")
                .totalProcessed(jobApplicationRepository.count())
                .currentKeyword(searchKeywords)
                .build();
    }

    public synchronized String startBot() {
        if (isRunning.get()) {
            return "Already running.";
        }
        isRunning.set(true);
        logAndNotify("INFO", "Job discovery bot started.");
        triggerAsyncJobApplicationCycle();
        return "Started successfully.";
    }

    public synchronized String stopBot() {
        if (!isRunning.get()) {
            return "Not running.";
        }
        isRunning.set(false);
        logAndNotify("INFO", "Job discovery bot stopped.");
        return "Stopped successfully.";
    }

    @Scheduled(fixedDelayString = "${bot.auto-apply-interval-ms:60000}")
    public void scheduledAutoApplyCycle() {
        if (isRunning.get()) {
            triggerAsyncJobApplicationCycle();
        }
    }

    public CompletableFuture<Void> triggerAsyncJobApplicationCycle() {
        return CompletableFuture.runAsync(() -> {
            if (!cycleRunning.compareAndSet(false, true)) return;
            try {
                int maxJobAgeDays = configurationService.getMaxJobAgeDays();
                String targetKeywords = configurationService.getSearchKeywords();
                String country = configurationService.getCountry();
                String company = configurationService.getCompany();
                boolean easyApplyOnly = configurationService.isEasyApplyOnly();
                logAndNotify("INFO", "Searching jobs in " + country + " posted within " + maxJobAgeDays
                    + " days" + (company.isBlank() ? "" : " at " + company)
                    + (easyApplyOnly ? " with Easy Apply" : ""));
                List<LinkedInJobListing> listings = jobsClient.search(targetKeywords, country, company,
                    maxResultsPerCycle, maxJobAgeDays, easyApplyOnly);
                int saved = 0;
                for (LinkedInJobListing listing : listings) {
                    if (jobApplicationRepository.existsByJobUrl(listing.url())) continue;
                    jobApplicationRepository.save(JobApplication.builder()
                            .jobTitle(listing.title())
                            .company(listing.company())
                            .location(listing.location())
                            .jobUrl(listing.url())
                            .posterEmail(listing.posterEmail())
                            .status(listing.applicationSubmitted()
                                ? ApplicationStatus.APPLIED
                                : ApplicationStatus.DISCOVERED)
                            .matchScore(listing.matchScore())
                            .postedAt(listing.postedAt())
                            .appliedAt(LocalDateTime.now())
                            .notes("Discovered from public job search. Review and apply manually.")
                            .build());
                    saved++;
                }
                logAndNotify("INFO", "Job search found " + listings.size() + " jobs; saved " + saved + " new jobs.");
                broadcastDashboardUpdate();
            } catch (Exception e) {
                logAndNotify("ERROR", "Error during bot execution cycle: " + e.getMessage());
            } finally {
                cycleRunning.set(false);
            }
        });
    }

    private void logAndNotify(String level, String message) {
        log.info("[{}] {}", level, message);
        BotExecutionLog botLog = BotExecutionLog.builder()
                .logLevel(level)
                .message(message)
                .build();
        botExecutionLogRepository.save(botLog);
        broadcastDashboardUpdate();
    }

    public void broadcastDashboardUpdate() {
        try {
            var stats = dashboardService.getDashboardStats(isRunning.get(), configurationService.getSearchKeywords());
            messagingTemplate.convertAndSend("/topic/dashboard-updates", stats);
        } catch (Exception e) {
            log.warn("WebSocket update broadcast warning: {}", e.getMessage());
        }
    }
}
