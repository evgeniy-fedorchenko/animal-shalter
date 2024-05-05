package com.evgeniyfedorchenko.animalshelter.telegram.handler;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

public class KeyboardUtils {

// todo связать тексты кнопок и их колбеки в карту и хранить её тут, вместо того,
//      чтобы передавать постоянно и то и другое. А лучше вообще прямо в CallType
//      определить в конструкторе сразу и колбеки тоже

    /**
     * Метод для получения разметки клавиатуры по типу "одна кнопка на одну линию". Каждая кнопка будет находиться
     * на отдельной линии
     * @param keyboardData Карта с данными для кнопок. Ключи - текст, который будет размещен на кнопках.
     *                    Значения - текст колбека, который будет отправлен по нажатию на кнопку
     * @return Готовая разметка для клавиатуры
     */
    public static InlineKeyboardMarkup getMarkupWithOneLinesButtons(Map<String, String> keyboardData) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

//        Нужно развернуть мапу
//        List<Map.Entry<String, String>> entries = new ArrayList<>(keyboardData.entrySet());
//        Collections.reverse(entries);
//
//        entries.forEach(entry -> {
//            List<InlineKeyboardButton> row = new ArrayList<>();
//            row.add(getButton(entry.getKey(), entry.getValue()));
//            rowList.add(row);
//        });

        keyboardData.forEach((key, value) -> {
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(getButton(key, value));
            rowList.add(row);
        });

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    private static InlineKeyboardButton getButton(String buttonTitle, String textToCallback) {

        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(buttonTitle);
        inlineKeyboardButton.setCallbackData(textToCallback);

        return inlineKeyboardButton;
    }
}
