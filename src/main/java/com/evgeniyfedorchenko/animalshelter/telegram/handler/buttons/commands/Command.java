package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.commands;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * A common interface for bot commands. Designed to work with such commands.
 * All commands that are present in the beta should extend this interface.
 */
public interface Command {

    /**
     * A method that executes the logic embedded in the bot-command and generates a response message.
     * Designed to send a <b>new</b> message to the corresponding chat
     * @param chatId Id of the chat for which the returned object will be created
     * @return Ready object for sending from app
     *
     * @see MessageUtils#applyCommand(MessageModel)
     */
    SendMessage apply(Long chatId);
}
