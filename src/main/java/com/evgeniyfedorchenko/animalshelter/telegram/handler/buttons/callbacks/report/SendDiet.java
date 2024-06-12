package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks.report;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageUtils;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks.Callback;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageData.SEND_DIET;
import static com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageData.SEND_REPORT;
import static com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks.report.SendingReportPart.DIET;

@AllArgsConstructor
@Component("SendDiet")
public class SendDiet implements Callback {

    private final RedisTemplate<Long, Long> redisTemplate;
    @Override
    public EditMessageText apply(Long chatId, Integer messageId) {
        Map<String, String> keyboardData = new LinkedHashMap<>();
        keyboardData.put("Назад", SEND_REPORT.getCallbackData());

        redisTemplate.opsForValue().set(chatId, DIET.getPartId());

        MessageUtils messageUtils = new MessageUtils();
        MessageModel messageModel = MessageModel.builder()
                .chatId(chatId)
                .messageId(messageId)
                .messageData(SEND_DIET)
                .keyboardData(keyboardData)
                .build();

        return messageUtils.applyCallback(messageModel);
    }
}
