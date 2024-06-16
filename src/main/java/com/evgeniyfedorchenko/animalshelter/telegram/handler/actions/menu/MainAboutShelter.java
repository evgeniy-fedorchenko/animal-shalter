package com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.menu;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageUtils;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.Callback;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageData.*;

@Component("MainAboutShelter")
public class MainAboutShelter implements Callback {

    @Override
    public EditMessageText apply(String chatId, Integer messageId) {

        Map<String, String> keyboardData = new LinkedHashMap<>();

        keyboardData.put("Наш сайт",                      "will be link");
        keyboardData.put("Контактные данные охраны",      SECURITY_CONTACTS.getCallbackData());
        keyboardData.put("Правила безопасности в приюте", SAFETY_AT_SHELTER.getCallbackData());
        keyboardData.put("Запишите мои данные (backend)", START.getCallbackData());
        keyboardData.put("Назад",                         START.getCallbackData());

        MessageUtils messageUtils = new MessageUtils();
        MessageModel messageModel = MessageModel.builder()
                .chatId(chatId)
                .messageId(messageId)
                .messageData(MAIN_ABOUT_SHELTER)
                .keyboardData(keyboardData)
                .build();

        EditMessageText editMessageText = messageUtils.applyCallback(messageModel);
        messageUtils.setUrlToButton(editMessageText);

        return editMessageText;
    }
}
