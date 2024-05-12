package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageData.VOLUNTEER;

@Component("Volunteer")
public class VolunteerCallback implements Callback {

    @Override
    public EditMessageText apply(String chatId, Integer messageId) {

        MessageUtils messageUtils = new MessageUtils();
        MessageModel messageModel = new MessageModel(chatId, messageId, VOLUNTEER, null);

        return messageUtils.applyCallback(messageModel);
    }


}
