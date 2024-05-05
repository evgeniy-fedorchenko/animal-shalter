package com.evgeniyfedorchenko.animalshelter.telegram.handler.commands;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.CallType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.CallType.CALL_VOLUNTEER;

@Component
public class VolunteerCommand implements Command {

    private final Logger logger = LoggerFactory.getLogger(VolunteerCommand.class);

    private final CallType callType = CALL_VOLUNTEER;

    @Override
    public String getTitle() {
        return callType.getTitle();
    }

    @Override
    public SendMessage apply(String chatId) {
        new Thread(this::callVolunteer).start();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Позвали волонтера. Он ответит Вам в этом чате");
        return sendMessage;
    }

    private void callVolunteer() {
        logger.info("Calling volunteer...");
    }
}
