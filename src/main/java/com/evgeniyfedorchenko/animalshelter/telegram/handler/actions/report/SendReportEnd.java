package com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.report;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageModel;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageUtils;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.SimpleApplicable;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageData.SEND_REPORT_END;
import static com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageData.START;

@AllArgsConstructor
@Component("SendReportEnd")
public class SendReportEnd implements SimpleApplicable {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Метод для создания сообщения последнего этапа процесса получения отчета от юзера. Происходит удаление
     * его из кеша {@code RedisTemplate} и далее он считается обычным пользователем, способным пользоваться
     * на равне с другими
     * @param chatId Id чата, для которого будет создан возвращаемый объект
     * @return Готовый объект сообщения для отправки посредством телеграм-бота
     */
    @Override
    public SendMessage apply(String chatId) {

        redisTemplate.delete(chatId);

        Map<String, String> keyboardData = new LinkedHashMap<>();
        keyboardData.put("Вернуться в главное меню", START.getCallbackData());

        MessageUtils messageUtils = new MessageUtils();
        MessageModel messageModel = MessageModel.builder()
                .chatId(chatId)
                .messageData(SEND_REPORT_END)
                .keyboardData(keyboardData)
                .build();

        return messageUtils.applySimpled(messageModel);
    }
}
