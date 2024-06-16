package com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.report;

import com.evgeniyfedorchenko.animalshelter.backend.services.ReportService;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageUtils;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.SimpleApplicable;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageData.*;

@AllArgsConstructor
@Component("SendReport")
public class SendReport implements SimpleApplicable {

    private final ReportService reportService;
    private final SendReportEnd sendReportEnd;

    @Override
    public SendMessage apply(String chatId) {

        List<ReportPart> unsentParts = reportService.checkUnsentReportParts(chatId);
        if (unsentParts.isEmpty()) {
            return sendReportEnd.apply(chatId);
        }

        Map<String, String> keyboardData = new LinkedHashMap<>();
        keyboardData.put("Прислать рацион питания",   SEND_DIET.getCallbackData());
        keyboardData.put("Самочувствие и привыкание", SEND_HEALTH.getCallbackData());
        keyboardData.put("Изменения в поведении",     SEND_BEHAVIOR.getCallbackData());
        keyboardData.put("Фото животного",            SEND_PHOTO.getCallbackData());
        keyboardData.put("Назад",                     MAIN_REPORT_MENU.getCallbackData());

        MessageUtils messageUtils = new MessageUtils();
        MessageModel messageModel = MessageModel.builder()
                .chatId(chatId)
                .messageData(SEND_REPORT)
                .keyboardData(keyboardData)
                .build();

        return messageUtils.applySimpled(messageModel);
    }
}
