package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.CallType;

import java.util.Map;

public class MessageModel {

    private final String chatId;
    private final Integer messageId;
    private final CallType callType;
    private final Map<String, String> keyboardData;


    /**
     * Конструктор для имплементаций Callback
     */
    public MessageModel(String chatId, Integer messageId, CallType callType, Map<String, String> keyboardData) {
        this.chatId = chatId;
        this.messageId = messageId;
        this.callType = callType;
        this.keyboardData = keyboardData;
    }

    /**
     * Конструктор для имплементаций Button
     */
    public MessageModel(String chatId, CallType callType, Map<String, String> keyboardData) {
        this.chatId = chatId;
        this.callType = callType;
        this.keyboardData = keyboardData;

        this.messageId = null;   // Не используется
    }

    public String getChatId() {
        return chatId;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public CallType getCallType() {
        return callType;
    }

    public Map<String, String> getKeyboardData() {
        return keyboardData;
    }
}
