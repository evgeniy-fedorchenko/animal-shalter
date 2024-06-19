package com.evgeniyfedorchenko.animalshelter.backend.services;

import java.util.Optional;

public interface TelegramService {

    boolean sendMessage(String chatId, String message);

    Optional<String> getFreeVolunteer();

    void returnVolunteer(String volunteerChatId);

    void makeDecisionAboutAdaptation();

}
