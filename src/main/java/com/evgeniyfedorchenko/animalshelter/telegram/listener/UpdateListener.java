package com.evgeniyfedorchenko.animalshelter.telegram.listener;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class UpdateListener extends TelegramLongPollingBot {

    public UpdateListener(String botToken) {
        // Сюда забахаем кнопки и так далее
        super(botToken);
    }

    @Override
    public String getBotUsername() {
//        Эта информация совершенно не секретная и изменится с вероятностью примерно 0%, так что просто зададим её тут
        return "animal_shelter_helper_bot";
    }

    @Override
    public void onUpdateReceived(Update update) {
//        Просто логируем и перенаправляем в handler
    }
}
