package com.evgeniyfedorchenko.animalshelter.telegram.handler.actions;

import com.evgeniyfedorchenko.animalshelter.backend.services.TelegramService;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageUtils;
import com.evgeniyfedorchenko.animalshelter.telegram.listener.TelegramExecutor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageData.ENDING_VOLUNTEER_CHAT;

@Slf4j
@Component("VolunteerChattingEnd")
@AllArgsConstructor
public class VolunteerChattingEnd implements SimpleApplicable {

    private final RedisTemplate<String, String> redisTemplate;
    private final TelegramExecutor telegramExecutor;
    private final TelegramService telegramService;

    @Override
    public SendMessage apply(String chatId) {

        String volunteerChatId = redisTemplate.opsForValue().get(chatId);

        if (volunteerChatId != null) {
            redisTemplate.delete(volunteerChatId);
            telegramService.returnVolunteer(volunteerChatId);

            SendMessage sendMessage = new SendMessage(volunteerChatId, "Пользователь завершил диалог");
            telegramExecutor.send(sendMessage);

        } else {
            log.warn("volunteerChatId was not found to delete");
        }

        redisTemplate.delete(chatId);
        log.info("Communication with volunteer was ended. vChatId: {}, uChatId: {}", volunteerChatId, chatId);

        MessageUtils messageUtils = new MessageUtils();
        MessageModel messageModel = MessageModel.builder()
                .chatId(chatId)
                .messageData(ENDING_VOLUNTEER_CHAT)
                .build();
        return messageUtils.applySimpled(messageModel);
    }
}
