package com.evgeniyfedorchenko.animalshelter.telegram.listener;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.UpdateDistributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final Logger logger = LoggerFactory.getLogger(TelegramBot.class);

    private final UpdateDistributor updateDistributor;

    public TelegramBot(@Value("${telegram.bot.token}") String botToken,
                       UpdateDistributor updateDistributor) {
        super(botToken);
        this.updateDistributor = updateDistributor;
    }

    @Override
    public String getBotUsername() {
        return "animal_shelter_helper_bot";
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update != null) {

            logger.info("Processing has BEGUN for updateID {}", update.getUpdateId());

            BotApiMethod<? extends Serializable> distribute = updateDistributor.distribute(update);
            send(distribute);

            logger.info("Processing has successfully ENDED for updateID {}", update.getUpdateId());
        }
    }

//    Нужно ли тут synchronized? Ну типа чтоб не перемешивались отправляемые сообщения
//    из разных потоков, когда несколько людей обратились к боту одновременно
    private synchronized void send(BotApiMethod<? extends Serializable> messToSend) {

        try {
            execute(messToSend);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }
}