package com.cth.sdm.repository;

import com.cth.sdm.model.entity.JobApplication;
import com.cth.sdm.model.enums.ApplicationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("h2")
class JobApplicationRepositoryTest {

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Test
    @DisplayName("Should save and retrieve job application correctly")
    void testSaveAndFindJobApplication() {
        JobApplication application = JobApplication.builder()
                .jobTitle("Senior Java Developer")
                .company("Oracle Corp")
                .location("Remote")
                .jobUrl("https://www.linkedin.com/jobs/view/987654321")
                .status(ApplicationStatus.APPLIED)
                .matchScore(94.5)
                .appliedAt(LocalDateTime.now())
                .notes("Testing JPA Repository")
                .build();

        JobApplication saved = jobApplicationRepository.save(application);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getJobTitle()).isEqualTo("Senior Java Developer");
        assertThat(saved.getCompany()).isEqualTo("Oracle Corp");
        assertThat(jobApplicationRepository.existsByJobUrl("https://www.linkedin.com/jobs/view/987654321")).isTrue();
    }
}
