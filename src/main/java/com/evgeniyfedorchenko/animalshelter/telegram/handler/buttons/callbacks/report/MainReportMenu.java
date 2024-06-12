package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks.report;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageUtils;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks.Callback;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageData.*;

@Component("MainReportMenu")
public class MainReportMenu implements Callback {



    @Override
    public EditMessageText apply(Long chatId, Integer messageId) {

        Map<String, String> keyboardData = new LinkedHashMap<>();

        keyboardData.put("Прислать отчет",         SEND_REPORT.getCallbackData());
        keyboardData.put("Получить шаблон отчета", GET_PATTERN_REPORT.getCallbackData());
        keyboardData.put("Назад",                  START.getCallbackData());

        MessageUtils messageUtils = new MessageUtils();
        MessageModel messageModel = MessageModel.builder()
                .chatId(chatId)
                .messageId(messageId)
                .messageData(MAIN_REPORT_MENU)
                .keyboardData(keyboardData)
                .build();

        return messageUtils.applyCallback(messageModel);
    }
}
