package com.evgeniyfedorchenko.animalshelter.telegram.listener;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.MainHandler;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
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
    private final RedisTemplate<Long, Long> redisTemplate;

    public TelegramBot(@Value("${telegram.bot.token}") String botToken,
                       MainHandler mainHandler,
                       TelegramExecutor telegramExecutor,
                       RedisTemplate<Long, Long> redisTemplate) {
        super(botToken);
        this.mainHandler = mainHandler;
        this.telegramExecutor = telegramExecutor;
        this.redisTemplate = redisTemplate;
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

            PartialBotApiMethod<? extends Serializable> messToSend = distributeUpdate(update);
            Optional.ofNullable(messToSend).ifPresent(telegramExecutor::send);

            log.info("Processing has successfully ENDED for updateID {}", update.getUpdateId());
        }
    }

    private @Nullable PartialBotApiMethod<? extends Serializable> distributeUpdate(Update update) {
        if (update.hasMessage()) {

            Message message = update.getMessage();

            Long specialBehaviorId = redisTemplate.opsForValue().get(message.getChatId());
            if (specialBehaviorId != null) {
                return specialBehaviorId > 0
                        ? mainHandler.communicationWithVolunteer(message)
                        : mainHandler.sendReportProcess(message, specialBehaviorId);
            }

            if (message.hasText()) {
                return message.isCommand()
                        ? mainHandler.handleCommands(update)
                        : mainHandler.applyUnknownUserAction(update);
            }
        } else if (update.hasCallbackQuery()) {
            return mainHandler.handleCallbacks(update);
        }
        return null;
    }
}