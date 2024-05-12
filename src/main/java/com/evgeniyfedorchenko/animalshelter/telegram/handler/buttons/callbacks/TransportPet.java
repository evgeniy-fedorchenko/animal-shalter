package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageData.MAIN_HOW_TAKE_ANIMAL;
import static com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageData.TRANSPORT_PET;

@Component("TransportPet")
public class TransportPet implements Callback {

    @Override
    public EditMessageText apply(String chatId, Integer messageId) {

        Map<String, String> keyboardData = new LinkedHashMap<>();
        keyboardData.put("Назад", MAIN_HOW_TAKE_ANIMAL.getCallbackData());

        MessageUtils messageUtils = new MessageUtils();
        MessageModel messageModel = new MessageModel(chatId, messageId, TRANSPORT_PET, keyboardData);

        return messageUtils.applyCallback(messageModel);
    }
}
