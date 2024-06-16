package com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.report;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageData;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReportPart {

    DIET     ("-1", MessageData.SEND_DIET, "Рацион питания"),
    HEALTH   ("-2", MessageData.SEND_HEALTH, "Самочувствие и привыкание"),
    BEHAVIOR ("-3", MessageData.SEND_BEHAVIOR, "Изменения в поведении"),
    PHOTO    ("-4", MessageData.SEND_PHOTO, "Фото животного");

    private final String partId;
    private final MessageData messageData;
    private final String buttonText; // TODO 12.06.2024 17:17 - Это поле временно в этом енаме, оно будет перенесено в главный класс перечисления текстов кнопок

    public static ReportPart of(String partId) {
        for (ReportPart part : ReportPart.values()) {
            if (part.getPartId().equals(partId)) {
                return part;
            }
        }
        return null;
    }
}
