package com.evgeniyfedorchenko.animalshelter.telegram.configuration;

import com.evgeniyfedorchenko.animalshelter.telegram.listener.UpdateListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class BotConfiguration {

    @Value("${telegram.bot.token}")
    private String token;

    @Bean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new UpdateListener(token));
        return telegramBotsApi;
    }
}
