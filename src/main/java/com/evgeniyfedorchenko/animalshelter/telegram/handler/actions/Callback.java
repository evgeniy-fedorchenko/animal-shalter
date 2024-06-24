package com.evgeniyfedorchenko.animalshelter.telegram.handler.actions;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageUtils;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

/**
 * Общий интерфейс для обработки обратных вызовов. Разработан только для работы с такими вызовами.
 * Все кнопки, реагирующие на обратный вызов, должны расширять этот интерфейс
 */
public interface Callback {

    /**
     * Метод, предназначенный к выполнению по триггеру <b><i>обратного вызова</i></b>
     * Используется для отправки инструкций по редактированию существующего сообщения,
     * а не для отправки нового. Чтобы отправить новое сообщение, используйте кнопку,
     * реализующую интерфейс {@link SimpleApplicable}
     *
     * @param chatId    Идентификатор чата, с которым вы будете работать,
     * @param messageId Идентификатор редактируемого сообщения
     * @return готовый объект для отправки из приложения
     * @see MessageUtils#applyCallback(MessageModel)
     */
    EditMessageText apply(String chatId, Integer messageId);

}
