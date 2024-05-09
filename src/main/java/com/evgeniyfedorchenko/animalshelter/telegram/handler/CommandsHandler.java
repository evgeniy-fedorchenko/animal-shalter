package com.evgeniyfedorchenko.animalshelter.telegram.handler;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.commands.Command;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class CommandsHandler {

    private final Map<String, Command> commandsMap;

    public CommandsHandler(Command... commands) {
        commandsMap = new HashMap<>();
        Arrays.stream(commands)
                .forEach(command -> this.commandsMap.put(command.getTitle(), command));
    }

    public SendMessage handleCommands(Message message) {

        String command = message.getText();
        Command invokedCommand = commandsMap.get(command);

        return invokedCommand == null
                ? new SendMessage(message.getChatId().toString(), "Unknown command: " + command)
                : invokedCommand.apply(message);

    }

}
