package com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.menu;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageUtils;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.Callback;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageData.DATING_RULES;
import static com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageData.MAIN_HOW_TAKE_ANIMAL;

@Component("DatingRules")
public class DatingRules implements Callback {

    @Override
    public EditMessageText apply(String chatId, Integer messageId) {

        Map<String, String> keyboardData = new LinkedHashMap<>();
        keyboardData.put("Назад", MAIN_HOW_TAKE_ANIMAL.getCallbackData());

        MessageUtils messageUtils = new MessageUtils();
        MessageModel messageModel = MessageModel.builder()
                .chatId(chatId)
                .messageId(messageId)
                .messageData(DATING_RULES)
                .keyboardData(keyboardData)
                .build();
        return messageUtils.applyCallback(messageModel);
    }
}
