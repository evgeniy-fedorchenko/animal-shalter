package com.evgeniyfedorchenko.animalshelter.telegram.handler;

import com.evgeniyfedorchenko.animalshelter.backend.services.TelegramService;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks.Callback;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.commands.Command;
import com.evgeniyfedorchenko.animalshelter.telegram.listener.TelegramExecutor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Основной обработчик сообщений из Телеграмм-бота. Содержит методы
 * для обработки всех возможных сценариев, предусмотренных логикой приложения
 */

@Slf4j
@AllArgsConstructor
@Component
public class MainHandler {

    private final ApplicationContext applicationContext;
    private final TelegramService telegramService;
    private final TelegramExecutor telegramExecutor;

    /**
     * Метод, обрабатывающий объекты {@code Command} полученные от Телеграм-бота. Метод ищет зарегистрированные
     * в ApplicationContext имплементации интерфейса {@link Command} и выполняет логику в соответствии с командой
     *
     * @param update Объект для обработки, полученный от Телеграм-бота
     * @return Сообщение, готовое к отправке с помощью Телеграм-бота
     */
    public SendMessage handleCommands(Update update) {

        Message message = update.getMessage();

        Map<String, Command> commandsMap = applicationContext.getBeansOfType(Command.class);
        String commandText = message.getText();
        Command command = commandsMap.get(commandText);

        return command == null
                ? new SendMessage(message.getChatId().toString(), "Unknown command: " + commandText)
                : command.apply(message.getChatId());
    }


    /**
     * Метод, обрабатывающий объекты {@code Callback} полученные от Телеграм-бота. Метод ищет зарегистрированные
     * в ApplicationContext имплементации интерфейса {@link Callback} и выполняет логику в соответствии с командой
     *
     * @param update Объект, полученный от Телеграм-бота, для обработки и выполнения логики на основе его содержания
     * @return Этот объект является не новым сообщением, а изменением другого сообщения, содержащего идентификатор
     * {@code this.callbackQuery.getMessage().getMessageId()}
     */
    public BotApiMethod<? extends Serializable> handleCallbacks(Update update) {

        Map<String, Callback> callbacksMap = applicationContext.getBeansOfType(Callback.class);

        CallbackQuery callbackQuery = update.getCallbackQuery();
        Callback callback = callbacksMap.get(callbackQuery.getData());
        Long chatId = callbackQuery.getMessage().getChatId();

        if (callback != null) {
            Integer messageId = callbackQuery.getMessage().getMessageId();
            return callback.apply(chatId, messageId);

        } else {
            return applyUnknownUserAction(update, chatId);
        }
    }

    /* Кажется в будущем понадобится обрабатывать такую же ошибку и для методов которые
       возвращают SendMessage, так что вернем сразу обобщенный тип */
    public BotApiMethod<? extends Serializable> applyUnknownUserAction(Update update, Long chatId) {
        String text = """
                Я прошу прощения, но кажется, я вас не понимаю \uD83E\uDD72
                Как насчет того, чтобы просто начать сначала?)
                👉 /start 👈""";

        if (update.hasCallbackQuery()) {

            CallbackQuery callbackQuery = update.getCallbackQuery();

            EditMessageText editMessageText = new EditMessageText(text);
            editMessageText.setChatId(chatId);
            editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
            return editMessageText;

        } else {
            return new SendMessage(chatId.toString(), text);
        }
    }


    /**
     * Метод для получения фотографии от пользователя (нужно для функционала получения отчета). Обычно это сообщение
     * не содержит текста, а только фотографию. После попадания в метод отсюда сразу же возвращается экземпляр
     * {@code CompletableFuture<SendMessage>}, а выполнение метода продолжается асинхронно. После выбора самого
     * качественного изображения и создания объекта SendMessage - он сразу же помещается в главный поток в ушедший объект
     * {@code CompletableFuture<SendMessage>} и становится доступен. А сохранение самой фотки происходит в том же
     * асинхронном потоке, но его уже никто не ждет
     *
     * @param message Сообщение, из которого нужно достать фотографию
     * @return Экземпляр CompletableFuture<SendMessage> внутри которого будет помещен целевой объект
     */
    @Async
    public CompletableFuture<SendMessage> savePhoto(Message message) {

        PhotoSize largestPhoto = message.getPhoto().stream()
                .max((photo1, photo2) -> Integer.compare(
                        photo2.getWidth() * photo2.getHeight(),
                        photo1.getWidth() * photo1.getHeight()))
                .orElseThrow();

        CompletableFuture<SendMessage> future = CompletableFuture.supplyAsync(() -> {
            return new SendMessage(String.valueOf(message.getChatId()), "photo saved"); // TODO 28.05.2024 21:10 - заменить текст
        });

        future.thenAcceptAsync(_ -> {
            URL photoUrl = telegramExecutor.getPhotoUrl(largestPhoto);
            telegramService.savePhoto(photoUrl, message.getChatId());
        });
        return future;
    }
}
