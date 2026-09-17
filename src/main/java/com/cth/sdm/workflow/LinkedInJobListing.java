package com.cth.sdm.workflow;

import java.time.LocalDateTime;

public record LinkedInJobListing(
        String title,
        String company,
        String location,
        String url,
        double matchScore,
        LocalDateTime postedAt,
        String posterEmail,
        boolean applicationSubmitted) {

        public LinkedInJobListing(String title, String company, String location, String url,
                                                          double matchScore, LocalDateTime postedAt, String posterEmail) {
                this(title, company, location, url, matchScore, postedAt, posterEmail, false);
        }
}