package com.evgeniyfedorchenko.animalshelter.telegram.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;

/**
 * The class contains the logic of parsing a raw object {@code Update}
 * and distributing it by methods of {@link MainHandler}
 */
@Slf4j
@AllArgsConstructor
@Component
public class UpdateDistributor {

    private final MainHandler mainHandler;


    /**
     * The method contains all the logic of checking, parsing and distributing
     * of the object non-validated {@code Update} that came from the telegram bot
     * @param update An object directly sent here from a telegram bot
     * @return A generalized type object ready to be sent
     */
    public BotApiMethod<? extends Serializable> distribute(Update update) {

        if (update.hasMessage()) {

            Message message = update.getMessage();
            if (message.hasText()) {
                if (update.getMessage().isCommand()) {   // Обработка команд меню
                    return mainHandler.handleCommands(update.getMessage());
                } else {

                }
            } else if (message.hasPhoto()) {
                // Видимо это прислали фотку животного
            }

        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            return mainHandler.handleCallbacks(callbackQuery);

        }
        return null;
    }
}
