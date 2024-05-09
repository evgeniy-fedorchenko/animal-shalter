package com.evgeniyfedorchenko.animalshelter.telegram.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class UpdateHandler {

    private final CommandsHandler commandsHandler;

    private final Logger logger = LoggerFactory.getLogger(UpdateHandler.class);

    public UpdateHandler(CommandsHandler commandsHandler) {
        this.commandsHandler = commandsHandler;
    }

    public SendMessage handle(Update update) {

        if (update.hasMessage()) {

            Message message = update.getMessage();
            if (message.hasText()) {
                if (update.getMessage().getText().startsWith("/")) {   // Обработка команд меню
                    return commandsHandler.handleCommands(update.getMessage());
                } else {

                }
            } else if (message.hasPhoto()) {
                // Видимо это прислали фотку животного
            }

        } else if (update.hasCallbackQuery()) {

            String call_data = update.getCallbackQuery().getData();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();


        }
        return null;
    }
}
