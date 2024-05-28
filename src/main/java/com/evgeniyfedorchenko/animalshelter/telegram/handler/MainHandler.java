package com.evgeniyfedorchenko.animalshelter.telegram.handler;

import com.evgeniyfedorchenko.animalshelter.backend.services.TelegramService;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks.Callback;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.commands.Command;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * The main handler of messages from the bot's Telegrams. Contains methods
 * for handling all possible scenarios provided by the application logic
 */

//@AllArgsConstructor
@Component
public class MainHandler {

    private final ApplicationContext applicationContext;
    private final TelegramService telegramService;

    public MainHandler(ApplicationContext applicationContext,
                       TelegramService telegramService) {
        this.applicationContext = applicationContext;
        this.telegramService = telegramService;
    }


// FIXME 25.05.2024 20:22 - поправить джавадок

    /**
     * A method for processing <b>commands</b> sent from a Telegram bot. The method
     * searches for registered implementations of {@link Command} and matches them with the message text
     *
     * @param message Object of type {@code Message} for processing
     * @return A ready-made message object to send via Telegram bot
     */

    public SendMessage handleCommands(Update update) {

        Message message = update.getMessage();

        Map<String, Command> commandsMap = applicationContext.getBeansOfType(Command.class);
        String commandText = message.getText();
        Command command = commandsMap.get(commandText);

        return command == null
                ? new SendMessage(message.getChatId().toString(), "Unknown command: " + commandText)
                : command.apply(message.getChatId());
    }

    // FIXME 25.05.2024 20:22 - поправить джавадок

    /**
     * A method for processing <b>callbacks</b> sent from a Telegram bot. The method
     * searches for registered implementations of {@link Callback} and matches them with the message text
     *
     * @param callbackQuery Object of {@code CallbackQuery} for processing
     * @return This object does not send a new message, but only modifies an existing
     * one containing {@code this.callbackQuery.getMessage().getMessageId()}
     */
    public EditMessageText handleCallbacks(Update update) {

        Map<String, Callback> callbacksMap = applicationContext.getBeansOfType(Callback.class);

        CallbackQuery callbackQuery = update.getCallbackQuery();
        Callback callback = callbacksMap.get(callbackQuery.getData());
        Long chatId = callbackQuery.getMessage().getChatId();

        if (callback != null) {
            Integer messageId = callbackQuery.getMessage().getMessageId();
            return callback.apply(chatId, messageId);

        } else {
            return (EditMessageText) applyUnknownUserAction(update, chatId);
        }
    }

    /* Кажется в будущем понадобится обрабатывать такую же ошибку и для методов которые
       возвращают SendMessage, так что вернем сразу обобщенный тип */
    public BotApiMethod<? extends Serializable> applyUnknownUserAction(Update update, Long chatId) {
        String text = """
                Я прошу прощения, но кажется, я вас не понимаю \uD83E\uDD72
                Как насчет того, чтобы просто начать сначала?)
                👉 /start 👈""";

        if (update.hasCallbackQuery()) {

            CallbackQuery callbackQuery = update.getCallbackQuery();

            EditMessageText editMessageText = new EditMessageText(text);
            editMessageText.setChatId(chatId);
            editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
            return editMessageText;

        } else {
            return new SendMessage(chatId.toString(), text);
        }
    }

    @Async
    public CompletableFuture<SendMessage> savePhoto(Message message) {
//        TelegramBot telegramBot = applicationContext.getBean("TelegramBot", TelegramBot.class);

        PhotoSize largestPhoto = message.getPhoto().stream()
                .max((photo1, photo2) -> Integer.compare(
                        photo2.getWidth() * photo2.getHeight(),
                        photo1.getWidth() * photo1.getHeight()))
                .orElseThrow();

        CompletableFuture<SendMessage> future = CompletableFuture.supplyAsync(() ->
                new SendMessage(String.valueOf(message.getChatId()), "photo saved") // TODO 28.05.2024 21:10 - заменить текст
        );

        future.thenAcceptAsync(_ -> telegramService.savePhoto(largestPhoto, message.getChatId()));
        return future;
    }
}
