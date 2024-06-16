package com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.commands;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageUtils;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.SimpleApplicable;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageData.*;

@Component("/help")
public class Help implements SimpleApplicable {

    @Override
    public SendMessage apply(String chatId) {

        Map<String, String> keyboardData = new LinkedHashMap<>();

        keyboardData.put("Позвать волонтера", VOLUNTEER.getCallbackData());
        keyboardData.put("Начать сначала",    START.getCallbackData());

        MessageUtils messageUtils = new MessageUtils();
        MessageModel messageModel = MessageModel.builder()
                .chatId(chatId)
                .messageData(HELP)
                .keyboardData(keyboardData)
                .build();

        return messageUtils.applySimpled(messageModel);
    }
}
