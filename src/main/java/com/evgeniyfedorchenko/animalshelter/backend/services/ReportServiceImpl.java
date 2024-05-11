package com.evgeniyfedorchenko.animalshelter.backend.services;

import com.evgeniyfedorchenko.animalshelter.backend.dto.ReportOutputDto;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Report;
import com.evgeniyfedorchenko.animalshelter.backend.mappers.ReportMapper;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.ReportRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final TelegramService telegramService;
    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;

    @Override
    public List<ReportOutputDto> getUnverifiedReports(int limit) {

        List<ReportOutputDto> list =reportRepository.findOldestUnviewedReports(PageRequest.of(0, limit))
                .stream()
                .map(reportMapper::toDto)
                .toList();

        new Thread(() -> {
            List<Long> idsForUpdate = list.stream().map(ReportOutputDto::getId).toList();
            reportRepository.updateReportsViewedStatus(idsForUpdate);
        }).start();

        return list;
    }

    @Override
    public Optional<ReportOutputDto> getReport(long id) {
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

        try {
            return telegramService.sendMessage(String.valueOf(adopterChatId), message);    //  todo Заменить тип на long
        } catch (TelegramApiException e) {
            log.error("Filed to send message to adopter about his bad report. Cause: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void deleteReports(List<Long> ids) {
        reportRepository.deleteAllById(ids);
    }
}
