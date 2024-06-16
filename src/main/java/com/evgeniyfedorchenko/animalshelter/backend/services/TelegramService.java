package com.evgeniyfedorchenko.animalshelter.backend.services;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface TelegramService {

    boolean sendMessage(String chatId, String message);

    CompletableFuture<Optional<String>> getFreeVolunteer();

    void returnVolunteer(String volunteerChatId);

    void makeDecisionAboutAdaptation();

}
