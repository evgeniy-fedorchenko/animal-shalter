package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.commands;

import com.evgeniyfedorchenko.animalshelter.backend.services.TelegramService;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageUtils;
import com.evgeniyfedorchenko.animalshelter.telegram.listener.TelegramExecutor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageData.VOLUNTEER;

@Slf4j
@AllArgsConstructor
@Component("/volunteer")
public class Volunteer implements Command {

    private final TelegramExecutor telegramExecutor;
    private final RedisTemplate<Long, Long> redisTemplate;
    private final TelegramService telegramService;

    @Override
    public SendMessage apply(Long chatId) {

        CompletableFuture.runAsync(() -> this.callVolunteer(chatId));

        MessageUtils messageUtils = new MessageUtils();
        MessageModel messageModel = MessageModel.builder()
                .chatId(chatId)
                .messageData(VOLUNTEER)
                .build();

        SendMessage sendMessage = messageUtils.applyCommand(messageModel);
        return sendMessage;
    }

    public void callVolunteer(Long userChatId) {

        // TODO 10.06.2024 23:12 - Добавить: если волонтера не нашлось - то еще в сервисе опрашивать бд раз в минуту
        //  с вопросом "а не появился ли свободный волонтер?" и возвращать значение только тогда, когда появился.
        //  + возможно выставить таймер на .join()
        //  А к письму, которое отправляется юзеру приделать кнопку "прекратить ожидание", чтоб поток намертво
        //  не вставал. Хотя все равно каждый юзер пользуется ботом в своем потоке

        String messToVolunteer = "Волонтер! Требуется твоя помощь, отправь приветственное сообщение в этот чат";
        Optional<Long> chatIdOpt = telegramService.getFreeVolunteer().join();

        if (chatIdOpt.isPresent()) {
            Long volunteerChatId = chatIdOpt.get();
            SendMessage sendMessage = new SendMessage(String.valueOf(volunteerChatId), messToVolunteer);
            telegramExecutor.send(sendMessage);

            redisTemplate.opsForValue().set(userChatId, volunteerChatId);
            redisTemplate.opsForValue().set(volunteerChatId, userChatId);
        }
    }
}
