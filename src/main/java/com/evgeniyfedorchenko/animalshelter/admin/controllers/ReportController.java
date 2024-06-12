package com.evgeniyfedorchenko.animalshelter.admin.controllers;

import com.evgeniyfedorchenko.animalshelter.admin.annotations.documentation.report.GetPhotoDocumentation;
import com.evgeniyfedorchenko.animalshelter.admin.annotations.documentation.report.GetReportByIdDocumentation;
import com.evgeniyfedorchenko.animalshelter.admin.annotations.documentation.report.GetUnverifiedReportsDocumentation;
import com.evgeniyfedorchenko.animalshelter.admin.annotations.documentation.report.SendMessageAboutBadReportDocumentation;
import com.evgeniyfedorchenko.animalshelter.backend.dto.ReportOutputDto;
import com.evgeniyfedorchenko.animalshelter.backend.services.ReportService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Tag(name = "Reports", description = "Controller for work with reports: receiving, analyzing, and sending warnings to adopters about the low-quality of reports")
@Validated
@RestController
@RequestMapping(path = ReportController.BASE_REPORT_URI)
@AllArgsConstructor
public class ReportController {

    private final ReportService reportService;
    public static final String BASE_REPORT_URI = "/reports";

    @GetUnverifiedReportsDocumentation
    @GetMapping
    public CompletableFuture<List<ReportOutputDto>> getUnverifiedReports(
            @Positive(message = "Limit of reports must be positive")
            @Parameter(description = "The requested number of reports to verify them")
            @RequestParam(required = false, defaultValue = "10") int limit) {

        return reportService.getUnverifiedReports(limit);
    }

    @GetReportByIdDocumentation
    @GetMapping("/{id}")
    public ResponseEntity<ReportOutputDto> getReportById(
            @Positive(message = "Report's id must be positive")
            @Parameter(description = "Id of the requested report", example = "1")
            @PathVariable long id) {

        return ResponseEntity.of(reportService.getReportById(id));
    }

    @SendMessageAboutBadReportDocumentation
    @GetMapping(path = "/send-warning")
    public ResponseEntity<Void> sendMessageAboutBadReport(
            @Positive(message = "Report's id must be positive")
            @Parameter(description = "Id of the  low-quality report", example = "1")
            @RequestParam long reportId) {
        return reportService.sendMessageAboutBadReport(reportId)
                ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().build();
    }

    @GetPhotoDocumentation
    @GetMapping(path = "/{id}/photo")
    public ResponseEntity<byte[]> getPhoto(@Positive(message = "Report's id must be positive")
                                           @Parameter(description = "Report's id of the requested photo", example = "1")
                                           @PathVariable Long id) {
        return reportService.getPhoto(id)
                .map(report -> ResponseEntity.status(HttpStatus.OK)
                        .contentLength(report.getFirst().length)
                        .contentType(report.getSecond())
                        .body(report.getFirst()))

                .orElseGet(() -> ResponseEntity.of(Optional.empty()));
    }
}
