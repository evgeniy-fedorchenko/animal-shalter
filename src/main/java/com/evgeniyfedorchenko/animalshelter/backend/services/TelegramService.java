package com.evgeniyfedorchenko.animalshelter.backend.services;


public interface TelegramService {

    boolean sendMessage(long chatId, String message);
}
