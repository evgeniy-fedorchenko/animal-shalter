package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.commands;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageData.*;

@Component("/help")
public class Help implements Command {

    @Override
    public SendMessage apply(Long chatId) {

        Map<String, String> keyboardData = new LinkedHashMap<>();

        keyboardData.put("Позвать волонтера", VOLUNTEER.getCallbackData());
        keyboardData.put("Начать сначала",    START.getCallbackData());

        MessageUtils messageUtils = new MessageUtils();
        MessageModel messageModel = MessageModel.builder()
                .chatId(chatId)
                .messageData(HELP)
                .keyboardData(keyboardData)
                .build();

        return messageUtils.applyCommand(messageModel);
    }
}
