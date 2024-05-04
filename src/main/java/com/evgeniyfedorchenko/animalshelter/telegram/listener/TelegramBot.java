package com.evgeniyfedorchenko.animalshelter.telegram.listener;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.UpdateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final Logger logger = LoggerFactory.getLogger(TelegramBot.class);
    private final UpdateHandler updateHandler;

    public TelegramBot(@Value("${telegram.bot.token}") String botToken,
                       UpdateHandler updateHandler) {
        super(botToken);
        this.updateHandler = updateHandler;
    }

    @Override
    public String getBotUsername() {
        return "animal_shelter_helper_bot";
    }

    @Override
    public void onUpdateReceived(Update update) {

        logger.info("Processing has BEGUN for update: {}", update);

        if (update != null) {

            try {
                execute(updateHandler.handle(update));
            } catch (TelegramApiException ex) {
                logger.error(ex.getMessage());
            }

        }

        if (update.hasMessage()) {



        } else if (update.hasCallbackQuery()) {

        }


        if (update.hasMessage() && update.getMessage().hasText()) {

        } else if (update.hasCallbackQuery()) {
            String call_data = update.getCallbackQuery().getData();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();

            System.out.println(call_data);
            System.out.println(message_id);
            System.out.println(chat_id);
        }

        if (update == null || !update.hasMessage()) {
            logger.info("Failed to process for update: {}. Cause: update or update.getMessage is null", update);
            return;
        } else if (update.hasCallbackQuery()) {

        }




        logger.info("Processing has successfully ENDED for update: {}", update);
    }
}