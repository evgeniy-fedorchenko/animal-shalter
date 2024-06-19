package com.evgeniyfedorchenko.animalshelter.telegram.handler.actions;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * Интерфейс для обработки простых сообщений (в том числе команд бота). Предназначен для простых реакций на действия
 * юзера, которые ничего не предусматривают (только наличие {@code chatId} для идентификации этого юзера).
 * Все команды, реализуют этот интерфейс, так как по факту являются простыми реакциями
 */
public interface SimpleApplicable {

    /**
     * Метод, который выполняет логику, заложенную в команду кода, и генерирует ответное сообщение.
     * Предназначен для отправки <b>нового</b> сообщения в соответствующий чат
     * @param chatId Id чата, для которого будет создан возвращаемый объект
     * @return готовый объект для отправки из приложения
     *
     * @see MessageUtils#applySimpled(MessageModel)
     */
    SendMessage apply(String chatId);
}
