package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageData.MAIN_ABOUT_SHELTER;
import static com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageData.SAFETY_AT_SHELTER;

@Component("SafetyAtShelter")
public class SafetyAtShelter implements Callback {

    @Override
    public EditMessageText apply(Long chatId, Integer messageId) {

        Map<String, String> keyboardData = new LinkedHashMap<>();
        keyboardData.put("Назад", MAIN_ABOUT_SHELTER.getCallbackData());

        MessageUtils messageUtils = new MessageUtils();
//        MessageModel messageModel = new MessageModel(chatId, messageId, SAFETY_AT_SHELTER, keyboardData);
        MessageModel messageModel = MessageModel.builder()
                .chatId(chatId)
                .messageId(messageId)
                .messageData(SAFETY_AT_SHELTER)
                .keyboardData(keyboardData)
                .build();

        return messageUtils.applyCallback(messageModel);
    }
}
