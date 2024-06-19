package com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.menu;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageUtils;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.Callback;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.VolunteerChatting;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.concurrent.CompletableFuture;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageData.VOLUNTEER;

@AllArgsConstructor
@Component("Volunteer")
public class VolunteerCallback implements Callback {

    private final VolunteerChatting volunteerChatting;

    @Override
    public EditMessageText apply(String chatId, Integer messageId) {

        CompletableFuture.runAsync(() -> volunteerChatting.callVolunteer(chatId));

        MessageUtils messageUtils = new MessageUtils();
        MessageModel messageModel = MessageModel.builder()
                .chatId(chatId)
                .messageId(messageId)
                .messageData(VOLUNTEER)
                .build();

        return messageUtils.applyCallback(messageModel);
    }
}
