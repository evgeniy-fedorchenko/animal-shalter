package com.evgeniyfedorchenko.animalshelter.telegram.listener;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.UpdateDistributor;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;

/**
 * A class representing a bot object registered and configured
 * with a private token.Allows you to interact with Telegram servers
 */
@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final UpdateDistributor updateDistributor;

    public TelegramBot(@Value("${telegram.bot.token}") String botToken,
                       UpdateDistributor updateDistributor) {
        super(botToken);
        this.updateDistributor = updateDistributor;
    }

    @Override
    public String getBotUsername() {
        return "animal_shelter_helper_bot";
    }

    /**
     * A method for receiving messages directly from the Telegram servers
     */
    @Override
    public void onUpdateReceived(Update update) {

        if (update != null) {

            log.info("Processing has BEGUN for updateID {}", update.getUpdateId());

            BotApiMethod<? extends Serializable> distribute = updateDistributor.distribute(update);
            send(distribute);

            log.info("Processing has successfully ENDED for updateID {}", update.getUpdateId());
        }
    }


    /**
     * A method for sending messages directly to the Telegram servers
     * In case of an exception, it will be logged as {@code TelegramApiException was thrown. Cause: ex.getMessage()}
     *
     * @param messToSend @NotNull The object of the message ready to be sent
     */
    public void send(@NotNull BotApiMethod<? extends Serializable> messToSend) {

        try {
            execute(messToSend);
        } catch (TelegramApiException ex) {
            log.error("TelegramApiException was thrown. Cause: {}", ex.getMessage());
        }

    }
}