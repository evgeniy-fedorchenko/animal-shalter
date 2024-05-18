package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.commands;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageData.VOLUNTEER;

@Slf4j
@Component("/volunteer")
public class Volunteer implements Command {

    @Override
    public SendMessage apply(Long chatId) {

        new Thread(this::callVolunteer).start();

        MessageUtils messageUtils = new MessageUtils();
//        MessageModel messageModel = new MessageModel(chatId, VOLUNTEER, null);
        MessageModel messageModel = MessageModel.builder()
                .chatId(chatId)
                .messageData(VOLUNTEER)
                .build();

        return messageUtils.applyCommand(messageModel);
    }

    private void callVolunteer() {
        // todo Реализовать вызов волонтера
        log.info("Calling volunteer...");
    }
}
