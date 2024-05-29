package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.commands;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * Общий интерфейс для команд ботов. Разработан для работы с такими командами.
 * Все команды, которые присутствуют в бета-версии, должны реализовывать этот интерфейс.
 */
public interface Command {

    /**
     * Метод, который выполняет логику, заложенную в bot-команду, и генерирует ответное сообщение.
     * Предназначен для отправки <b>нового</b> сообщения в соответствующий чат
     * @param chatId Id чата, для которого будет создан возвращаемый объект
     * @return готовый объект для отправки из приложения
     *
     * @see MessageUtils#applyCommand(MessageModel)
     */
    SendMessage apply(Long chatId);
}
