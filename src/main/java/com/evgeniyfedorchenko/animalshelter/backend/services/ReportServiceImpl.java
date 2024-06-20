package com.evgeniyfedorchenko.animalshelter.backend.services;

import com.evgeniyfedorchenko.animalshelter.backend.dto.ReportOutputDto;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Report;
import com.evgeniyfedorchenko.animalshelter.backend.mappers.ReportMapper;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.AdopterRepository;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.ReportRepository;
import com.evgeniyfedorchenko.animalshelter.telegram.configuration.RedisConfiguration;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.report.ReportPart;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.report.ReportPart.*;

@Slf4j
@Service
@AllArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final TelegramService telegramService;
    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;
    private final AdopterRepository adopterRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    @Transactional
    public List<ReportOutputDto> getUnverifiedReports(int limit) {

        List<ReportOutputDto> reports =
                reportRepository.findOldestUnverifiedReports(PageRequest.of(0, limit)).stream()
                        .map(reportMapper::toDto)
                        .toList();

        reportRepository.updateReportsVerifiedStatus(
                reports.stream().map(ReportOutputDto::getId).toList()
        );
        return reports;
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

        Report report = reportRepository.findNewestReportByAdopterChatId(chatId).orElse(null);
        if (report == null) {
            return Arrays.asList(ReportPart.values());
        }

        Adopter adopter = adopterRepository.findByChatId(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Adopter of this report not found"));

        List<ReportPart> unsentParts = new ArrayList<>();

        /* Нужно void-действие для случая, когда report.get...() != null, а такой есть только
           в .ifPresentOrElse(). В .orElse(), .orElseGet() неподходящие действия */
        Optional.ofNullable(report.getDiet()).ifPresentOrElse(_ -> {
        }, () -> unsentParts.add(DIET));
        Optional.ofNullable(report.getHealth()).ifPresentOrElse(_ -> {
        }, () -> unsentParts.add(HEALTH));
        Optional.ofNullable(report.getChangeBehavior()).ifPresentOrElse(_ -> {
        }, () -> unsentParts.add(BEHAVIOR));
        Optional.ofNullable(report.getPhotoData()).ifPresentOrElse(_ -> {
        }, () -> unsentParts.add(PHOTO));

        this.linkIfFalse(adopter, report, report.hasAdopter());
        report.setSendingAt(Instant.now());

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
                    report.setMediaType(mediaType.toString());
                }
            }
        }
        this.linkIfFalse(adopter, report, report.hasAdopter());
        report.setSendingAt(Instant.now());

        adopterRepository.save(adopter);
        reportRepository.save(report);
    }


    /**
     * Метод для очистки {@code RedisTemplate} от застоявшихся ключей.
     * Застоявшимися считаются ключи, котороые указывают на {@code chatId} юзеров, которые иницировали
     * отправку отчета, и в течении получаса ничего не прислали. Метод опирается на поле {@code sendingAt}
     * объекта {@code Report} (поле обновляется каждый раз когда юзер отправляет часть отчета)
     * из таблицы {@code reports} базы данных {@code shelter-db}
     *
     * @apiNote При остановке приложения на профиле {@code dev} <b>все</b> ключи удаляются автоматически
     * @see RedisConfiguration
     */
    @Scheduled(fixedDelay = 30, timeUnit = TimeUnit.MINUTES)
    public void cleanRedis() {

        List<String> keysToDelete = new ArrayList<>();
        try (Cursor<String> cursor = redisTemplate.scan(ScanOptions.scanOptions().match("[-\\d]").build())) {

            while (cursor.hasNext()) {
                String key = cursor.next();
                String value = redisTemplate.opsForValue().get(key);

                if (value != null) {
                    Optional<Report> reportOpt = reportRepository.findNewestReportByAdopterChatId(value);
                        if (reportOpt.isPresent() && Duration.between(Instant.now(), reportOpt.get().getSendingAt()).toMinutes() > 10) {
                            keysToDelete.add(key);
                        }
                }
            }
        }
        if (!keysToDelete.isEmpty()) {
            redisTemplate.delete(keysToDelete);
        }
    }
}
