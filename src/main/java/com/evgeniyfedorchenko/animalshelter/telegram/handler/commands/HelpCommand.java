package com.evgeniyfedorchenko.animalshelter.telegram.handler.commands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.commands.MessageText.HELP;

@Component
public class HelpCommand implements Command {

    @Override
    public String getTitle() {
        return "/help";
    }

    @Override
    public SendMessage apply(Message message) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText(HELP.getText());

        InlineKeyboardMarkup markup = getInlineKeyboardMarkup();
        sendMessage.setReplyMarkup(markup);

        return sendMessage;
    }

    private InlineKeyboardMarkup getInlineKeyboardMarkup() {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        row.add(getButton("Позвать волонтера"));
        rowList.add(row);

        row = new ArrayList<>();
        row.add(getButton("Начать сначала"));
        rowList.add(row);

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    private InlineKeyboardButton getButton(String text) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(text);
        inlineKeyboardButton.setCallbackData("Callback");
        return inlineKeyboardButton;
    }
}
