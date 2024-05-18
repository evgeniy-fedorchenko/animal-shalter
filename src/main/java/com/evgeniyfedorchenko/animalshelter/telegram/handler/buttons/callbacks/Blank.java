package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageData.BLANK;

@Component
public class Blank implements Callback {

    private final MessageUtils messageUtils = new MessageUtils();

    @Override
    public EditMessageText apply(Long chatId, Integer messageId) {

        MessageModel messageModel = MessageModel.builder()
                .chatId(chatId)
                .messageId(messageId)
                .messageData(BLANK)
                .build();

        return messageUtils.applyCallback(messageModel);
    }
}
