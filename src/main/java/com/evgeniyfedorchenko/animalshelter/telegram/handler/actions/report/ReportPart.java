package com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.report;

import com.evgeniyfedorchenko.animalshelter.backend.entities.Report;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageData;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Класс перечисления, представляющий собой отдельную часть отчета {@link Report}. Используется для разбивки
 * такого отчета, когда его присылает пользователь отдельными сообщениями, по одной части за раз
 */
@Getter
@AllArgsConstructor
public enum ReportPart {

    DIET     ("-1", MessageData.SEND_DIET, "Рацион питания"),
    HEALTH   ("-2", MessageData.SEND_HEALTH, "Самочувствие и привыкание"),
    BEHAVIOR ("-3", MessageData.SEND_BEHAVIOR, "Изменения в поведении"),
    PHOTO    ("-4", MessageData.SEND_PHOTO, "Фото животного");


    /**
     * Идентификатор части. Хранится в кеше Redis в то время, когда пользователь нажал
     * на кнопку "отправить часть отчета" для идентификации следующего сообщения
     * непосредственно, как части этого отчета
     */
    private final String partId;

    /**
     * Привязка к кнопке в главном классе перечисления заготовленных сообщения. Нужно, чтобы знать,
     * какое сообщения отправлять после получения каждой части сообщения
     */
    private final MessageData messageData;

    /**
     * Текст, который будет расположен на этой кнопке
     */
    private final String buttonText; // TODO 12.06.2024 17:17 - Это поле временно в этом енаме, оно будет перенесено в главный класс перечисления текстов кнопок

    /**
     * Метод для нахождения объекта {@code this} по его полю - идентификатору {@code partId}
     * @param partId идентификатор объекта {@code ReportPart}, который нужно найти
     * @return объект {@code ReportPart}, найденный по переданному идентификатору. Или {@code null},
     * по указанному идентификатору не найдено объекта
     */
    public static @Nullable ReportPart of(String partId) {
        for (ReportPart part : ReportPart.values()) {
            if (part.getPartId().equals(partId)) {
                return part;
            }
        }
        return null;
    }
}
