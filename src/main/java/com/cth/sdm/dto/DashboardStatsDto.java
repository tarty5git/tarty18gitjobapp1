package com.cth.sdm.dto;

import com.cth.sdm.model.entity.JobApplication;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsDto {

    private long totalApplications;
    private long appliedToday;
    private long jobsViewed;
    private long offersReceived;
    private long rejected;
    private double averageMatchScore;
    private boolean botRunning;
    private String currentKeyword;
    private List<JobApplication> recentApplications;
}
