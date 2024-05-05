package com.evgeniyfedorchenko.animalshelter.telegram.handler.callbacks;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

public interface Callback {

    String getCallbackData();

    EditMessageText apply(String chatId, Integer messageId);

}
