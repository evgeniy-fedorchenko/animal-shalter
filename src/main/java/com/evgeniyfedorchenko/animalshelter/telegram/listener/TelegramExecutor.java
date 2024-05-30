package com.evgeniyfedorchenko.animalshelter.telegram.listener;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

@Slf4j
@Component
public class TelegramExecutor extends DefaultAbsSender {

    @Value("${telegram.bot.token}")
    private String botToken;
    protected TelegramExecutor(@Value("${telegram.bot.token}") String botToken) {
//        this.botToken = botToken;
        super(new DefaultBotOptions(), botToken);
    }

    /**
     * Метод для непосредственной оправки сообщения на сервера Telegram. В случае ошибки отправки исключение
     * логируется как {@code TelegramApiException was thrown. Cause: ex.getMessage()} и подавляется
     *
     * @param messToSend {@code @NotNull} Объект сообщения, готового к отправке
     * @return true, если сообщение было успешно отправлено, иначе false
     */
    public boolean send(@NotNull BotApiMethod<? extends Serializable> messToSend) {

        try {
            execute(messToSend);
            return true;
        } catch (TelegramApiException ex) {
            log.error("TelegramApiException was thrown. Cause: {}", ex.getMessage());
            return false;
        }
    }

    public URL getPhotoUrl(PhotoSize photo) {

        GetFile getFileRequest = new GetFile(photo.getFileId());

        try {
            String fileUrl = execute(getFileRequest).getFileUrl(botToken);
            return URI.create(fileUrl).toURL();

        } catch (TelegramApiException ex) {
            log.error("Cannot invoke 'AbsSender.execute()'. Cause: {}", ex.getMessage());

        } catch (MalformedURLException ex) {
            log.error("Malformed URL. Cause: {}", ex.getMessage());
        }
        // FIXME 31.05.2024 00:39
        return null;
    }
}
