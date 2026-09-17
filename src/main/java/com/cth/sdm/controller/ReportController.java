package com.cth.sdm.controller;

import com.cth.sdm.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Automated Reports", description = "APIs for exporting automatic reports in CSV format")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/export/csv")
    @Operation(summary = "Export Job Listings CSV Report", description = "Generates and downloads a CSV report of all job listings")
    public ResponseEntity<byte[]> exportCsvReport() {
        byte[] csvData = reportService.generateJobApplicationCsvReport();
        String filename = "job_applications_" + LocalDate.now() + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csvData);
    }
}
