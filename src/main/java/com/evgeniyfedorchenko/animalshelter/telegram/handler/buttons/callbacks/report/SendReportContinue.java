package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks.report;

import com.evgeniyfedorchenko.animalshelter.backend.services.ReportService;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageUtils;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.commands.Command;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageData.SEND_REPORT_CONTINUE_PATTERN;
import static com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageData.SEND_REPORT_END;

@AllArgsConstructor
@Component("SendReportContinue")
public class SendReportContinue implements Command {

    private final ReportService reportService;
    private final SendReportEnd sendReportEnd;

    @Override
    public SendMessage apply(Long chatId) {

        List<SendingReportPart> unsentParts = reportService.checkUnsentReportParts(chatId);
        if (unsentParts.isEmpty()) {
            return sendReportEnd.apply(chatId);
        }

        Map<String, String> keyboardData = new LinkedHashMap<>();
        unsentParts.forEach(part -> keyboardData.put(part.getButtonText(), part.getMessageData().getCallbackData()));
        keyboardData.put("Больше не хочу ничего присылать", SEND_REPORT_END.getCallbackData());

        MessageUtils messageUtils = new MessageUtils();
        MessageModel messageModel = MessageModel.builder()
                .chatId(chatId)
                .messageData(SEND_REPORT_CONTINUE_PATTERN)
                .keyboardData(keyboardData)
                .build();

        return messageUtils.applyCommand(messageModel);
    }
}
