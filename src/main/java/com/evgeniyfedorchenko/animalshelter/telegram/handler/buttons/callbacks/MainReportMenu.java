package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageData.*;

@Component("MainReportMenu")
public class MainReportMenu implements Callback {



    @Override
    public EditMessageText apply(String chatId, Integer messageId) {

        Map<String, String> keyboardData = new LinkedHashMap<>();

        keyboardData.put("Прислать отчет (backend)", START.getCallbackData());
        keyboardData.put("Получить шаблон отчета",   GET_PATTERN_REPORT.getCallbackData());
        keyboardData.put("Назад",                    START.getCallbackData());

        MessageUtils messageUtils = new MessageUtils();
        MessageModel messageModel = new MessageModel(chatId, messageId, MAIN_REPORT_MENU, keyboardData);

        return messageUtils.applyCallback(messageModel);
    }
}
