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
public class HelpCommand implements Command {

    private final CallType callType = HELP;

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
            put("Позвать волонтера", CALL_VOLUNTEER.getTitle()); // волонтер
            put("Начать сначала",    START.getTitle());          // есть - почему-то не работает
        }};

        InlineKeyboardMarkup keyboardMarkup = ButtonUtils.getMarkupWithOneLinesButtons(keyboardData);
        sendMessage.setReplyMarkup(keyboardMarkup);
        return sendMessage;
    }
}
