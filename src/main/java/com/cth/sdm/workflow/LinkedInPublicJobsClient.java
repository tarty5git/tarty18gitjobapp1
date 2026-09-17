package com.cth.sdm.workflow;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LinkedInPublicJobsClient {

    private static final String SEARCH_URL = "https://www.linkedin.com/jobs/search/?keywords=%s&location=%s&f_TPR=r%s%s";
    private static final String USER_AGENT = "Mozilla/5.0 (compatible; JobSearchBot/1.0; +https://www.linkedin.com/legal/user-agreement)";
    private static final Pattern EMAIL_PATTERN = Pattern.compile("\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}\\b", Pattern.CASE_INSENSITIVE);

    @Value("${bot.linkedin.request-timeout-ms:10000}")
    private int requestTimeoutMs;

    public List<LinkedInJobListing> search(String keywords, String country, String company, int maxResults,
                                           int maxJobAgeDays, boolean easyApplyOnly) throws IOException {
        String url = SEARCH_URL.formatted(encode(keywords), encode(country), maxJobAgeDays * 86400L,
                easyApplyOnly ? "&f_AL=true" : "");
        Document document = Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .timeout(requestTimeoutMs)
                .followRedirects(true)
                .get();

        List<LinkedInJobListing> listings = new ArrayList<>();
        for (Element result : document.select("ul.jobs-search__results-list > li")) {
            Element card = result.selectFirst("div.base-card");
            if (card == null) {
                continue;
            }
            Element link = card.selectFirst("a.base-card__full-link");
            Element title = card.selectFirst("h3.base-search-card__title");
            Element employer = card.selectFirst("h4.base-search-card__subtitle");
            Element jobLocation = card.selectFirst("span.job-search-card__location");
            if (link == null || title == null || employer == null) {
                continue;
            }

            String jobUrl = link.absUrl("href");
            if (jobUrl.isBlank()) {
                continue;
            }
                LinkedInJobListing listing = new LinkedInJobListing(
                    title.text().trim(),
                    employer.text().trim(),
                    jobLocation == null ? country : jobLocation.text().trim(),
                    normalizeUrl(jobUrl),
                    calculateMatchScore(title.text(), keywords),
                    parsePostedAt(card),
                    findPosterEmail(jobUrl),
                    card.text().toLowerCase(Locale.ROOT).contains("application submitted"));
                if (company != null && !company.isBlank()
                    && !listing.company().toLowerCase().contains(company.trim().toLowerCase())) {
                continue;
                }
                listings.add(listing);
            if (listings.size() >= maxResults) {
                break;
            }
        }
        return listings;
    }

    private String findPosterEmail(String jobUrl) {
        try {
            Document jobPage = Jsoup.connect(jobUrl)
                    .userAgent(USER_AGENT)
                    .timeout(requestTimeoutMs)
                    .followRedirects(true)
                    .get();
            Matcher matcher = EMAIL_PATTERN.matcher(jobPage.text());
            return matcher.find() ? matcher.group() : null;
        } catch (IOException e) {
            return null;
        }
    }

    private LocalDateTime parsePostedAt(Element card) {
        Element dateElement = card.selectFirst("time.job-search-card__listdate, time");
        if (dateElement == null) return null;

        String machineDate = dateElement.attr("datetime").trim();
        if (!machineDate.isBlank()) {
            try {
                return LocalDateTime.parse(machineDate, DateTimeFormatter.ISO_DATE_TIME);
            } catch (DateTimeParseException ignored) {
                try {
                    return LocalDate.parse(machineDate, DateTimeFormatter.ISO_DATE).atStartOfDay();
                } catch (DateTimeParseException ignoredDate) {
                    // Use the visible relative date when the machine-readable value is invalid.
                }
            }
        }

        String visibleDate = dateElement.text().trim().toLowerCase();
        LocalDateTime now = LocalDateTime.now();
        if (visibleDate.contains("just now") || visibleDate.equals("today")) return now;
        var matcher = java.util.regex.Pattern.compile("(\\d+)\\+?\\s+(hour|day|week|month)s?\\s+ago")
                .matcher(visibleDate);
        if (!matcher.find()) return null;
        long amount = Long.parseLong(matcher.group(1));
        return switch (matcher.group(2)) {
            case "hour" -> now.minusHours(amount);
            case "day" -> now.minusDays(amount);
            case "week" -> now.minusWeeks(amount);
            case "month" -> now.minusMonths(amount);
            default -> null;
        };
    }

    private double calculateMatchScore(String title, String keywords) {
        List<String> terms = Arrays.stream(keywords.split(","))
                .map(String::trim)
                .filter(term -> !term.isBlank())
                .toList();
        long matchingTerms = terms.stream()
                .filter(term -> title.toLowerCase().contains(term.toLowerCase()))
                .count();
        return terms.isEmpty() ? 0.0 : Math.round((matchingTerms * 100.0 / terms.size()) * 100.0) / 100.0;
    }

    private String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    private String normalizeUrl(String url) {
        int trackingParameter = url.indexOf('?');
        return trackingParameter >= 0 ? url.substring(0, trackingParameter) : url;
    }
}