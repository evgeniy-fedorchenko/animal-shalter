package com.evgeniyfedorchenko.animalshelter.telegram.handler.callbacks.animalfromshelter;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.ButtonUtils;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.ChatAction;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.callbacks.Callback;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.CallType.ADOPTION_DOCS;
import static com.evgeniyfedorchenko.animalshelter.telegram.handler.CallType.MAIN_HOW_TAKE_ANIMAL;

@Component
public class AdoptionDocsCallback implements Callback {

    @Override
    public EditMessageText apply(String chatId, Integer messageId) {

        // todo Перенести все кнопки в один класс конфигурации и реализовать как бины и действием Optional<Runnable>
        Map<String, String> keyboardData = new LinkedHashMap<>() {{
            put("Назад", MAIN_HOW_TAKE_ANIMAL.getTitle());
        }};
        ChatAction chatAction = new ChatAction(chatId, messageId, keyboardData, ADOPTION_DOCS);
        return new ButtonUtils().performCallback(chatAction);
    }
}
