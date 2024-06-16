package com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.commands;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageUtils;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.SimpleApplicable;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.VolunteerChatting;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.concurrent.CompletableFuture;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageData.VOLUNTEER;

@Slf4j
@AllArgsConstructor
@Component("/volunteer")
public class Volunteer implements SimpleApplicable {

    private final VolunteerChatting volunteerChatting;

    @Override
    public SendMessage apply(String chatId) {

        CompletableFuture.runAsync(() -> volunteerChatting.callVolunteer(chatId));

        MessageUtils messageUtils = new MessageUtils();
        MessageModel messageModel = MessageModel.builder()
                .chatId(chatId)
                .messageData(VOLUNTEER)
                .build();

        return messageUtils.applySimpled(messageModel);
    }
}
