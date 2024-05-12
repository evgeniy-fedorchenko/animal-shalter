package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks.Callback;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.commands.Command;
import jakarta.annotation.Nullable;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.MainHandler;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.Map;

/**
 * The class represents the initial model for accumulating all the necessary parameters
 * and forming a {@link SendMessage} object that can be sent directly via Telegram
 *
 * @apiNote To convert a model into a {@code SendMessage} message object,
 * use {@link MessageUtils#applyCommand(MessageModel)} or {@link MessageUtils#applyCallback(MessageModel)}
 */
@Getter
public class MessageModel {

    /**
     * Id of the chat for whom the future message is intended
     */
    private final String chatId;

    /**
     * Id of the message for whom the future message is intended
     */
    private final Integer messageId;

    /**
     * An object of the enumeration class from which the text of the message will be taken for future sending
     */
    private final MessageData messageData;

    /**
     * The Map that will be converted into a keyboard object for the message being sent.<br>
     * <b>Key</b> - is the text that will be displayed on the button.<br>
     * <b>Value</b> -  is the  string id of the call that can be caught in the {@link MainHandler#handleCallbacks(CallbackQuery)}.
     * It must match the name of the Spring's bean that sends this button
     */
    @Nullable
    public final Map<String, String> keyboardData;

    /**
     * Constructor for implementation of {@link Callback}
     */
    public MessageModel(String chatId, Integer messageId, MessageData messageData, @Nullable Map<String, String> keyboardData) {
        this.chatId = chatId;
        this.messageId = messageId;
        this.messageData = messageData;
        this.keyboardData = keyboardData;
    }

    /**
     * Constructor for implementation of {@link Command}
     */
    public MessageModel(String chatId, MessageData messageData, @Nullable Map<String, String> keyboardData) {
        this.chatId = chatId;
        this.messageData = messageData;
        this.keyboardData = keyboardData;

        this.messageId = null;   // Не используется
//        todo Возможно стоит переделать на билдер, тк и keyboardData в паре кнопок не нужна
    }
}
