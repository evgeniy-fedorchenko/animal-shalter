package com.evgeniyfedorchenko.animalshelter.backend.services;

import com.evgeniyfedorchenko.animalshelter.backend.repositories.ReportRepository;
import com.evgeniyfedorchenko.animalshelter.telegram.listener.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

@Slf4j
@Service
public class TelegramServiceImpl implements TelegramService {

    @Value("${telegram.bot.token}")
    private String botToken;

    private final TelegramBot telegramBot;
    private final ReportRepository reportRepository;

    //    К сожалению, мне не удалось решить эту циклическую зависимость по-другому :(
    public TelegramServiceImpl(@Lazy TelegramBot telegramBot,
                               ReportRepository reportRepository) {
        this.telegramBot = telegramBot;
        this.reportRepository = reportRepository;
    }

    @Override
    public boolean sendMessage(long chatId, String message) {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), message);
        return telegramBot.send(sendMessage);
    }

    @Override
    public void savePhoto(PhotoSize photo, Long chatId) {

        try {
            GetFile getFileRequest = new GetFile(photo.getFileId());
            String fileUrl = telegramBot.execute(getFileRequest).getFileUrl(botToken);
            URL url = URI.create(fileUrl).toURL();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream is = url.openStream();
            try (baos; is) {

                byte[] data = new byte[4096];
                int nRead;
                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    baos.write(data, 0, nRead);
                }
                baos.flush();
            }

        } catch (TelegramApiException ex) {
            log.error("Cannot invoke 'AbsSender.execute()'. Cause: {}", ex.getMessage());
        } catch (MalformedURLException ex) {
            log.error("Malformed URL. Cause: {}", ex.getMessage());
        } catch (IOException ex) {
            log.error("Cannot download file. Cause: {}", ex.getMessage());
        }
=======
import com.evgeniyfedorchenko.animalshelter.telegram.listener.TelegramBot;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@AllArgsConstructor
@Service
public class TelegramServiceImpl implements TelegramService {

    private final TelegramBot telegramBot;

    @Override
    public boolean sendMessage(long chatId, String message) {

        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), message);
        try {
            telegramBot.execute(sendMessage);
        } catch (TelegramApiException ex) {
            log.error("Filed to send message to adopter about his bad report. Cause: {}", ex.getMessage());
//            todo придумать как вернуть тут bad_gateway
        }
        return false;
    }
}
