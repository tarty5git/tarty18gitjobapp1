package com.cth.sdm.service;

import com.cth.sdm.model.entity.BotConfig;
import com.cth.sdm.repository.BotConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotConfigurationService {

    public static final String MAX_JOB_AGE_DAYS = "maxJobAgeDays";
    public static final String COUNTRY = "country";
    public static final String COMPANY = "company";
    public static final String EASY_APPLY_ONLY = "easyApplyOnly";
    public static final String SEARCH_KEYWORDS = "searchKeywords";
    private static final int DEFAULT_MAX_JOB_AGE_DAYS = 7;
    private static final String DEFAULT_COUNTRY = "United States";
    private static final String DEFAULT_SEARCH_KEYWORDS = "Java Developer, Spring Boot Engineer, Full Stack Developer";

    private final BotConfigRepository botConfigRepository;

    public int getMaxJobAgeDays() {
        return botConfigRepository.findById(MAX_JOB_AGE_DAYS)
                .map(BotConfig::getConfigValue)
                .map(this::parseDays)
                .orElse(DEFAULT_MAX_JOB_AGE_DAYS);
    }

    public int saveMaxJobAgeDays(int days) {
        if (days < 1 || days > 365) {
            throw new IllegalArgumentException("Job age must be between 1 and 365 days.");
        }
        botConfigRepository.save(BotConfig.builder()
                .configKey(MAX_JOB_AGE_DAYS)
                .configValue(Integer.toString(days))
                .build());
        return days;
    }

    public String getCountry() {
        return getValue(COUNTRY, DEFAULT_COUNTRY);
    }

    public String getCompany() {
        return getValue(COMPANY, "");
    }

    public boolean isEasyApplyOnly() {
        return Boolean.parseBoolean(getValue(EASY_APPLY_ONLY, "false"));
    }

    public String getSearchKeywords() {
        return getValue(SEARCH_KEYWORDS, DEFAULT_SEARCH_KEYWORDS);
    }

    public void saveSearchPreferences(String country, String company, boolean easyApplyOnly, String searchKeywords) {
        if (country == null || country.isBlank() || country.length() > 100) {
            throw new IllegalArgumentException("Country is required and must be 100 characters or fewer.");
        }
        if (searchKeywords == null || searchKeywords.isBlank() || searchKeywords.length() > 500) {
            throw new IllegalArgumentException("Search keywords are required and must be 500 characters or fewer.");
        }
        saveValue(COUNTRY, country.trim());
        saveValue(COMPANY, company == null ? "" : company.trim());
        saveValue(EASY_APPLY_ONLY, Boolean.toString(easyApplyOnly));
        saveValue(SEARCH_KEYWORDS, searchKeywords.trim());
    }

    private String getValue(String key, String defaultValue) {
        return botConfigRepository.findById(key)
                .map(BotConfig::getConfigValue)
                .orElse(defaultValue);
    }

    private void saveValue(String key, String value) {
        botConfigRepository.save(BotConfig.builder().configKey(key).configValue(value).build());
    }

    private int parseDays(String value) {
        try {
            int days = Integer.parseInt(value);
            return days >= 1 && days <= 365 ? days : DEFAULT_MAX_JOB_AGE_DAYS;
        } catch (NumberFormatException exception) {
            return DEFAULT_MAX_JOB_AGE_DAYS;
        }
    }
}