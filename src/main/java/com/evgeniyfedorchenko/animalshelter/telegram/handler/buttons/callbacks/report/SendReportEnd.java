package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks.report;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageUtils;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.commands.Command;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageData.SEND_REPORT_END;
import static com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageData.START;

@AllArgsConstructor
@Component("SendReportEnd")
public class SendReportEnd implements Command {

    private final RedisTemplate<Long, Long> redisTemplate;
    @Override
    public SendMessage apply(Long chatId) {

        redisTemplate.delete(chatId);

        Map<String, String> keyboardData = new LinkedHashMap<>();
        keyboardData.put("Вернуться в главное меню", START.getCallbackData());

        MessageUtils messageUtils = new MessageUtils();
        MessageModel messageModel = MessageModel.builder()
                .chatId(chatId)
                .messageData(SEND_REPORT_END)
                .keyboardData(keyboardData)
                .build();

        return messageUtils.applyCommand(messageModel);
    }
}
