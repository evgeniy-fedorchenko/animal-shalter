package com.evgeniyfedorchenko.animalshelter.telegram.handler.callbacks.animalfromshelter;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.CallType;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.ButtonUtils;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.callbacks.Callback;
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
            put("Правила знакомства с животным",             DATING_RULES.getTitle());
            put("Обустройству дома для взрослого животного", HOUSE_FOR_ADULT_ANIMAL.getTitle());
            put("Обустройству дома для щенка/котенка",       HOUSE_FOR_SMALL_ANIMAL.getTitle());
            put("Документы для усыновления питомца",         ADOPTION_DOCS.getTitle());
            put("Транспортировке животного",                 TRANSPORT_PET.getTitle());
            put("Назад",                                     START.getTitle());
        }};

        InlineKeyboardMarkup keyboardMarkup = ButtonUtils.getMarkupWithOneLinesButtons(keyboardData);
        editMessage.setReplyMarkup(keyboardMarkup);
        return editMessage;
    }
}
