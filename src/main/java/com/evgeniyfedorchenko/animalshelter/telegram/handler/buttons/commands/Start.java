package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.commands;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageData.*;

@Component("/start")
public class Start implements Command {

    @Override
    public SendMessage apply(Long chatId) {

        Map<String, String> keyboardData = new LinkedHashMap<>();

        keyboardData.put("О нашем приюте",               MAIN_ABOUT_SHELTER.getCallbackData());
        keyboardData.put("Как взять животное из приюта", MAIN_HOW_TAKE_ANIMAL.getCallbackData());
        keyboardData.put("Прислать отчет о питомце",     MAIN_REPORT_MENU.getCallbackData());

        MessageUtils messageUtils = new MessageUtils();
        MessageModel messageModel = MessageModel.builder()
                .chatId(chatId)
                .messageData(START)
                .keyboardData(keyboardData)
                .build();

        return messageUtils.applyCommand(messageModel);

    }
}
