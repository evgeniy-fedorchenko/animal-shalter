package com.evgeniyfedorchenko.animalshelter.telegram.configuration;

import com.evgeniyfedorchenko.animalshelter.telegram.listener.TelegramBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class BotConfiguration {

    Logger logger = LoggerFactory.getLogger(BotConfiguration.class);
    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramBot telegramBot) {

        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramBot);
            return telegramBotsApi;

        } catch (TelegramApiException ex) {
            logger.error("Failed to register the bot, cause: {}", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }
}
