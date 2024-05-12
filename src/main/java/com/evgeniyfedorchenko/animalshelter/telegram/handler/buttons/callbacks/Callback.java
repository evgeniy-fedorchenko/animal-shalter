package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageUtils;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.commands.Command;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

/**
 * A common interface for bot callbacks. Designed to work with such calls.
 * All commands that are present in the beta should extend this interface.
 */
public interface Callback {

    /**
     * A method that executes the logic embedded in the bot's callback and generates a response message.
     * The method is designed to send instructions <b>for editing an existing message</b>, rather
     * than sending a new one. To send a new message, use the interface {@link Command}
     * @param chatId The id of the chat that you will be working with
     * @param messageId ID of the message to be edited
     * @return Ready object for sending from app
     *
     * @see MessageUtils#applyCallback(MessageModel)
     */
    EditMessageText apply(String chatId, Integer messageId);

}
