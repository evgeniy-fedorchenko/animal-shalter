package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks.report;

import com.evgeniyfedorchenko.animalshelter.backend.services.ReportService;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageUtils;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks.Callback;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageData.*;

@AllArgsConstructor
@Component("SendReport")
public class SendReport implements Callback {

    private final ReportService reportService;
    @Override
    public EditMessageText apply(Long chatId, Integer messageId) {

        Map<String, String> keyboardData = new LinkedHashMap<>();
        keyboardData.put("Прислать рацион питания",   SEND_DIET.getCallbackData());
        keyboardData.put("Самочувствие и привыкание", SEND_HEALTH.getCallbackData());
        keyboardData.put("Изменения в поведении",     SEND_BEHAVIOR.getCallbackData());
        keyboardData.put("Фото животного",            SEND_PHOTO.getCallbackData());
        keyboardData.put("Назад",                     MAIN_REPORT_MENU.getCallbackData());

        MessageUtils messageUtils = new MessageUtils();
        MessageModel messageModel = MessageModel.builder()
                .chatId(chatId)
                .messageId(messageId)
                .messageData(SEND_REPORT)
                .keyboardData(keyboardData)
                .build();

        return messageUtils.applyCallback(messageModel);
    }

    private boolean checkThisDay() {
//        reportService.
        return false;
    }
}
