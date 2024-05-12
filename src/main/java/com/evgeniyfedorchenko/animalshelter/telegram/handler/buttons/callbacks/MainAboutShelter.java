package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageData.*;

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
        MessageModel messageModel = new MessageModel(chatId, messageId, MAIN_ABOUT_SHELTER, keyboardData);
        EditMessageText editMessageText = messageUtils.applyCallback(messageModel);
        messageUtils.setUrlToButton(editMessageText);

        return editMessageText;
    }
}
