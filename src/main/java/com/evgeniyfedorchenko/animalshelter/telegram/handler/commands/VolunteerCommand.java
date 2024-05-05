package com.evgeniyfedorchenko.animalshelter.telegram.handler.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class VolunteerCommand implements Command {

    private final Logger logger = LoggerFactory.getLogger(VolunteerCommand.class);

    @Override
    public String getTitle() {
        return "/volunteer";
    }

    @Override
    public SendMessage apply(Message message) {
        new Thread(this::callVolunteer).start();
        return new SendMessage(
                message.getChatId().toString(),
                "Позвали волонтера. Он ответит Вам в этом чате"
        );
    }

    private void callVolunteer() {
        logger.info("Calling volunteer...");
    }
}
