package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageUtils;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.commands.Start;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageData.START;

@Component("Start")
public class BackToStart implements Callback {



    @Override
    public EditMessageText apply(Long chatId, Integer messageId) {

        MessageUtils messageUtils = new MessageUtils();
        MessageModel messageModel = MessageModel.builder()
                .chatId(chatId)
                .messageId(messageId)
                .messageData(START)
                .build();
        EditMessageText editMessage = messageUtils.applyCallback(messageModel);

        /* К сожалению из объекта SendMessage нельзя вытащить клавиатуру в виде коллекции кнопок (или мапы), можно
           только в виде ReplyKeyboard. А метод создания клавиатуры принимает только мапу, потому создаем клавиатуру
           через отдельный объект new Start, забираем её в виде объекта ReplyKeyboard и сетим в сообщение */

        ReplyKeyboard replyMarkup = new Start().apply(chatId).getReplyMarkup();
        editMessage.setReplyMarkup((InlineKeyboardMarkup) replyMarkup);
        return editMessage;
    }
}
