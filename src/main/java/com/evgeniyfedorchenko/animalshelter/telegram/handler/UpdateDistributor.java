package com.evgeniyfedorchenko.animalshelter.telegram.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;

@Slf4j
@AllArgsConstructor
@Component
public class UpdateDistributor {

    private final MainHandler mainHandler;

/*    Я решил по получению колбека не отправлять новое сообщение, а редактировать старое,
      чтоб не загромождать чат + @BotFather именно так делает. Там тип данных EditMessageText,
      а не SendMessage. Поэтому возвращаем ближайшего общего родителя */
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
