package com.cth.sdm.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "applicant_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicantProfile {

    @Id
    private Long id;

    @Column(name = "full_name", length = 150)
    private String fullName;

    @Column(name = "email", length = 320)
    private String email;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "location", length = 150)
    private String location;

    @Column(name = "linkedin_url", length = 500)
    private String linkedinUrl;

    @Column(name = "work_authorization", length = 150)
    private String workAuthorization;

    @Column(name = "cv_filename", length = 255)
    private String cvFilename;

    @Column(name = "cv_content_type", length = 100)
    private String cvContentType;

    @Lob
    @Column(name = "cv_content")
    private byte[] cvContent;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}