package com.evgeniyfedorchenko.animalshelter.telegram.handler;

import com.evgeniyfedorchenko.animalshelter.backend.services.AdopterService;
import com.evgeniyfedorchenko.animalshelter.backend.services.ReportService;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.Callback;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.SimpleApplicable;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.VolunteerChattingEnd;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.report.ReportPart;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.report.SendReportContinue;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.report.SendReportEnd;
import com.evgeniyfedorchenko.animalshelter.telegram.listener.TelegramExecutor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
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
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageData.ENDING_VOLUNTEER_CHAT;
import static com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.report.ReportPart.PHOTO;
import static com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.report.ReportPart.of;


/**
 * Основной обработчик сообщений из Телеграмм-бота. Содержит методы
 * для обработки всех возможных сценариев, предусмотренных логикой приложения
 */
@Slf4j
@AllArgsConstructor
@Component
public class MainHandler {

    private final ApplicationContext applicationContext;
    private final ReportService reportService;
    private final AdopterService adopterService;
    private final TelegramExecutor telegramExecutor;
    private final SendReportContinue sendReportContinue;

    /**
     * Метод, обрабатывающий объекты {@code Command} полученные от Телеграм-бота. Метод ищет зарегистрированные
     * в ApplicationContext имплементации интерфейса {@link SimpleApplicable} и выполняет логику в соответствии с командой
     *
     * @param update Объект для обработки, полученный от Телеграм-бота
     * @return Сообщение, готовое к отправке с помощью Телеграм-бота
     */
    public SendMessage handleCommands(Update update) {

        Message message = update.getMessage();

        Map<String, SimpleApplicable> commandsMap = applicationContext.getBeansOfType(SimpleApplicable.class);
        String commandText = message.getText();
        SimpleApplicable simpleApplicable = commandsMap.get(commandText);

        return simpleApplicable == null
                ? new SendMessage(message.getChatId().toString(), "Unknown command: " + commandText)
                : simpleApplicable.apply(String.valueOf(message.getChatId()));
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

        CallbackQuery callbackQuery = update.getCallbackQuery();
        String chatId = String.valueOf(callbackQuery.getMessage().getChatId());

        // TODO 16.06.2024 18:21 - почему не достаю бин через контекст?
        if (callbackQuery.getData().equals("VolunteerChattingEnd")) {
            return ((VolunteerChattingEnd) applicationContext.getBean("VolunteerChattingEnd")).apply(chatId);
        }

        Map<String, Callback> callbacksMap = applicationContext.getBeansOfType(Callback.class);
        Callback callback = callbacksMap.get(callbackQuery.getData());

        return callback == null
                ? applyUnknownUserAction(update)
                : callback.apply(chatId, callbackQuery.getMessage().getMessageId());
    }

    /**
     * Метод для обработки неизвестных действий от юзера - ошибочных команд или сообщений, а так же устаревших
     * колбеков. Логика метода сводится к генерации нового сообщения (или к изменению ошибочного, в случае
     * получения неизвестно колбека (редко)).
     *
     * @param update Непустой объект для получения ChatId. Метод получает весь объект {@code Update update},
     *               а не только {@code Long chatId}, чтобы определить, какого потомка
     *               {@code BotApiMethod<? extends Serializable>} отправлять
     * @return объект {@code} BotApiMethod<? extends Serializable>, по факту суженый до экземпляра класса
     * {@code SendMessage} или {@code EditMessageText}, в зависимости от ожиданий вызывающего метода
     */
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

    /**
     * Метод для получения байтов фотографии от пользователя (нужно для функционала получения отчета) с серверов
     * телеграм. Метод выбирает самую качественную фотографию из имеющихся (обычно телеграм дает три копии в
     * разном разрешении на выбор)
     *
     * @param message Сообщение, из которого нужно достать фотографию
     * @return объект {@link org.springframework.data.util.Pair}, где первый элемент представляет собой байты
     * фотографии, а второй {@link MediaType} указанной фотографии
     */
    public Pair<byte[], MediaType> getPhotoData(Message message) {

        PhotoSize largestPhoto = message.getPhoto().stream()
                .max(Comparator.comparing(PhotoSize::getFileSize))
                .orElseThrow();

        return telegramExecutor.getPhotoDataPair(largestPhoto).orElseThrow();
    }

    /**
     * В этом методе происходит коммуникация волонтера и юзера после инициации юзером такого диалога.
     * При уже инициированном общении сообщение одной стороны перехватывается и отправляется сюда вместе
     * с идентификатором (<b>chatId</b>) другой стороны. Здесь происходит разбор сообщения и построения потомка
     * {@code PartialBotApiMethod<Message>}, готового для отправки с помощью телеграм-бота.
     * Поддерживаются объекта классов: {@code SendSticker}, {@code SendPhoto} и {@code SendMessage}
     *
     * @param message        Сообщение, полученное ботом от одной стороны и подлежащее пересылке другой стороне
     * @param chatIdToAnswer объект {@code String chatId} - телеграм-идентификатор пользователя (ожидающей стороны),
     *                       то есть той, кто должен получить сообщение
     * @return объект {@code PartialBotApiMethod<Message>} по факту суженый до {@code SendSticker}, {@code SendPhoto}
     * или {@code SendMessage}
     */
    public PartialBotApiMethod<Message> chattingWithVolunteerProcess(Message message, String chatIdToAnswer) {

        InlineKeyboardMarkup keyboardMarkup = MessageUtils.getMarkupWithOneLinesButtons(
                Map.of(ENDING_VOLUNTEER_CHAT.getAnswer(), ENDING_VOLUNTEER_CHAT.getCallbackData()));

        return switch (message) {
            case Message inMess when inMess.hasSticker() -> {
                SendSticker sticker = new SendSticker(chatIdToAnswer, new InputFile(inMess.getSticker().getFileId()));
                sticker.setReplyMarkup(keyboardMarkup);
                yield sticker;
            }
            case Message inMess when inMess.hasPhoto() -> {
                Optional<PhotoSize> largestPhoto = message.getPhoto().stream()
                        .max(Comparator.comparing(PhotoSize::getFileSize));

                SendPhoto photo =
                        new SendPhoto(chatIdToAnswer, new InputFile(largestPhoto.orElseThrow().getFileId()));
                photo.setCaption(message.getCaption());
                photo.setReplyMarkup(keyboardMarkup);
                yield photo;
            }
            default -> {
                SendMessage sendMessage = new SendMessage(chatIdToAnswer, message.getText());
                sendMessage.setReplyMarkup(keyboardMarkup);
                yield sendMessage;
            }
        };
    }

    /**
     * Метод, инкапсулирующий логику процессинга отправки отчета пользователем. Метод получает объект {@link Message},
     * полученный от юзера и на основании флага {@code Long specialBehaviorId} формирует часть отчета, которую
     * отсылает на бекенд, где эта часть сохраняется в соответствующий отчет.
     *
     * @param message           Объект, содержащий часть отчета
     * @param specialBehaviorId Идентификатор части отчета, на его основе происходит определение того,
     *                          какую именно информацию содержит конкретно эта часть отчета
     * @return Объект {@code SendMessage} для отправки пользователю. Содержит текст сообщения, говорящий,
     * сколько еще (и какие именно) частей осталось прислать. Или простое поздравление, если все
     * часть отчета присланы
     * @see ReportPart
     * @see SendReportContinue
     * @see SendReportEnd
     * @see ReportService#acceptReportPart(ReportPart, byte[], String, MediaType)
     */
    public SendMessage sendReportProcess(Message message, String specialBehaviorId) {

        String chatId = String.valueOf(message.getChatId());
        if (message.hasText() && message.getText().length() > 500) {
            String tooLongMess = """
                    Ваше сообщение слишком длинное, пожалуйста сократите его
                    Максимальна длина - 500 символов, а ваше сообщение содержит %s символов
                    """.formatted(message.getText().length());
            return new SendMessage(chatId, tooLongMess);
        }

        ReportPart reportPart = of(specialBehaviorId);
        String suggestionToAdding = null;
        byte[] messageData;
        MediaType mediaType;

        /* Для облегчения взаимодействия с ботом во время разработки - автоматически создадим адоптера с присвоенным
           животным, если отчет захочет прислать недобавленный юзер. А так-то адоптеров должны добавлять волонтеры
           через API и присваивать им животных там же */
        if (!adopterService.existAdopterWithChatId(chatId)) {
            adopterService.addTrialAdopter(message);
            suggestionToAdding = "Упс! Кажется я еще не знаю Вас, как усыновителя животного! Пожалуйста, свяжитесь с волонтером, чтобы он добавил вас! Но пока что сделаем вид, что этой проблемы нет, я просто добавил вас автоматически. На продакшене эта функция будет удалена";
        }

       /* Передаем байты фотки в той же переменной (messageData), где передаем и текст сообщения. А content-type
          передаем в @Nullable-переменной (mediaType). Не самое элегантное решение, но зато самое лаконичное. Вместо
          того, чтоб заводить отдельный метод чисто под сохранение фотки + все проверки - выходит очень длинно */
        if (Objects.equals(reportPart, PHOTO)) {

            Pair<byte[], MediaType> photoData = getPhotoData(message);
            messageData = photoData.getFirst();
            mediaType = photoData.getSecond();
        } else {
            mediaType = null;
            messageData = message.getText().getBytes();
        }
        reportService.acceptReportPart(reportPart, messageData, chatId, mediaType);
        SendMessage applied = sendReportContinue.apply(chatId);

        if (suggestionToAdding != null) {
            applied.setText(suggestionToAdding + "\n" + "_".repeat(30) + "\n\n" + applied.getText());
        }
        return applied;
    }
}
