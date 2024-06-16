package com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.report;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageUtils;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.Callback;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageData.SEND_PHOTO;
import static com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageData.SEND_REPORT;
import static com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.report.ReportPart.PHOTO;

@AllArgsConstructor
@Component("SendPhoto")
public class SendPhoto implements Callback {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public EditMessageText apply(String chatId, Integer messageId) {
        Map<String, String> keyboardData = new LinkedHashMap<>();
        keyboardData.put("Назад", SEND_REPORT.getCallbackData());

        redisTemplate.opsForValue().set(chatId, PHOTO.getPartId());

        MessageUtils messageUtils = new MessageUtils();
        MessageModel messageModel = MessageModel.builder()
                .chatId(chatId)
                .messageId(messageId)
                .messageData(SEND_PHOTO)
                .keyboardData(keyboardData)
                .build();

        return messageUtils.applyCallback(messageModel);
    }
}
