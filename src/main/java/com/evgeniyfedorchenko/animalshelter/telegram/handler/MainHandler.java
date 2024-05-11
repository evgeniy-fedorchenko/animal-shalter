package com.evgeniyfedorchenko.animalshelter.telegram.handler;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.Callback;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.Command;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Map;

@Component
public class MainHandler {

    @Autowired
    private ApplicationContext applicationContext;


    public SendMessage handleCommands(Message message) {

        Map<String, Command> commandsMap = applicationContext.getBeansOfType(Command.class);

        String commandText = message.getText();
        Command command = commandsMap.get(commandText);

        return command == null
                ? new SendMessage(message.getChatId().toString(), "Unknown command: " + commandText)
                : command.apply(String.valueOf(message.getChatId()));
    }

    public EditMessageText handleCallbacks(CallbackQuery callbackQuery) {

//        Чтобы колбек подхватился правильно нужно чтобы были равны:
//        - ключ в callbacksMap, задается как в параметре аннотации @Component
//        - callbackQuery().getData() колбек вернувшийся с прошлой кнопки (задается в CallType.getTitle())

        Map<String, Callback> callbacksMap = applicationContext.getBeansOfType(Callback.class);

        String chatId = String.valueOf(callbackQuery.getMessage().getChatId());
        Integer messageId = callbackQuery.getMessage().getMessageId();

//        todo Добавить проверку на null
        return callbacksMap.get(callbackQuery.getData()).apply(chatId, messageId);
    }
}
