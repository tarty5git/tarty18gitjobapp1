package com.cth.sdm.service;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cth.sdm.model.entity.ApplicantProfile;
import com.cth.sdm.repository.ApplicantProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicantProfileService {

    private static final long PROFILE_ID = 1L;
    private static final long MAX_CV_SIZE = 10 * 1024 * 1024;

    private final ApplicantProfileRepository profileRepository;

    public ApplicantProfile getProfile() {
        return profileRepository.findById(PROFILE_ID)
                .orElseGet(() -> ApplicantProfile.builder().id(PROFILE_ID).build());
    }

    public ApplicantProfile saveProfile(String fullName, String email, String phone, String location,
            String linkedinUrl, String workAuthorization, MultipartFile cv) {
        validate(fullName, email, phone, location, linkedinUrl, workAuthorization);
        ApplicantProfile profile = getProfile();
        profile.setFullName(trim(fullName));
        profile.setEmail(trim(email));
        profile.setPhone(trim(phone));
        profile.setLocation(trim(location));
        profile.setLinkedinUrl(trim(linkedinUrl));
        profile.setWorkAuthorization(trim(workAuthorization));
        if (cv != null && !cv.isEmpty()) {
            if (cv.getSize() > MAX_CV_SIZE) {
                throw new IllegalArgumentException("CV must be 10 MB or smaller.");
            }
            String contentType = cv.getContentType();
            if (!"application/pdf".equalsIgnoreCase(contentType)
                    && !"application/msword".equalsIgnoreCase(contentType)
                    && !"application/vnd.openxmlformats-officedocument.wordprocessingml.document".equalsIgnoreCase(contentType)) {
                throw new IllegalArgumentException("CV must be a PDF or Word document.");
            }
            try {
                profile.setCvContent(cv.getBytes());
            } catch (IOException exception) {
                throw new IllegalArgumentException("Unable to read the uploaded CV.", exception);
            }
            profile.setCvFilename(cv.getOriginalFilename());
            profile.setCvContentType(contentType);
        }
        profile.setUpdatedAt(LocalDateTime.now());
        return profileRepository.save(profile);
    }

    private void validate(String fullName, String email, String phone, String location,
            String linkedinUrl, String workAuthorization) {
        if (isBlank(fullName) || fullName.length() > 150) {
            throw new IllegalArgumentException("Full name is required and must be 150 characters or fewer.");
        }
        if (isBlank(email) || email.length() > 320 || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("A valid email address is required.");
        }
        if (lengthOver(phone, 50) || lengthOver(location, 150)
                || lengthOver(linkedinUrl, 500) || lengthOver(workAuthorization, 150)) {
            throw new IllegalArgumentException("Applicant details exceed the allowed length.");
        }
    }

    private boolean lengthOver(String value, int maxLength) {
        return value != null && value.length() > maxLength;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}