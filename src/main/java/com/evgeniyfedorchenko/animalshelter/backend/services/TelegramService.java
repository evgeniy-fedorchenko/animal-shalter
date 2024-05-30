package com.evgeniyfedorchenko.animalshelter.backend.services;

import java.net.URL;

public interface TelegramService {

    boolean sendMessage(long chatId, String message);

    void savePhoto(URL url, Long chatId);

}
