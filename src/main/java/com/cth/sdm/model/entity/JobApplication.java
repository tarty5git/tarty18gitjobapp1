package com.cth.sdm.model.entity;

import java.time.LocalDateTime;

import com.cth.sdm.model.enums.ApplicationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "job_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_title", nullable = false)
    @NotBlank
    @Size(max = 255)
    private String jobTitle;

    @Column(name = "company", nullable = false)
    @NotBlank
    @Size(max = 255)
    private String company;

    @Column(name = "location")
    @Size(max = 255)
    private String location;

    @Column(name = "job_url", nullable = false, length = 1000)
    @NotBlank
    @Size(max = 1000)
    @Pattern(regexp = "https?://.+", message = "Job URL must use HTTP or HTTPS")
    private String jobUrl;

    @Column(name = "poster_email", length = 320)
    @Size(max = 320)
    private String posterEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @NotNull
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    @Column(name = "match_score")
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private Double matchScore;

    @Column(name = "applied_at", nullable = false, updatable = false)
    private LocalDateTime appliedAt;

    @Column(name = "posted_at")
    private LocalDateTime postedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "viewed", nullable = false)
    @Builder.Default
    private boolean viewed = false;

    @Column(name = "notes", length = 2000)
    @Size(max = 2000)
    private String notes;

    @PrePersist
    protected void onCreate() {
        if (appliedAt == null) {
            appliedAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
