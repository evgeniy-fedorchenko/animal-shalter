package com.evgeniyfedorchenko.animalshelter.backend.services;

import com.evgeniyfedorchenko.animalshelter.backend.dto.ReportOutputDto;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks.report.SendingReportPart;
import jakarta.annotation.Nullable;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ReportService {

    CompletableFuture<List<ReportOutputDto>> getUnverifiedReports(int limit);

    Optional<ReportOutputDto> getReportById(long id);

    boolean sendMessageAboutBadReport(long reportId);

    Optional<Pair<byte[], MediaType>> getPhoto(Long id);

    List<SendingReportPart> checkUnsentReportParts(Long chatId);

    void acceptReportPart(SendingReportPart specialBehaviorId, String text, Long chatId, @Nullable MediaType mediaType);

    void acceptPhoto(Pair<byte[], MediaType> photoDataPair, Long chatId);
}
