package com.evgeniyfedorchenko.animalshelter.telegram.handler.callbacks;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.CallType;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.commands.StartCommand;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.CallType.START;

@Component
public class BackToStartCallback implements Callback {

    private final CallType callType = START;

    @Override
    public String getCallbackData() {
        return callType.getTitle();
    }

    @Override
    public EditMessageText apply(String chatId, Integer messageId) {

        SendMessage start = new StartCommand().apply(chatId);
        EditMessageText editMessage = new EditMessageText();

        editMessage.setChatId(chatId);
        editMessage.setMessageId(messageId);
        editMessage.setText(callType.getAnswer());

//        ReplyMarkup - это родительский интерфейс для InlineKeyboardMarkup
        editMessage.setReplyMarkup((InlineKeyboardMarkup) start.getReplyMarkup());
        return editMessage;
    }
}
