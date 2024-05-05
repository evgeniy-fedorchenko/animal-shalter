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
public class MainHowTakeAnimalCallback implements Callback {

    private final CallType callType = MAIN_HOW_TAKE_ANIMAL;

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
            put("Правила знакомства с животным",             MAIN_ABOUT.getTitle());
            put("Обустройству дома для взрослого животного", MAIN_ABOUT.getTitle());
            put("Обустройству дома для щенка/котенка",       MAIN_ABOUT.getTitle());
            put("Документы для усыновления питомца",         MAIN_ABOUT.getTitle());
            put("Транспортировке животного",                 MAIN_ABOUT.getTitle());
            put("Назад",                                     START.getTitle());
        }};

        InlineKeyboardMarkup keyboardMarkup = KeyboardUtils.getMarkupWithOneLinesButtons(keyboardData);
        editMessage.setReplyMarkup(keyboardMarkup);
        return editMessage;
    }
}
