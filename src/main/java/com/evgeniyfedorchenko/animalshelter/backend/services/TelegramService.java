package com.evgeniyfedorchenko.animalshelter.backend.services;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface TelegramService {

    boolean sendMessage(long chatId, String message) throws TelegramApiException;
}
