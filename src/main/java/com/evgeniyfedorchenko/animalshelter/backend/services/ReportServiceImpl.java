package com.evgeniyfedorchenko.animalshelter.backend.services;

import com.evgeniyfedorchenko.animalshelter.backend.dto.ReportOutputDto;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Report;
import com.evgeniyfedorchenko.animalshelter.backend.mappers.ReportMapper;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.ReportRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@AllArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final TelegramService telegramService;
    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;

    @Async
    @Override
    public CompletableFuture<List<ReportOutputDto>> getUnverifiedReports(int limit) {

        CompletableFuture<List<ReportOutputDto>> futureList = CompletableFuture.supplyAsync(() -> 
                reportRepository.findOldestUnviewedReports(PageRequest.of(0, limit)).stream()
                .map(reportMapper::toDto)
                .toList());
      
        futureList.thenAcceptAsync(list -> {
            List<Long> idsForUpdate = list.stream().map(ReportOutputDto::getId).toList();
            reportRepository.updateReportsViewedStatus(idsForUpdate);
        });
        return futureList;
    }

    @Override
    public Optional<ReportOutputDto> getReportById(long id) {
        return reportRepository.findById(id)
                .map(reportMapper::toDto);
    }

    @Override
    public boolean sendMessageAboutBadReport(long reportId) {
        Optional<Report> reportOpt = reportRepository.findById(reportId);
        if (reportOpt.isEmpty()) {
            return false;
        }

        long adopterChatId = reportOpt.get().getAdopter().getChatId();
        String message = "Bad report";  // todo Заменить на подходящий текст

        return telegramService.sendMessage(adopterChatId, message);
    }

    @Override
    public Optional<Pair<byte[], MediaType>> getPhoto(Long id) {
        return reportRepository.findById(id)
                .map(report -> Pair.of(report.getPhotoData(), MediaType.parseMediaType(report.getMediaType())));
    }
}
