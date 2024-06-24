package com.evgeniyfedorchenko.animalshelter.telegram.handler;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.Callback;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

/**
 * Класс представляет собой исходную модель для накопления всех необходимых параметров
 * и формирования объекта {@link SendMessage} или {@link EditMessageText}, который может
 * быть отправлен напрямую через Telegram
 *
 * @apiNote Чтобы преобразовать модель в объект сообщения {@code SendMessage} или {@code EditMessageText},
 * используйте {@link MessageUtils#applySimpled(MessageModel)} или {@link MessageUtils#applyCallback(MessageModel)}
 * соответственно
 */
@Getter
@Builder
public class MessageModel {

    /**
     * Идентификатор чата, для которого предназначено сообщение
     */
    private final String chatId;

    /**
     * Идентификатор сообщения, которое будет изменено на новое.
     * Актуально для имплементаций {@link Callback}
     */
    @Nullable
    private final Integer messageId;

    /**
     * Объект enumeration, из которого будет взят текст сообщения для последующей отправки
     */
    private final MessageData messageData;

    /**
     * Карта, которая будет преобразована в объект клавиатуры для отправляемого сообщения.<br>
     * <b>Key</b> - Текст, который будет отображаться на кнопке<br>
     * <b>Value</b> - Строковый идентификатор вызова, который может быть перехвачен в {@link MainHandler#handleCallbacks(Update)}.
     * Он должен совпадать с именем компонента источника, который отправляет эту кнопку
     */
    @Nullable
    private final Map<String, String> keyboardData;

}
