package com.evgeniyfedorchenko.animalshelter.telegram.listener;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.MainHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.Optional;

/**
 * Класс, представляющий объект бота, зарегистрированный и настроенный с помощью
 * приватного токена. Позволяет взаимодействовать с серверами Telegram
 */
@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final MainHandler mainHandler;
    private final TelegramExecutor telegramExecutor;

    public TelegramBot(@Value("${telegram.bot.token}") String botToken,
                       MainHandler mainHandler,
                       TelegramExecutor telegramExecutor) {
        super(botToken);
        this.mainHandler = mainHandler;
        this.telegramExecutor = telegramExecutor;
    }

    @Override
    public String getBotUsername() {
        return "animal_shelter_helper_bot";
    }

    /**
     * Метод получения сообщений непосредственно с серверов Telegram, а так же их маршрутизации по методам обработки
     *
     * @param update корневой объект, содержащий всю информацию о пришедшем обновлении
     */
    @Override
    public void onUpdateReceived(Update update) {

        if (update != null) {

            log.info("Processing has BEGUN for updateID {}", update.getUpdateId());
            BotApiMethod<? extends Serializable> messToSend = null;

            if (update.hasMessage()) {

                Message message = update.getMessage();

                if (message.hasText()) {
                    if (message.isCommand()) {
                        messToSend = mainHandler.handleCommands(update);
                    } else {
                        messToSend = mainHandler.applyUnknownUserAction(update, message.getChatId());
                    }
                } else if (message.hasPhoto()) {
                    messToSend = mainHandler.savePhoto(message).join();
                }
            } else if (update.hasCallbackQuery()) {
                messToSend = mainHandler.handleCallbacks(update);
            }

            Optional.ofNullable(messToSend).ifPresent(telegramExecutor::send);
            log.info("Processing has successfully ENDED for updateID {}", update.getUpdateId());
        }
    }
}