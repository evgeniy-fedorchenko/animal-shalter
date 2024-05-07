package com.evgeniyfedorchenko.animalshelter.telegram.handler;

import java.util.Map;

public class ChatAction {

    private final String chatId;
    private final Integer messageId;
    private final Map<String, String> keyboardData;
    private final CallType callType;

    public ChatAction(String chatId, Integer messageId, Map<String, String> keyboardData, CallType callType) {
        this.chatId = chatId;
        this.messageId = messageId;
        this.keyboardData = keyboardData;
        this.callType = callType;
    }

    public String getChatId() {
        return chatId;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public Map<String, String> getKeyboardData() {
        return keyboardData;
    }

    public CallType getCallType() {
        return callType;
    }
}
