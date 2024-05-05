package com.evgeniyfedorchenko.animalshelter.telegram.handler.callbacks;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.CallType;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.KeyboardUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

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
            put("Наш сайт",                      BLANK.getTitle());
            put("Контактные данные охраны",      SECURITY_CONTACTS.getTitle());
            put("Правила безопасности в приюте", SAFETY_AT_SHELTER.getTitle());
            put("Запишите мои данные",           BLANK.getTitle());
            put("Назад",                         START.getTitle());
        }};

        InlineKeyboardMarkup keyboardMarkup = KeyboardUtils.getMarkupWithOneLinesButtons(keyboardData);
//        keyboardMarkup.setKeyboard(List.of(toEmbedLink(keyboardMarkup)));

        editMessage.setReplyMarkup(keyboardMarkup);
        return editMessage;
    }

    private List<InlineKeyboardButton> toEmbedLink(InlineKeyboardMarkup keyboardMarkup) {
        List<InlineKeyboardButton> first = keyboardMarkup.getKeyboard().getFirst();
        int buttonToSiteNum = IntStream.range(0, first.size())
                .filter(i -> first.get(i).getText().equals("Наш сайт"))
                .findFirst()
                .orElse(-1);

        if (buttonToSiteNum == -1) {
            logger.warn("Failed to embedded link to the website in the button because the button could not be found");
        } else {
            first.get(buttonToSiteNum).setUrl("https://predannoeserdce.ru/");
        }
        return first;
    }
}
