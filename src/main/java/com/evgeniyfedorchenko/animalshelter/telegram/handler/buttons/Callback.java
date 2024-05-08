package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

public interface Callback {

    default String getCallbackName() {
        return this.getClass().getSimpleName();
    }

    EditMessageText apply(String chatId, Integer messageId);

}
