package com.evgeniyfedorchenko.animalshelter.telegram.handler.commands;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * Общий интерфейс для команд (и потенциально для обычных кнопок).
 * Команды, расширяющие этот интерфейс заканчиваются на -Command
 */
public interface Command {

    /**
     * @return Возвращает строковое представление вызова. Так, как оно приходит из телеграма,
     * чтобы можно было отловить по этому параметру
     */
    String getTitle();

    /**
     * Метод, который выполняет логику, заложенную в команду и формирует ответное сообщение
     * @param chatId Идентификатор чата, в который будет отправлено сообщение
     * @return готовый объект {@link SendMessage} для отправки
     */
    SendMessage apply(String chatId);
}
