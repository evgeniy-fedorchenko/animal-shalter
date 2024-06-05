package com.evgeniyfedorchenko.animalshelter.backend.services;

import com.evgeniyfedorchenko.animalshelter.backend.repositories.ReportRepository;
import com.evgeniyfedorchenko.animalshelter.telegram.listener.TelegramExecutor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Slf4j
@Service
@AllArgsConstructor
public class TelegramServiceImpl implements TelegramService {

    private final TelegramExecutor telegramExecutor;
    private final ReportRepository reportRepository;

    @Override
    public boolean sendMessage(long chatId, String message) {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), message);
        return telegramExecutor.send(sendMessage);
    }

    @Override
    public void savePhoto(URL url, Long chatId) {

        byte[] photoData;
        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                InputStream is = url.openStream()
        ) {

            byte[] data = new byte[4096];
            int nRead;
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                baos.write(data, 0, nRead);
            }
            baos.flush();
            photoData = baos.toByteArray();
        } catch (IOException ex) {
            log.error("Cannot download file. Cause: {}", ex.getMessage());
            return;
        }
    }
}
