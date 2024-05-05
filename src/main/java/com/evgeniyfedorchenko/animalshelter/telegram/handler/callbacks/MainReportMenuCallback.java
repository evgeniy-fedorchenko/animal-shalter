package com.evgeniyfedorchenko.animalshelter.telegram.handler.callbacks;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.CallType;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.KeyboardUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.CallType.*;

@Component
public class MainReportMenuCallback implements Callback {

    private final CallType callType = MAIN_REPORT_MENU;

    @Override
    public String getCallbackData() {
        return callType.getTitle();
    }

    @Override
    public EditMessageText apply(String chatId, Integer messageId) {
        EditMessageText editMessage = new EditMessageText();

        editMessage.setChatId(chatId);
        editMessage.setMessageId(messageId);
        editMessage.setText(callType.getAnswer());

        Map<String, String> keyboardData = new LinkedHashMap<>() {{
            put("Прислать отчет",         MAIN_ABOUT.getTitle());
            put("Получить шаблон отчета", MAIN_ABOUT.getTitle());
            put("Назад",                  START.getTitle());
        }};

        InlineKeyboardMarkup keyboardMarkup = KeyboardUtils.getMarkupWithOneLinesButtons(keyboardData);
        editMessage.setReplyMarkup(keyboardMarkup);
        return editMessage;
    }
}
