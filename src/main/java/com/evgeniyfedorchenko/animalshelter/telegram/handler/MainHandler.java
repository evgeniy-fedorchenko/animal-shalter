package com.evgeniyfedorchenko.animalshelter.telegram.handler;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.callbacks.Callback;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.commands.Command;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class MainHandler {

    private final Map<String, Command> commandsMap;
    private final Map<String, Callback> callbacksMap;

    public MainHandler(Command[] commands, Callback[] callbacks) {

        this.commandsMap = new HashMap<>();
        this.callbacksMap = new HashMap<>();

        Arrays.stream(commands).forEach(command -> this.commandsMap.put(command.getTitle(), command));
        Arrays.stream(callbacks).forEach(callback -> this.callbacksMap.put(callback.getCallbackData(), callback));
    }

    public SendMessage handleCommands(Message message) {

        String commandText = message.getText();
        Command command = commandsMap.get(commandText);

        return command == null
                ? new SendMessage(message.getChatId().toString(), "Unknown command: " + commandText)
                : command.apply(String.valueOf(message.getChatId()));
    }

    public EditMessageText handleCallbacks(CallbackQuery callbackQuery) {

//        Добавить проверку на null

        String chatId = String.valueOf(callbackQuery.getMessage().getChatId());
        Integer messageId = callbackQuery.getMessage().getMessageId();

        return callbacksMap.get(callbackQuery.getData()).apply(chatId, messageId);
    }
}
