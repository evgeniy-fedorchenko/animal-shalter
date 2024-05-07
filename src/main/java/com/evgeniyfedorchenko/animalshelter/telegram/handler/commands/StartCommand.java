package com.evgeniyfedorchenko.animalshelter.telegram.handler.commands;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.CallType;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.ButtonUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.CallType.*;

@Component
public class StartCommand implements Command {

    private final CallType callType = START;


    @Override
    public String getTitle() {
        return callType.getTitle();
    }

    @Override
    public SendMessage apply(String chatId) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(callType.getAnswer());

        Map<String, String> keyboardData = new LinkedHashMap<>() {{
            put("О нашем приюте",               MAIN_ABOUT.getTitle());           // есть
            put("Как взять животное из приюта", MAIN_HOW_TAKE_ANIMAL.getTitle()); // есть
            put("Прислать отчет о питомце",     MAIN_REPORT_MENU.getTitle());     // есть
        }};

        InlineKeyboardMarkup keyboardMarkup = ButtonUtils.getMarkupWithOneLinesButtons(keyboardData);
        sendMessage.setReplyMarkup(keyboardMarkup);

        return sendMessage;
    }
}
