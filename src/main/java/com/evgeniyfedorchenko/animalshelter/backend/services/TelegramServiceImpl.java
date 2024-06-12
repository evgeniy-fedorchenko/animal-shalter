package com.evgeniyfedorchenko.animalshelter.backend.services;

import com.evgeniyfedorchenko.animalshelter.backend.repositories.ReportRepository;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.VolunteerRepository;
import com.evgeniyfedorchenko.animalshelter.telegram.listener.TelegramExecutor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@AllArgsConstructor
public class TelegramServiceImpl implements TelegramService {

    private final TelegramExecutor telegramExecutor;
    private final ReportRepository reportRepository;
    private final VolunteerRepository volunteerRepository;

    @Override
    public boolean sendMessage(long chatId, String message) {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), message);
        return telegramExecutor.send(sendMessage);
    }

    @Override
    public void savePhoto(Pair<byte[], MediaType> photoDataPair, Long chatId) {
        // TODO 10.06.2024 23:05
    }

    @Override
    @Async
    @Transactional
    public CompletableFuture<Optional<Long>> getFreeVolunteer() {

        CompletableFuture<Optional<Long>> f = CompletableFuture.supplyAsync(volunteerRepository::getFreeVolunteer);
        f.thenAcceptAsync(chatIdOpt -> chatIdOpt.ifPresent(chatId ->
                volunteerRepository.setFreeStatusToVolunteerWith(false, chatId)));
        return f;
    }

    @Override
    @Transactional
    public void returnVolunteer(Long volunteerChatId) {
        volunteerRepository.setFreeStatusToVolunteerWith(true, volunteerChatId);
    }
}
