package com.evgeniyfedorchenko.animalshelter.telegram.handler.actions;

import com.evgeniyfedorchenko.animalshelter.backend.services.TelegramService;
import com.evgeniyfedorchenko.animalshelter.telegram.listener.TelegramExecutor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Component
public class VolunteerChatting {

    private final TelegramService telegramService;
    private final TelegramExecutor telegramExecutor;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Метод для первого коннекта волонтера и юзера, который позвал волонтера. Происходит поиск свободного
     * волонтера и при нахождении такого, ему отправляется уведомляющее сообщение.
     * Так же происходит кеширование их {@code chatId} для предстоящего общения
     * @param userChatId телеграм-идентификатор юзера, которому требуется помощь
     */
    public void callVolunteer(String userChatId) {

        // TODO 10.06.2024 23:12 - Добавить: если волонтера не нашлось - то еще в сервисе опрашивать бд раз в минуту
        //  с вопросом "а не появился ли свободный волонтер?" и возвращать значение только тогда, когда появился.
        //  + возможно выставить таймер на .join()
        //  А к письму, которое отправляется юзеру приделать кнопку "прекратить ожидание", чтоб поток намертво
        //  не вставал. Хотя все равно каждый юзер пользуется ботом в своем потоке

        String messToVolunteer = "Волонтер! Требуется твоя помощь, отправь приветственное сообщение в этот чат";
        Optional<String> chatIdOpt = telegramService.getFreeVolunteer().join();

        if (chatIdOpt.isPresent()) {
            String volunteerChatId = chatIdOpt.get();
            SendMessage sendMessage = new SendMessage(volunteerChatId, messToVolunteer);
            telegramExecutor.send(sendMessage);

            redisTemplate.opsForValue().set(userChatId, volunteerChatId);
            redisTemplate.opsForValue().set(volunteerChatId, userChatId);
            log.info("Communication with volunteer started. vChatId: {}, uChatId: {}", volunteerChatId, userChatId);
        }
    }
}
