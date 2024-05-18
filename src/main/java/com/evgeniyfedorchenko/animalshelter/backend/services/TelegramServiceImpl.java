package com.evgeniyfedorchenko.animalshelter.backend.services;

import com.evgeniyfedorchenko.animalshelter.telegram.listener.TelegramBot;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@AllArgsConstructor
@Service
public class TelegramServiceImpl implements TelegramService {

    private final TelegramBot telegramBot;

    @Override
    public boolean sendMessage(long chatId, String message) {

        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), message);
        try {
            telegramBot.execute(sendMessage);
        } catch (TelegramApiException ex) {
            log.error("Filed to send message to adopter about his bad report. Cause: {}", ex.getMessage());
//            todo придумать как вернуть тут bad_gateway
        }
        return false;
    }
}
