package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.CallType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class MessageUtils {

    private final Logger logger = LoggerFactory.getLogger(MessageUtils.class);

    public static final Map<String, CallType> COMPLIANCE_TABLE = new HashMap<>(Map.of(
//        todo связать тут CallType и их кнопки. И когда нужно с помощью одного получить другое - обращаться сюда
    ));

    public EditMessageText applyCallback(MessageModel messageModel) {

        EditMessageText editMessage = new EditMessageText(messageModel.getCallType().getAnswer());
        editMessage.setChatId(messageModel.getChatId());
        editMessage.setMessageId(messageModel.getMessageId());

        if (messageModel.getKeyboardData() != null) {
            InlineKeyboardMarkup keyboardMarkup = getMarkupWithOneLinesButtons(messageModel.getKeyboardData());
            editMessage.setReplyMarkup(keyboardMarkup);
        }
        return editMessage;
    }

    public SendMessage applyCommand(MessageModel messageModel) {

        SendMessage sendMessage = new SendMessage(
                messageModel.getChatId(),
                messageModel.getCallType().getAnswer()
        );

        if (messageModel.getKeyboardData() != null) {
            InlineKeyboardMarkup keyboardMarkup = getMarkupWithOneLinesButtons(messageModel.getKeyboardData());
            sendMessage.setReplyMarkup(keyboardMarkup);
        }
        return sendMessage;
    }

    /**
     * Метод для получения разметки клавиатуры по типу "одна кнопка на одну линию". Каждая кнопка будет находиться
     * на отдельной линии
     *
     * @param keyboardData Карта с данными для кнопок. Ключи - текст, который будет размещен на кнопках.
     *                     Значения - текст колбека, который будет отправлен по нажатию на кнопку
     * @return Готовая разметка для клавиатуры
     */
    public static InlineKeyboardMarkup getMarkupWithOneLinesButtons(Map<String, String> keyboardData) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

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

    public void setUrlToButton(EditMessageText editMessage) {

          /* Внешний лист содержит 5 внутренних листов, каждый внутренний лист содержит по одной кнопке
             Находим индекс кнопки с текстом "Наш сайт" и удаляем её. Если не находим -
             - просто оставляем клавиатуру, как есть - готовую, но без ссылки */

        InlineKeyboardMarkup keyboardMarkup = editMessage.getReplyMarkup();
        List<List<InlineKeyboardButton>> keyboardList = keyboardMarkup.getKeyboard();
        String url = "https://predannoeserdce.ru/";

        IntStream.range(0, keyboardList.getFirst().size())
                .filter(idx -> keyboardList.get(idx).getFirst().getText().equals("Наш сайт"))
                .findFirst()
                .ifPresentOrElse(idx -> {
                    InlineKeyboardButton newButton = new InlineKeyboardButton("Наш сайт");
                    newButton.setUrl(url);
                    keyboardList.set(idx, new ArrayList<>(List.of(newButton)));
                    keyboardMarkup.setKeyboard(keyboardList);
                    editMessage.setReplyMarkup(keyboardMarkup);

                }, () -> logger.warn("Filed to add a website-link because the button was not found"));
    }
}
