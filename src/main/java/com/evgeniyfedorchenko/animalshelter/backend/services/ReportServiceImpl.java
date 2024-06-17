package com.evgeniyfedorchenko.animalshelter.backend.services;

import com.evgeniyfedorchenko.animalshelter.backend.dto.ReportOutputDto;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Report;
import com.evgeniyfedorchenko.animalshelter.backend.mappers.ReportMapper;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.AdopterRepository;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.ReportRepository;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.report.ReportPart;
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.report.ReportPart.*;

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

        String adopterChatId = reportOpt.get().getAdopter().getChatId();
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
    @Transactional
    public List<ReportPart> checkUnsentReportParts(String chatId) {
        Report report = reportRepository.findNewestReportByAdopterChatId(chatId).orElseGet(Report::new);
        Adopter adopter = adopterRepository.findByChatId(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Adopter of this report not found"));

        List<ReportPart> unsentParts = new ArrayList<>();

        /* Нужно void-действие для случая, когда report.get...() != null, а такой есть только
           в .ifPresentOrElse(). В .orElse(), .orElseGet() неподходящие действия */
        Optional.ofNullable(report.getDiet()).ifPresentOrElse(_ -> {},           () -> unsentParts.add(DIET));
        Optional.ofNullable(report.getHealth()).ifPresentOrElse(_ -> {},         () -> unsentParts.add(HEALTH));
        Optional.ofNullable(report.getChangeBehavior()).ifPresentOrElse(_ -> {}, () -> unsentParts.add(BEHAVIOR));
        Optional.ofNullable(report.getPhotoData()).ifPresentOrElse(_ -> {},      () -> unsentParts.add(PHOTO));

        this.linkIfFalse(adopter, report, report.hasAdopter());

        adopterRepository.save(adopter);
        reportRepository.save(report);

        return unsentParts;
    }

    @Override
    @Transactional
    public void acceptReportPart(ReportPart reportPart,
                                 byte[] reportPartData,
                                 String relatedAdopterChatId,
                                 @Nullable MediaType mediaType) {

        Report report = reportRepository.findNewestReportByAdopterChatId(relatedAdopterChatId).orElseGet(Report::new);
        Adopter adopter = adopterRepository.findByChatId(relatedAdopterChatId)
                .orElseThrow(() -> new EntityNotFoundException("Adopter of this report not found"));

        switch (reportPart) {
            case DIET -> report.setDiet(new String(reportPartData, StandardCharsets.UTF_8));
            case HEALTH -> report.setHealth(new String(reportPartData, StandardCharsets.UTF_8));
            case BEHAVIOR -> report.setChangeBehavior(new String(reportPartData, StandardCharsets.UTF_8));
            case PHOTO -> {
                report.setPhotoData(reportPartData);
                if (mediaType != null) {
                    report.setMediaType(mediaType.getType());
                }
            }
        }
        this.linkIfFalse(adopter, report, report.hasAdopter());

        adopterRepository.save(adopter);
        reportRepository.save(report);
    }
}
