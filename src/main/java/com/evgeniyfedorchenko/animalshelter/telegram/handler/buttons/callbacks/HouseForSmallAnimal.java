package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageData.HOUSE_FOR_SMALL_ANIMAL;
import static com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageData.MAIN_HOW_TAKE_ANIMAL;

@Component("HouseForSmallAnimal")
public class HouseForSmallAnimal implements Callback {

    @Override
    public EditMessageText apply(Long chatId, Integer messageId) {

        Map<String, String> keyboardData = new LinkedHashMap<>();
        keyboardData.put("Назад", MAIN_HOW_TAKE_ANIMAL.getCallbackData());

        MessageUtils messageUtils = new MessageUtils();
//        MessageModel messageModel = new MessageModel(chatId, messageId, HOUSE_FOR_SMALL_ANIMAL, keyboardData);
        MessageModel messageModel = MessageModel.builder()
                .chatId(chatId)
                .messageId(messageId)
                .messageData(HOUSE_FOR_SMALL_ANIMAL)
                .keyboardData(keyboardData)
                .build();

        return messageUtils.applyCallback(messageModel);
    }
}
