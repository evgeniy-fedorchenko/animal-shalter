package com.evgeniyfedorchenko.animalshelter.telegram.handler.commands;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface Command {

    String getTitle();

    SendMessage apply(Message message);
}
