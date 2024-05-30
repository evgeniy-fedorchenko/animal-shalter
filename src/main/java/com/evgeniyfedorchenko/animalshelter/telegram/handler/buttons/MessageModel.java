package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.MainHandler;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

/**
 * Класс представляет собой исходную модель для накопления всех необходимых параметров
 * и формирования объекта {@link SendMessage}, который может быть отправлен напрямую через Telegram
 *
 * @apiNote Чтобы преобразовать модель в объект сообщения {@code SendMessage},
 * используйте {@link MessageUtils#applyCommand(MessageModel)} или {@link MessageUtils#applyCallback(MessageModel)}
 */
@Getter
@Builder
public class MessageModel {

    /**
     * Идентификатор чата, для которого предназначено будущее сообщение
     */
    private final Long chatId;

    /**
     * Идентификатор сообщения, для которого предназначено будущее сообщение
     */
    private final Integer messageId;

    /**
     * Объект класса enumeration, из которого будет взят текст сообщения для последующей отправки
     */
    private final MessageData messageData;

    /**
     * Карта, которая будет преобразована в объект клавиатуры для отправляемого сообщения.<br>
     * <b>Key</b> - Текст, который будет отображаться на кнопке<br>
     * <b>Value</b> - Строковый идентификатор вызова, который может быть перехвачен в {@link MainHandler#handleCallbacks(Update)}.
     * Оно должно совпадать с именем компонента источника, который отправляет эту кнопку
     */
    @Nullable
    public final Map<String, String> keyboardData;

}
