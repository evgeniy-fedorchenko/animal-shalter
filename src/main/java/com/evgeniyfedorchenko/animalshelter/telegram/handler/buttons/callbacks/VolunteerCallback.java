package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageUtils;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.commands.Volunteer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.concurrent.CompletableFuture;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageData.VOLUNTEER;

@AllArgsConstructor
@Component("Volunteer")
public class VolunteerCallback implements Callback {

    private final Volunteer volunteer;

    @Override
    public EditMessageText apply(Long chatId, Integer messageId) {

        CompletableFuture.runAsync(() -> volunteer.callVolunteer(chatId));

        MessageUtils messageUtils = new MessageUtils();
        MessageModel messageModel = MessageModel.builder()
                .chatId(chatId)
                .messageId(messageId)
                .messageData(VOLUNTEER)
                .build();

        return messageUtils.applyCallback(messageModel);
    }
}
