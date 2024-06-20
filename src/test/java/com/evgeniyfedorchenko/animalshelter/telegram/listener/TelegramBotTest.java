package com.evgeniyfedorchenko.animalshelter.telegram.listener;

import com.evgeniyfedorchenko.animalshelter.TestUtils;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import com.evgeniyfedorchenko.animalshelter.backend.services.TelegramServiceImpl;
import com.evgeniyfedorchenko.animalshelter.telegram.configuration.RedisTestConfiguration;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageData;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.Callback;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.SimpleApplicable;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.VolunteerChattingEnd;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.commands.Help;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.commands.Start;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.commands.Volunteer;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.actions.menu.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.MessageData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(RedisTestConfiguration.class)
@ActiveProfiles("test")
class TelegramBotTest {

    @MockBean
    private TelegramExecutor telegramExecutorMock;
    @MockBean
    private TelegramServiceImpl telegramServiceImplMock;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private TelegramBot telegramBot;

    @MockBean
    private ValueOperations<String, String> valueOperationsMock;
    @MockBean
    private RedisTemplate<String, String> redisTemplateMock;

    @Captor
    private ArgumentCaptor<PartialBotApiMethod<?>> methodCaptor;

    @Autowired
    private TestUtils<Adopter> testUtils;
    private static TestUtils<?> testUtilsStatic;

    @AfterEach
    public void cleanRedis() {

        Set<String> keys = new HashSet<>();
        ScanOptions scanOptions = ScanOptions.scanOptions().match("[-\\d]").build();

        try (Cursor<String> cursor = redisTemplateMock.scan(scanOptions)) {

            while (cursor.hasNext()) {
                keys.add(cursor.next());
            }

            redisTemplateMock.delete(keys);
        }
    }

    @BeforeAll
    public void initStaticUtils() {
        testUtilsStatic = applicationContext.getBean(TestUtils.class);
    }

    private static Stream<Arguments> provideArgumentsForCommandsPositiveTest() {
        return Stream.of(
                Arguments.of(Start.class, START, 3),
                Arguments.of(Help.class, HELP, 2)
        );
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForCommandsPositiveTest")
    void commandsPositiveTest(Class<? extends SimpleApplicable> commandClass,
                              MessageData expectedMessageData,
                              int keyboardButtonsCount) {

//        Получаем бин из контекста, потому что нужно именно название бина, а не сам класс или имя класса
        String[] beanNamesForType = applicationContext.getBeanNamesForType(commandClass);
        assertThat(beanNamesForType).hasSize(1);

        Update updateWithMessage =
                testUtils.getUpdateWithMessage(beanNamesForType[0], true, false);
        String expectedChatId = String.valueOf(updateWithMessage.getMessage().getChatId());

        when(redisTemplateMock.opsForValue()).thenReturn(valueOperationsMock);
        when(valueOperationsMock.get(expectedChatId)).thenReturn(null);
        when(telegramExecutorMock.send(any(PartialBotApiMethod.class))).thenReturn(true);

        telegramBot.onUpdateReceived(updateWithMessage);

        verify(redisTemplateMock, only()).opsForValue();
        verify(valueOperationsMock, only()).get(expectedChatId);

        verify(telegramExecutorMock, only()).send(methodCaptor.capture());
        PartialBotApiMethod<?> actual = methodCaptor.getValue();

//        Проверяем тип ответа
        assertThat(actual).isNotNull();
        assertThat(actual instanceof SendMessage).isTrue();
        SendMessage actualSendMessage = (SendMessage) actual;

//        Проверяем chatId и text
        assertThat(actualSendMessage.getChatId()).isEqualTo(expectedChatId);
        assertThat(actualSendMessage.getText()).isEqualTo(expectedMessageData.getAnswer());

//        Проверяем клавиатуру
        InlineKeyboardMarkup actualKeyboard = (InlineKeyboardMarkup) actualSendMessage.getReplyMarkup();
        assertThat(actualKeyboard).isNotNull();

        assertThat(
                actualKeyboard.getKeyboard().stream()
                        .flatMapToInt(row -> IntStream.of(row.size()))
                        .sum()
        ).isEqualTo(keyboardButtonsCount);
    }

    private static Stream<Arguments> provideArgumentsForCallbackPositiveTest() {
        return Stream.of(
//                Мб и стоит получить их из контекста, но просто пришлось отсюда выкинуть
//                некоторые классы, которые немного по-другому себя ведут
                Arguments.of(AdoptionDocs.class, ADOPTION_DOCS, 1),
                Arguments.of(BackToStart.class, START, 3),
                Arguments.of(DatingRules.class, DATING_RULES, 1),
                Arguments.of(GetPatternReport.class, GET_PATTERN_REPORT, 2),
                Arguments.of(HouseForAdultAnimal.class, HOUSE_FOR_ADULT_ANIMAL, 1),
                Arguments.of(HouseForSmallAnimal.class, HOUSE_FOR_SMALL_ANIMAL, 1),
                Arguments.of(MainAboutShelter.class, MAIN_ABOUT_SHELTER, 5),
                Arguments.of(MainHowTakeAnimal.class, MAIN_HOW_TAKE_ANIMAL, 6),
                Arguments.of(MainReportMenu.class, MAIN_REPORT_MENU, 3),
                Arguments.of(SafetyAtShelter.class, SAFETY_AT_SHELTER, 1),
                Arguments.of(SecurityContacts.class, SECURITY_CONTACTS, 1),
                Arguments.of(TransportPet.class, TRANSPORT_PET, 1)
        );
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForCallbackPositiveTest")
    void callbackPositiveTest(Class<? extends Callback> callbackData,
                              MessageData expectedMessageData,
                              int keyboardButtonsCount) {
        String[] beanNamesForType = applicationContext.getBeanNamesForType(callbackData);
        assertThat(beanNamesForType).hasSize(1);

        Update updateWithCallback = testUtils.getUpdateWithCallback(beanNamesForType[0]);
        MaybeInaccessibleMessage messageFromCallback = updateWithCallback.getCallbackQuery().getMessage();

        when(telegramExecutorMock.send(any(PartialBotApiMethod.class))).thenReturn(true);
        telegramBot.onUpdateReceived(updateWithCallback);

        verify(telegramExecutorMock, only()).send(methodCaptor.capture());
        PartialBotApiMethod<?> actual = methodCaptor.getValue();

        assertThat(actual).isNotNull();
        assertThat(actual instanceof EditMessageText).isTrue();
        EditMessageText actualEditMessageText = (EditMessageText) actual;

        assertThat(actualEditMessageText.getChatId())
                .isEqualTo(String.valueOf(messageFromCallback.getChatId()));
        assertThat(actualEditMessageText.getMessageId())
                .isEqualTo(messageFromCallback.getMessageId());
        assertThat(actualEditMessageText.getText()).isEqualTo(expectedMessageData.getAnswer());

        if (keyboardButtonsCount != 0) {
            InlineKeyboardMarkup actualKeyboard = actualEditMessageText.getReplyMarkup();
            assertThat(actualKeyboard).isNotNull();
            assertThat(
                    actualKeyboard.getKeyboard().stream()
                            .flatMapToInt(row -> IntStream.of(row.size()))
            ).hasSize(keyboardButtonsCount);
        }
    }

    @Test
    void callingVolunteerFromButtonPositiveTest() throws InterruptedException {

        String[] beanNamesForType = applicationContext.getBeanNamesForType(Volunteer.class);
        assertThat(beanNamesForType).hasSize(1);
        Update updateWithMessage =
                testUtils.getUpdateWithMessage(beanNamesForType[0], true, false);
        String expectedChatId = String.valueOf(updateWithMessage.getMessage().getChatId());
        String freeVolunteerChatId = "999";

        when(redisTemplateMock.opsForValue()).thenReturn(valueOperationsMock)
                .thenReturn(valueOperationsMock)
                .thenReturn(valueOperationsMock);
        when(valueOperationsMock.get(expectedChatId)).thenReturn(null);
        when(telegramExecutorMock.send(any(PartialBotApiMethod.class))).thenReturn(true);
        when(telegramServiceImplMock.getFreeVolunteer())
                .thenReturn(Optional.of(freeVolunteerChatId));

        doNothing().when(valueOperationsMock).set(expectedChatId, freeVolunteerChatId);
        doNothing().when(valueOperationsMock).set(freeVolunteerChatId, expectedChatId);

        telegramBot.onUpdateReceived(updateWithMessage);
        Thread.sleep(1000);

        verify(redisTemplateMock, times(3)).opsForValue();
//        Вот кто бы мог подумать что 'Mockito.times(1)' и 'Mockito.only()' это не одно и то же :/
        verify(valueOperationsMock, times(1)).get(expectedChatId);
        verify(valueOperationsMock, times(1))
                .set(freeVolunteerChatId, expectedChatId);
        verify(valueOperationsMock, times(1))
                .set(expectedChatId, freeVolunteerChatId);

//        Проверка, что отправляется два сообщения - одно волонтеру и одно юзеру
        verify(telegramExecutorMock, times(2)).send(methodCaptor.capture());
        List<PartialBotApiMethod<?>> allActualValues = methodCaptor.getAllValues();

        String messToVolunteer = "Волонтер! Требуется твоя помощь, отправь приветственное сообщение в этот чат";
        assertThat(allActualValues.stream().allMatch(anyValue -> anyValue instanceof SendMessage)).isTrue();
        assertThat(
                allActualValues.stream().anyMatch(actualValue1 ->
                        ((SendMessage) actualValue1).getChatId().equals(freeVolunteerChatId)
                                && ((SendMessage) actualValue1).getText().equals(messToVolunteer)
                )).isTrue();

        assertThat(
                allActualValues.stream().anyMatch(actualValue2 ->
                        ((SendMessage) actualValue2).getChatId().equals(expectedChatId)
                                && ((SendMessage) actualValue2).getText().equals(VOLUNTEER.getAnswer())
                )).isTrue();
    }

    @Test
    void callingVolunteerFromCallbackPositiveTest() {
        String[] beanNamesForType = applicationContext.getBeanNamesForType(VolunteerCallback.class);
        assertThat(beanNamesForType).hasSize(1);

        Update updateWithCallback = testUtils.getUpdateWithCallback(beanNamesForType[0]);
        MaybeInaccessibleMessage messageFromCallback = updateWithCallback.getCallbackQuery().getMessage();

        when(telegramExecutorMock.send(any(PartialBotApiMethod.class))).thenReturn(true);
        telegramBot.onUpdateReceived(updateWithCallback);

        verify(telegramExecutorMock, only()).send(methodCaptor.capture());
        PartialBotApiMethod<?> actual = methodCaptor.getValue();

        assertThat(actual).isNotNull();
        assertThat(actual instanceof EditMessageText).isTrue();
        EditMessageText actualEditMessageText = (EditMessageText) actual;

        assertThat(actualEditMessageText.getChatId()).isEqualTo(String.valueOf(messageFromCallback.getChatId()));
        assertThat(actualEditMessageText.getMessageId()).isEqualTo(messageFromCallback.getMessageId());
        assertThat(actualEditMessageText.getText()).isEqualTo(VOLUNTEER.getAnswer());

//        Вызов 'Volunteer.callVolunteer()' проверяется в другом тесте
    }

    @Test
    void endingVolunteerChatPositiveTest() {

        String[] beanNamesForType = applicationContext.getBeanNamesForType(VolunteerChattingEnd.class);
        assertThat(beanNamesForType).hasSize(1);

        Update updateWithCallback = testUtils.getUpdateWithCallback(beanNamesForType[0]);
        String chatIdFromCallback = String.valueOf(updateWithCallback.getCallbackQuery().getMessage().getChatId());
        String volunteerChatId = "999";

        when(redisTemplateMock.opsForValue()).thenReturn(valueOperationsMock);
        when(valueOperationsMock.get(chatIdFromCallback)).thenReturn(volunteerChatId);

        when(redisTemplateMock.delete(volunteerChatId)).thenReturn(true);
        when(redisTemplateMock.delete(chatIdFromCallback)).thenReturn(true);
        when(telegramExecutorMock.send(any(PartialBotApiMethod.class))).thenReturn(true);

        telegramBot.onUpdateReceived(updateWithCallback);

//        Проверка, что по завершению общения отправилось два сообщения - юзеру и волонтеру
        verify(telegramExecutorMock, times(2)).send(methodCaptor.capture());
        List<PartialBotApiMethod<?>> allActualValues = methodCaptor.getAllValues();

        assertThat(allActualValues.stream().allMatch(anyValue -> anyValue instanceof SendMessage)).isTrue();
        assertThat(
                allActualValues.stream().anyMatch(actualValue1 ->
                        ((SendMessage) actualValue1).getChatId().equals(volunteerChatId)
                                && ((SendMessage) actualValue1).getText().equals("Пользователь завершил диалог")
                )).isTrue();

        assertThat(
                allActualValues.stream().anyMatch(actualValue2 ->
                        ((SendMessage) actualValue2).getChatId().equals(chatIdFromCallback)
                                && ((SendMessage) actualValue2).getText().equals(ENDING_VOLUNTEER_CHAT.getAnswer())
                )).isTrue();
    }

    private static Stream<Arguments> provideParamsForChattingWithVolunteerProcessPositiveTest() {

        return Stream.of(
                Arguments.of(testUtilsStatic.getUpdateWithSticker()),   // Стикер
                Arguments.of(testUtilsStatic.getUpdateWithMessage(null, false, true)),                 // Только фото
                Arguments.of(testUtilsStatic.getUpdateWithMessage("message and photo", false, false)), // Только сообщение
                Arguments.of(testUtilsStatic.getUpdateWithMessage("message and photo", false, true))   // Сообщение и фото
        );
    }

    @ParameterizedTest
    @MethodSource("provideParamsForChattingWithVolunteerProcessPositiveTest")
    void chattingWithVolunteerProcessPositiveTest(@NotNull Update update) {

        String[] beanNamesForType = applicationContext.getBeanNamesForType(Volunteer.class);
        assertThat(beanNamesForType).hasSize(1);

        Message messageFromUpdate = update.getMessage();
        String otherChattingSideChatId = "999";

        when(redisTemplateMock.opsForValue()).thenReturn(valueOperationsMock)
                .thenReturn(valueOperationsMock);
        when(valueOperationsMock.get(String.valueOf(messageFromUpdate.getChatId()))).thenReturn(otherChattingSideChatId)
                .thenReturn(otherChattingSideChatId);
        when(telegramExecutorMock.send(any(PartialBotApiMethod.class))).thenReturn(true);

        telegramBot.onUpdateReceived(update);

        verify(telegramExecutorMock, only()).send(methodCaptor.capture());
        PartialBotApiMethod<?> actualValue = methodCaptor.getValue();
        assertThat(actualValue).isNotNull();

//        Проверка, что любой поддерживаемый тип контента (стрикер, текст, фото, фото + текст) отправляется
        InlineKeyboardMarkup actualKeyboard;
        switch (actualValue) {
            case SendSticker actual -> {

                assertThat(messageFromUpdate.hasSticker()).isTrue();
                assertThat(actual.getChatId()).isEqualTo(otherChattingSideChatId);
                actualKeyboard = (InlineKeyboardMarkup) actual.getReplyMarkup();
            }
            case SendPhoto actual -> {

                assertThat(messageFromUpdate.hasPhoto()).isTrue();
                assertThat(actual.getCaption()).isEqualTo(
                        messageFromUpdate.hasText() ? messageFromUpdate.getText() : null
                );
                assertThat(actual.getChatId()).isEqualTo(otherChattingSideChatId);
                actualKeyboard = (InlineKeyboardMarkup) actual.getReplyMarkup();
            }
            case SendMessage actual -> {

                assertThat(messageFromUpdate.hasText()).isTrue();
                assertThat(actual.getChatId()).isEqualTo(otherChattingSideChatId);
                assertThat(actual.getText()).isEqualTo(messageFromUpdate.getText());

                actualKeyboard = (InlineKeyboardMarkup) actual.getReplyMarkup();
            }
            default -> throw new AssertionError(
                    "Unexpected child type of PartialBotApiMethod<?>. Type: " + actualValue.getClass()
            );
        }

//        Проверка клавиатуры (одна кнопка - "Завершить диалог")
        assertThat(actualKeyboard).isNotNull();
        assertThat(
                actualKeyboard.getKeyboard().stream()
                        .flatMapToInt(row -> IntStream.of(row.size()))
        ).hasSize(1);
        assertThat(actualKeyboard.getKeyboard().getFirst().getFirst().getText()).isEqualTo("Завершить диалог");
    }

    @Test
    void unknownCommandPositiveTest() {
        String targetUnknownCommand = "/command";
        Update updateWithMessage = testUtils.getUpdateWithMessage(targetUnknownCommand, true, false);
        String chatIdFromUpdate = String.valueOf(updateWithMessage.getMessage().getChatId());

        when(redisTemplateMock.opsForValue()).thenReturn(valueOperationsMock);
        when(valueOperationsMock.get(chatIdFromUpdate)).thenReturn(null);
        when(telegramExecutorMock.send(any(PartialBotApiMethod.class))).thenReturn(true);

        telegramBot.onUpdateReceived(updateWithMessage);

        verify(telegramExecutorMock, only()).send(methodCaptor.capture());
        PartialBotApiMethod<?> actualValue = methodCaptor.getValue();

        assertThat(actualValue).isNotNull();
        SendMessage actualMessage = (SendMessage) actualValue;

        assertThat(actualMessage.getChatId()).isEqualTo(chatIdFromUpdate);
        assertThat(actualMessage.getText()).isEqualTo("Unknown command: " + targetUnknownCommand);
    }

    @Test
    void applyUnknownUserActionPositiveTest() {
        String targetUnknownText = "unknown text";
        Update updateWithMessage = testUtils.getUpdateWithMessage(targetUnknownText, false, false);
        String chatIdFromUpdate = String.valueOf(updateWithMessage.getMessage().getChatId());

        when(redisTemplateMock.opsForValue()).thenReturn(valueOperationsMock);
        when(valueOperationsMock.get(chatIdFromUpdate)).thenReturn(null);
        when(telegramExecutorMock.send(any(PartialBotApiMethod.class))).thenReturn(true);

        telegramBot.onUpdateReceived(updateWithMessage);

        verify(telegramExecutorMock, only()).send(methodCaptor.capture());
        PartialBotApiMethod<?> actualValue = methodCaptor.getValue();

        assertThat(actualValue).isNotNull();
        SendMessage actualMessage = (SendMessage) actualValue;

        assertThat(actualMessage.getChatId()).isEqualTo(chatIdFromUpdate);
        assertThat(actualMessage.getText())
                .isEqualTo(
                        """
                                Я прошу прощения, но кажется, я вас не понимаю \uD83E\uDD72
                                Как насчет того, чтобы просто начать сначала?)
                                👉 /start 👈"""
                );
    }


}
