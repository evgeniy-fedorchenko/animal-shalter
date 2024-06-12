package com.evgeniyfedorchenko.animalshelter.backend.services;

import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface TelegramService {

    boolean sendMessage(long chatId, String message);

    void savePhoto(Pair<byte[], MediaType> photoDataPair, Long chatId);

    CompletableFuture<Optional<Long>> getFreeVolunteer();

    void returnVolunteer(Long volunteerChatId);
}
