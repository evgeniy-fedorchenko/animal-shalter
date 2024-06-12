package com.evgeniyfedorchenko.animalshelter.backend.services;

import com.evgeniyfedorchenko.animalshelter.backend.dto.ReportOutputDto;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Report;
import com.evgeniyfedorchenko.animalshelter.backend.mappers.ReportMapper;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.AdopterRepository;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.ReportRepository;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks.report.SendingReportPart;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
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
    private final AdopterRepository adopterRepository;

    @Async
    @Override
    @Transactional
    public CompletableFuture<List<ReportOutputDto>> getUnverifiedReports(int limit) {

        CompletableFuture<List<ReportOutputDto>> futureList = CompletableFuture.supplyAsync(() ->
                reportRepository.findOldestUnviewedReports(PageRequest.of(0, limit)).stream()
                        .map(reportMapper::toDto)
                        .toList());

        futureList.thenAccept(list -> {
            List<Long> idsForUpdate = list.stream().map(ReportOutputDto::getId).toList();
            System.out.println(idsForUpdate);
            reportRepository.updateReportsVerifiedStatus(idsForUpdate);
        });
        return futureList;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReportOutputDto> getReportById(long id) {
        return reportRepository.findById(id)
                .map(reportMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public Optional<Pair<byte[], MediaType>> getPhoto(Long id) {
        return reportRepository.findById(id).map(report ->
                report.hasPhotoDataAndMediaType()
                        ? Pair.of(report.getPhotoData(), MediaType.parseMediaType(report.getMediaType()))
                        : null
        );
    }

    @Override
    public List<SendingReportPart> checkUnsentReportParts(Long chatId) {
        Report report = reportRepository.findNewestReportByAdopterChatId(chatId).orElseThrow();

        List<SendingReportPart> unsentReportParts = new ArrayList<>();

        if (report.getDiet() == null) {
            unsentReportParts.add(SendingReportPart.DIET);
        }
        if (report.getHealth() == null) {
            unsentReportParts.add(SendingReportPart.HEALTH);
        }
        if (report.getChangeBehavior() == null) {
            unsentReportParts.add(SendingReportPart.BEHAVIOR);
        }
        if (report.getPhotoData() == null) {
            unsentReportParts.add(SendingReportPart.PHOTO);
        }
        return unsentReportParts;
    }

    @Override
    public void acceptReportPart(SendingReportPart reportPart, String textOfReportPart, Long relatedAdopterChatId, @Nullable MediaType mediaType) {

        Optional<Report> reportOpt = reportRepository.findNewestReportByAdopterChatId(relatedAdopterChatId);
        Optional<Adopter> adopterOpt = adopterRepository.findByChatId(relatedAdopterChatId);

        Adopter adopter = adopterOpt.orElseThrow(() -> new EntityNotFoundException("Adopter of this report not found"));
        Report report = reportOpt.orElseGet(Report::new);

        switch (reportPart) {
            case DIET     -> report.setDiet(textOfReportPart);
            case HEALTH   -> report.setHealth(textOfReportPart);
            case BEHAVIOR -> report.setChangeBehavior(textOfReportPart);
            case PHOTO -> {
                report.setPhotoData(textOfReportPart.getBytes());
                if (mediaType != null) {
                    report.setMediaType(mediaType.getType());
                }
            }
        }

        if (!report.hasAdopter()) {
            adopter.addReport(report);
            report.setSendingAt(Instant.now());
        }
        adopterRepository.save(adopter);
        reportRepository.save(report);
    }


    @Override
    public void acceptPhoto(Pair<byte[], MediaType> photoDataPair, Long chatId) {

    }
}
