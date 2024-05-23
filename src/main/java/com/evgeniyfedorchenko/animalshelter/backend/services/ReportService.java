package com.evgeniyfedorchenko.animalshelter.backend.services;

import com.evgeniyfedorchenko.animalshelter.backend.dto.ReportOutputDto;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ReportService {

    CompletableFuture<List<ReportOutputDto>> getUnverifiedReports(int limit);

    Optional<ReportOutputDto> getReportById(long id);

    boolean sendMessageAboutBadReport(long reportId);

    Optional<Pair<byte[], String>> getPhoto(Long id);
}
