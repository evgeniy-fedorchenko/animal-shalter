package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageData.*;

@Component("MainHowTakeAnimal")
public class MainHowTakeAnimal implements Callback {

    @Override
    public EditMessageText apply(String chatId, Integer messageId) {

        Map<String, String> keyboardData = new LinkedHashMap<>();

        keyboardData.put("Правила знакомства с животным",             DATING_RULES.getCallbackData());
        keyboardData.put("Обустройство дома для взрослого животного", HOUSE_FOR_ADULT_ANIMAL.getCallbackData());
        keyboardData.put("Обустройство дома для щенка/котенка",       HOUSE_FOR_SMALL_ANIMAL.getCallbackData());
        keyboardData.put("Документы для усыновления питомца",         ADOPTION_DOCS.getCallbackData());
        keyboardData.put("Транспортировка животного",                 TRANSPORT_PET.getCallbackData());
        keyboardData.put("Назад",                                     START.getCallbackData());

        MessageUtils messageUtils = new MessageUtils();
        MessageModel messageModel = new MessageModel(chatId, messageId, MAIN_HOW_TAKE_ANIMAL, keyboardData);
        return messageUtils.applyCallback(messageModel);
    }
}
