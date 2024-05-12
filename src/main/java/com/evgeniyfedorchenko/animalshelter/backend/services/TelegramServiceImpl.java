package com.evgeniyfedorchenko.animalshelter.backend.services;

import com.evgeniyfedorchenko.animalshelter.telegram.listener.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class TelegramServiceImpl implements TelegramService {

    @Autowired
    private TelegramBot telegramBot;

    @Override
    public boolean sendMessage(long chatId, String message) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), message);
        telegramBot.execute(sendMessage);
        return true;
    }
}
