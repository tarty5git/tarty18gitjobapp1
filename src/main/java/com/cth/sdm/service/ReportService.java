package com.cth.sdm.service;

import com.cth.sdm.model.entity.JobApplication;
import com.cth.sdm.repository.JobApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final JobApplicationRepository jobApplicationRepository;

    public byte[] generateJobApplicationCsvReport() {
        List<JobApplication> applications = jobApplicationRepository.findAll();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (PrintWriter writer = new PrintWriter(out, true, StandardCharsets.UTF_8)) {
            writer.println("ID,Job Title,Company,Location,Job URL,Status,Match Score %,Date Posted,Notes");

            for (JobApplication app : applications) {
                writer.printf("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%.2f,\"%s\",\"%s\"%n",
                        app.getId(),
                        escapeCsv(app.getJobTitle()),
                        escapeCsv(app.getCompany()),
                        escapeCsv(app.getLocation()),
                        escapeCsv(app.getJobUrl()),
                        app.getStatus().name(),
                        app.getMatchScore() != null ? app.getMatchScore() : 0.0,
                        app.getPostedAt() != null ? app.getPostedAt().toString() : "",
                        escapeCsv(app.getNotes())
                );
            }
        }

        return out.toByteArray();
    }

    private String escapeCsv(String input) {
        if (input == null) return "";
        return input.replace("\"", "\"\"");
    }
}
