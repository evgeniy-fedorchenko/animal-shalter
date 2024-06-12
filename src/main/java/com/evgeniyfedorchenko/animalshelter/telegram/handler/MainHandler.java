package com.evgeniyfedorchenko.animalshelter.telegram.handler;

import com.evgeniyfedorchenko.animalshelter.backend.services.AdopterService;
import com.evgeniyfedorchenko.animalshelter.backend.services.ReportService;
import com.evgeniyfedorchenko.animalshelter.backend.services.TelegramService;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageUtils;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks.Callback;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks.report.SendReportContinue;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks.report.SendingReportPart;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.commands.Command;
import com.evgeniyfedorchenko.animalshelter.telegram.listener.TelegramExecutor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.MessageData.ENDING_VOLUNTEER_CHAT;
import static com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks.report.SendingReportPart.PHOTO;
import static com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks.report.SendingReportPart.of;


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
    private final ReportService reportService;
    private final AdopterService adopterService;
    private final TelegramExecutor telegramExecutor;
    private final RedisTemplate<Long, Long> redisTemplate;
    private final SendReportContinue sendReportContinue;

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
            return applyUnknownUserAction(update);
        }
    }

    public BotApiMethod<? extends Serializable> applyUnknownUserAction(Update update) {
        String text = """
                Я прошу прощения, но кажется, я вас не понимаю \uD83E\uDD72
                Как насчет того, чтобы просто начать сначала?)
                👉 /start 👈""";


        if (update.hasCallbackQuery()) {

            CallbackQuery callbackQuery = update.getCallbackQuery();
            String chatId = String.valueOf(callbackQuery.getMessage().getChatId());

            EditMessageText editMessageText = new EditMessageText(text);
            editMessageText.setChatId(chatId);
            editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
            return editMessageText;

        } else {
            String chatId = String.valueOf(update.getMessage().getChatId());
            return new SendMessage(chatId, text);
        }
    }


    // TODO 12.06.2024 23:47 - изменить javadoc

    /**
     * Метод для получения фотографии от пользователя (нужно для функционала получения отчета). Обычно это сообщение
     * не содержит текста, а только фотографию. После попадания в метод отсюда сразу же возвращается экземпляр
     * {@code CompletableFuture<SendMessage>}, а выполнение метода продолжается асинхронно. После выбора самого
     * качественного изображения и создания объекта SendMessage - он сразу же помещается в главный поток в ушедший объект
     * {@code CompletableFuture<SendMessage>} и становится доступен. А сохранение самой фотки происходит в том же
     * асинхронном потоке, но его уже никто не ждет
     *
     * @param message Сообщение, из которого нужно достать фотографию
     */
    public Pair<byte[], MediaType> getPhotoData(Message message) {

        PhotoSize largestPhoto = message.getPhoto().stream()
                .max(Comparator.comparing(PhotoSize::getFileSize))
                .orElseThrow();

        return telegramExecutor.getPhotoDataPair(largestPhoto).orElseThrow();
    }

    public PartialBotApiMethod<Message> communicationWithVolunteer(Message message) {
        Long l = redisTemplate.opsForValue().get(message.getChatId());
        String answeringChatId = String.valueOf(l);

        Map<String, String> endingButton =
                Map.of(ENDING_VOLUNTEER_CHAT.getAnswer(), ENDING_VOLUNTEER_CHAT.getCallbackData());
        InlineKeyboardMarkup keyboardMarkup = MessageUtils.getMarkupWithOneLinesButtons(endingButton);

        if (message.hasSticker()) {
            String fileId = message.getSticker().getFileId();
            SendSticker sendSticker = new SendSticker(answeringChatId, new InputFile(fileId));
            sendSticker.setReplyMarkup(keyboardMarkup);

            return sendSticker;
        }

        if (message.hasPhoto()) {
            PhotoSize largestPhoto = message.getPhoto().stream()
                    .max(Comparator.comparing(PhotoSize::getFileSize))
                    .orElseThrow();

            SendPhoto sendPhoto = new SendPhoto(answeringChatId, new InputFile(largestPhoto.getFileId()));
            sendPhoto.setCaption(message.getCaption());
            sendPhoto.setReplyMarkup(keyboardMarkup);

            return sendPhoto;
        }

        SendMessage sendMessage = new SendMessage(answeringChatId, message.getText());
        sendMessage.setReplyMarkup(keyboardMarkup);
        return sendMessage;
    }

    public SendMessage sendReportProcess(Message message, Long specialBehaviorId) {

        if (message.hasText() && message.getText().length() > 500) {
            String tooLongMess = """
                    Ваше сообщение слишком длинное, пожалуйста сократите его
                    Максимальна длина - 500 символов, а ваше сообщение содержит %s символов
                    """.formatted(message.getText().length());
            return new SendMessage(String.valueOf(message.getChatId()), tooLongMess);
        }

        SendingReportPart sendingReportPart = of(specialBehaviorId);
        String suggestionToAdding = null;
        String messageData;
        MediaType mediaType;

        if (!adopterService.existAdopterWithChatId(message.getChatId())) {
            adopterService.addTrialAdopter(message);
            suggestionToAdding = "Упс! Кажется я еще не знаю Вас, как усыновителя животного! Пожалуйста, свяжитесь с волонтером, чтобы он добавил вас! Но пока что сделаем вид, что этой проблемы нет, я просто добавил вас автоматически. На продакшене эта функция будет удалена";
        }

       /* Передаем байты фотки в той же переменной (messageData), где передаем и текст сообщения. А content-type
          передаем в @Nullable-переменной (mediaType). Не самое элегантное решение, но зато самое лаконичное. Вместо
          того, чтоб заводить отдельный метод чисто под сохранение фотки. А еще плюс все проверки - выходит очень длинно */
        if (Objects.equals(sendingReportPart, PHOTO)) {

            Pair<byte[], MediaType> photoData = getPhotoData(message);
            messageData = Arrays.toString(photoData.getFirst());
            mediaType = photoData.getSecond();
        } else {
            mediaType = null;
            messageData = message.getText();
        }

//        Чтоб не ждать пока там всё проверится по репозиториям, создастся, свяжется и сохранится
        CompletableFuture.runAsync(() ->
                reportService.acceptReportPart(sendingReportPart, messageData, message.getChatId(), mediaType));

        SendMessage applied = sendReportContinue.apply(message.getChatId());
        if (suggestionToAdding != null) {
            applied.setText(suggestionToAdding + "\n" + "_".repeat(30) + "\n\n" + applied.getText());
        }
        return applied;
    }
}
