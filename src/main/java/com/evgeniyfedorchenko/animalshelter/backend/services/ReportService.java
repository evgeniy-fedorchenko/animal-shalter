package com.evgeniyfedorchenko.animalshelter.backend.services;

import com.evgeniyfedorchenko.animalshelter.backend.dto.ReportOutputDto;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Report;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.report.ReportPart;
import jakarta.annotation.Nullable;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ReportService {

    List<ReportOutputDto> getUnverifiedReports(int limit);

    Optional<ReportOutputDto> getReportById(long id);

    boolean sendMessageAboutBadReport(long reportId);

    Optional<Pair<byte[], MediaType>> getPhoto(Long id);

    List<ReportPart> checkUnsentReportParts(String chatId);

    void acceptReportPart(ReportPart specialBehaviorId, byte[] reportPartData, String chatId, @Nullable MediaType mediaType);

    default void linkIfFalse(Adopter adopter, Report report, boolean condition) {
        if (!condition) {
            adopter.addReport(report);
//            report.setSendingAt(Instant.now());
        }
    }
}
