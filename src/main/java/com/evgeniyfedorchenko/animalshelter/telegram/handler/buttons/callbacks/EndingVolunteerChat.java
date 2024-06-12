package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageUtils;
import com.evgeniyfedorchenko.animalshelter.telegram.listener.TelegramExecutor;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageData.ENDING_VOLUNTEER_CHAT;

@Component("EndingVolunteerChat")
@AllArgsConstructor
public class EndingVolunteerChat implements Callback {

    private final RedisTemplate<Long, Long> redisTemplate;
    private final TelegramExecutor telegramExecutor;

    @Override
    public EditMessageText apply(Long chatId, Integer messageId) {

        Long volunteerChatId = redisTemplate.opsForValue().get(chatId);

        redisTemplate.delete(volunteerChatId);   // TODO 01.06.2024 23:51 - assert notnull
        redisTemplate.delete(chatId);

        SendMessage sendMessage = new SendMessage(String.valueOf(volunteerChatId), "Пользователь завершил диалог");
        telegramExecutor.send(sendMessage);

        MessageUtils messageUtils = new MessageUtils();
        MessageModel messageModel = MessageModel.builder()
                .chatId(chatId)
                .messageData(ENDING_VOLUNTEER_CHAT)
                .build();
        return messageUtils.applyCallback(messageModel);
    }
}
