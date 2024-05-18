package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageData.*;

@Component("GetPatternReport")
public class GetPatternReport implements Callback {

    @Override
    public EditMessageText apply(Long chatId, Integer messageId) {

        Map<String, String> keyboardData = new LinkedHashMap<>();

        keyboardData.put("Начнем! (backend)", START.getCallbackData());
        keyboardData.put("Назад",             MAIN_REPORT_MENU.getCallbackData());

        MessageUtils messageUtils = new MessageUtils();
//        MessageModel messageModel = new MessageModel(chatId, messageId, GET_PATTERN_REPORT, keyboardData);
        MessageModel messageModel = MessageModel.builder()
                .chatId(chatId)
                .messageId(messageId)
                .messageData(GET_PATTERN_REPORT)
                .keyboardData(keyboardData)
                .build();

        return messageUtils.applyCallback(messageModel);
    }
}
