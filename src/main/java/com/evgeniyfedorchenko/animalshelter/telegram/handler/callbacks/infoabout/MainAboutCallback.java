package com.evgeniyfedorchenko.animalshelter.telegram.handler.callbacks.infoabout;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.CallType;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.ButtonUtils;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.callbacks.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.CallType.*;

@Component
public class MainAboutCallback implements Callback {

    private final CallType callType = MAIN_ABOUT;

    private final Logger logger = LoggerFactory.getLogger(MainAboutCallback.class);


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
            put("Наш сайт",                      "will be link");             // есть
            put("Контактные данные охраны",      SECURITY_CONTACTS.getTitle()); // есть
            put("Правила безопасности в приюте", SAFETY_AT_SHELTER.getTitle()); // есть
            put("Запишите мои данные (backend)", START.getTitle());             // backend
            put("Назад",                         START.getTitle());             // есть
        }};

        InlineKeyboardMarkup keyboardMarkup = ButtonUtils.getMarkupWithOneLinesButtons(keyboardData);
        setUrlToButton(keyboardMarkup, editMessage);
        return editMessage;
    }

    private void setUrlToButton(InlineKeyboardMarkup keyboardMarkup, EditMessageText editMessage) {

          /* Внешний лист содержит 5 внутренних листов, каждый внутренний лист содержит по одной кнопке
             Находим индекс кнопки с текстом "Наш сайт" и удаляем её. Если не находим -
             - просто оставляем клавиатуру, как есть - готовую, но без ссылки */

        List<List<InlineKeyboardButton>> keyboardList = keyboardMarkup.getKeyboard();
        String url = "https://predannoeserdce.ru/";

        IntStream.range(0, keyboardList.getFirst().size())
                .filter(idx -> keyboardList.get(idx).getFirst().getText().equals("Наш сайт"))
                .findFirst()
                .ifPresentOrElse(idx -> {
                    InlineKeyboardButton newButton = new InlineKeyboardButton("Наш сайт");
                    newButton.setUrl(url);
                    keyboardList.set(idx, new ArrayList<>(List.of(newButton)));
                    keyboardMarkup.setKeyboard(keyboardList);
                    editMessage.setReplyMarkup(keyboardMarkup);

                }, () -> logger.warn("Filed to add a website-link because the button was not found"));
    }
}
