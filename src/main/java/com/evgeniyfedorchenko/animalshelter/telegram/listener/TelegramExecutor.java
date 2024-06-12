package com.evgeniyfedorchenko.animalshelter.telegram.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Optional;

@Slf4j
@Component
public class TelegramExecutor extends DefaultAbsSender {

    @Value("${telegram.bot.token}")
    private String botToken;

    protected TelegramExecutor(@Value("${telegram.bot.token}") String botToken) {
        super(new DefaultBotOptions(), botToken);
    }

    /**
     * Метод для непосредственной оправки сообщения на сервера Telegram. В случае ошибки отправки исключение
     * логируется как {@code TelegramApiException was thrown. Cause: ex.getMessage()} и подавляется
     *
     * @param method {@code @NotNull} Объект сообщения, готового к отправке
     * @return true, если сообщение было успешно отправлено, иначе false
     */
    public Boolean send(PartialBotApiMethod<?> method) {

//        Нет метода execute(), который принимает родителя SendSticker и SendPhoto и тем более общего с BotApiMethod<>
        boolean suc = true;
        try {
            switch (method) {
                case SendSticker sendSticker -> execute(sendSticker);
                case SendPhoto sendPhoto -> execute(sendPhoto);
                case BotApiMethod<? extends Serializable> other -> execute(other);
                default -> {
                    log.error("Unexpected value to DefaultAbsSender.execute(): {}", method.getClass());
                    suc = false;
                }
            }
            return suc;
        } catch (TelegramApiException ex) {
            log.error("TelegramApiException was thrown. Cause: {}", ex.getMessage());
            return false;
        }
    }

    public Optional<Pair<byte[], MediaType>> getPhotoDataPair(PhotoSize photo) {

        try (InputStream is = downloadFileAsStream(execute(new GetFile(photo.getFileId())))) {
//             TODO 10.06.2024 21:18 - пока поставим заглушку на content-type, пока непонятно как его определять
            return Optional.of(Pair.of(is.readAllBytes(), MediaType.IMAGE_PNG));

        } catch (TelegramApiException | IOException ex) {
            log.error("{} was thrown. Cause: {}", ex.getClass().getSimpleName(), ex.getMessage());
            return Optional.empty();
        }
    }
}
