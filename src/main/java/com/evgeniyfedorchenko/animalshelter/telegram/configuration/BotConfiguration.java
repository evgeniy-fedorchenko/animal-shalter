package com.evgeniyfedorchenko.animalshelter.telegram.configuration;

import com.evgeniyfedorchenko.animalshelter.telegram.listener.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Telegram bot configuration class
 */
@Slf4j
@Configuration
public class BotConfiguration {

    /**
     * A method that configures the bin for <b>Spring context</b> based
     * on the collected object {@link TelegramBot} and registers it on telegram servers
     * @param telegramBot Initialized object to register
     * @return System object for work on Telegram rules
     */
    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramBot telegramBot) {

        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramBot);
            return telegramBotsApi;

        } catch (TelegramApiException ex) {
            log.error("Failed to register the bot, cause: {}", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }
}
