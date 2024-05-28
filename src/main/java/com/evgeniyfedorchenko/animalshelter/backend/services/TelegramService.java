package com.evgeniyfedorchenko.animalshelter.backend.services;

import org.telegram.telegrambots.meta.api.objects.PhotoSize;

public interface TelegramService {

    boolean sendMessage(long chatId, String message);

    void savePhoto(PhotoSize photo, Long chatId);

}
