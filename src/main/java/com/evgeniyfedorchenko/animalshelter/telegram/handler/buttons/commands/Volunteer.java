package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.commands;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageUtils;
import com.evgeniyfedorchenko.animalshelter.telegram.listener.TelegramExecutor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.concurrent.CompletableFuture;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageData.VOLUNTEER;

@Slf4j
@AllArgsConstructor
@Component("/volunteer")
public class Volunteer implements Command {

    private final TelegramExecutor telegramExecutor;
    private final RedisTemplate<Long, Long> redisTemplate;

    @Override
    public SendMessage apply(Long chatId) {

        CompletableFuture.runAsync(() -> this.callVolunteer(chatId));

        MessageUtils messageUtils = new MessageUtils();
        MessageModel messageModel = MessageModel.builder()
                .chatId(chatId)
                .messageData(VOLUNTEER)
                .build();

        return messageUtils.applyCommand(messageModel);
    }

    public void callVolunteer(Long userChatId) {

        long volunteerChatId = 5076421775L;
        String needVolunteerMessage = "Требуется помощь волонтера";

        // TODO 01.06.2024 15:39 - создать пул волонтеров, и выбирать рандомного свободного из пула;
        //      с ним инициировать диалог и удалять из пула свободных волонтеров;
        //      после завершения диалога с юзером, класть волонтера обратно

        SendMessage sendMessage = new SendMessage(String.valueOf(volunteerChatId), needVolunteerMessage);
        telegramExecutor.send(sendMessage);

        redisTemplate.opsForValue().set(userChatId, volunteerChatId);
        redisTemplate.opsForValue().set(volunteerChatId, userChatId);
    }
}
