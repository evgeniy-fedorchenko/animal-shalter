package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageUtils;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.commands.Command;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

/**
 * Общий интерфейс для обоих обратных вызовов. Разработан для работы с такими вызовами.
 * Все команды, которые присутствуют в бета-версии, должны расширять этот интерфейс.
 */
public interface Callback {

    /**
     * Метод, который выполняет логику, заложенную в обратном вызове бота, и генерирует ответное сообщение.
     * Метод предназначен для отправки инструкций по редактированию существующего сообщения, а
     * не для отправки нового. Чтобы отправить новое сообщение, используйте команду интерфейса {@link Command}
     *
     * @param chatId    Идентификатор чата, с которым вы будете работать,
     * @param messageId Идентификатор редактируемого сообщения
     * @return готовый объект для отправки из приложения
     * @see MessageUtils#applyCallback(MessageModel)
     */
    EditMessageText apply(Long chatId, Integer messageId);

}
