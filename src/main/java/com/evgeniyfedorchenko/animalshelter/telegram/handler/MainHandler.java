package com.evgeniyfedorchenko.animalshelter.telegram.handler;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks.Callback;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.commands.Command;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Map;

/**
 * The main handler of messages from the bot's Telegrams. Contains methods
 * for handling all possible scenarios provided by the application logic
 */
@Component
public class MainHandler {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * A method for processing <b>commands</b> sent from a Telegram bot. The method
     * searches for registered implementations of {@link Command} and matches them with the message text
     * @param message Object of type {@code Message} for processing
     * @return A ready-made message object to send via Telegram bot
     */

    public SendMessage handleCommands(Message message) {

        Map<String, Command> commandsMap = applicationContext.getBeansOfType(Command.class);
        String commandText = message.getText();
        Command command = commandsMap.get(commandText);

        return command == null
                ? new SendMessage(message.getChatId().toString(), "Unknown command: " + commandText)
                : command.apply(String.valueOf(message.getChatId()));
    }

    /**
     * A method for processing <b>callbacks</b> sent from a Telegram bot. The method
     * searches for registered implementations of {@link Callback} and matches them with the message text
     * @param callbackQuery Object of {@code CallbackQuery} for processing
     * @return This object does not send a new message, but only modifies an existing
     *         one containing {@code this.callbackQuery.getMessage().getMessageId()}
     */
    public EditMessageText handleCallbacks(CallbackQuery callbackQuery) {

        Map<String, Callback> callbacksMap = applicationContext.getBeansOfType(Callback.class);
        String chatId = String.valueOf(callbackQuery.getMessage().getChatId());
        Integer messageId = callbackQuery.getMessage().getMessageId();

//        todo Добавить проверку на null
        return callbacksMap.get(callbackQuery.getData()).apply(chatId, messageId);
    }
}
