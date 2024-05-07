package com.evgeniyfedorchenko.animalshelter.telegram.handler.callbacks.infoabout;

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
public class SecurityContactsCallback implements Callback {

    private final CallType callType = SECURITY_CONTACTS;

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
            put("Назад", MAIN_ABOUT.getTitle()); // есть
        }};

        InlineKeyboardMarkup keyboardMarkup = ButtonUtils.getMarkupWithOneLinesButtons(keyboardData);
        editMessage.setReplyMarkup(keyboardMarkup);
        return editMessage;
    }
}
