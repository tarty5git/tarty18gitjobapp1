package com.cth.sdm.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cth.sdm.service.ApplicantProfileService;
import com.cth.sdm.service.BotConfigurationService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/config")
@RequiredArgsConstructor
public class BotConfigurationController {

    private final BotConfigurationService configurationService;
    private final ApplicantProfileService applicantProfileService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getConfiguration() {
        return ResponseEntity.ok(Map.of(
                "maxJobAgeDays", configurationService.getMaxJobAgeDays(),
                "country", configurationService.getCountry(),
                "company", configurationService.getCompany(),
                "easyApplyOnly", configurationService.isEasyApplyOnly(),
                "searchKeywords", configurationService.getSearchKeywords()));
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> updateConfiguration(@Valid @RequestBody ConfigurationRequest request) {
        int days = configurationService.saveMaxJobAgeDays(request.maxJobAgeDays());
        configurationService.saveSearchPreferences(request.country(), request.company(), request.easyApplyOnly(), request.searchKeywords());
        return ResponseEntity.ok(Map.of("maxJobAgeDays", days,
                "country", request.country().trim(), "company", request.company() == null ? "" : request.company().trim(),
            "easyApplyOnly", request.easyApplyOnly(), "searchKeywords", request.searchKeywords().trim()));
    }

        @GetMapping("/applicant")
        public ResponseEntity<Map<String, Object>> getApplicantProfile() {
        var profile = applicantProfileService.getProfile();
        return ResponseEntity.ok(Map.of(
            "fullName", profile.getFullName() == null ? "" : profile.getFullName(),
            "email", profile.getEmail() == null ? "" : profile.getEmail(),
            "phone", profile.getPhone() == null ? "" : profile.getPhone(),
            "location", profile.getLocation() == null ? "" : profile.getLocation(),
            "linkedinUrl", profile.getLinkedinUrl() == null ? "" : profile.getLinkedinUrl(),
            "workAuthorization", profile.getWorkAuthorization() == null ? "" : profile.getWorkAuthorization(),
            "cvFilename", profile.getCvFilename() == null ? "" : profile.getCvFilename()));
        }

        @PostMapping(value = "/applicant", consumes = "multipart/form-data")
        public ResponseEntity<Map<String, Object>> updateApplicantProfile(
            @RequestParam String fullName, @RequestParam String email,
            @RequestParam(required = false, defaultValue = "") String phone,
            @RequestParam(required = false, defaultValue = "") String location,
            @RequestParam(required = false, defaultValue = "") String linkedinUrl,
            @RequestParam(required = false, defaultValue = "") String workAuthorization,
            @RequestPart(required = false) MultipartFile cv) {
        var profile = applicantProfileService.saveProfile(fullName, email, phone, location,
            linkedinUrl, workAuthorization, cv);
        return ResponseEntity.ok(Map.of("message", "Applicant profile saved.",
            "cvFilename", profile.getCvFilename() == null ? "" : profile.getCvFilename()));
        }

    public record ConfigurationRequest(
            @Min(1) @Max(365) int maxJobAgeDays,
            @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max = 100) String country,
            @jakarta.validation.constraints.Size(max = 150) String company,
            boolean easyApplyOnly,
            @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max = 500) String searchKeywords) {
    }
}