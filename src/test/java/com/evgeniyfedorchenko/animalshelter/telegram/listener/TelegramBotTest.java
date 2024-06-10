package com.evgeniyfedorchenko.animalshelter.telegram.listener;

import com.evgeniyfedorchenko.animalshelter.TestUtils;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.MainHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelegramBotTest {

    @Mock
    private MainHandler mainHandlerMock;
    @Mock
    private TelegramExecutor telegramExecutorMock;
    @Mock
    private ValueOperations<Long, Long> valueOperationsMock;
    @Mock
    private RedisTemplate<Long, Long> redisTemplateMock;
    @InjectMocks
    private TelegramBot out;

    @Captor
    private ArgumentCaptor<Update> updateCaptor;
    @Captor
    private ArgumentCaptor<Message> messageCaptor;
    private final TestUtils<?> testUtils = new TestUtils<>();

    @BeforeEach
    public void setup() {
    }

    @Test
    void communicationWithVolunteerCall_positiveTest() {

        Update updateWithMessage = testUtils.getUpdateWithMessage("some string", false, false);

        when(redisTemplateMock.opsForValue()).thenReturn(valueOperationsMock);
        when(valueOperationsMock.get(anyLong())).thenReturn(1L);
        when(mainHandlerMock.communicationWithVolunteer(updateWithMessage.getMessage())).thenReturn(null);

        out.onUpdateReceived(updateWithMessage);

        verify(mainHandlerMock, only()).communicationWithVolunteer(messageCaptor.capture());
        assertThat(messageCaptor.getValue()).isEqualTo(updateWithMessage.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/start", "/help"})
    void handleCommandsCall_positiveTest(String command) {

        Update update = testUtils.getUpdateWithMessage(command, true, false);

        when(telegramExecutorMock.send(any(PartialBotApiMethod.class))).thenReturn(true);

        when(redisTemplateMock.opsForValue()).thenReturn(valueOperationsMock);
        when(valueOperationsMock.get(anyLong())).thenReturn(null);
        when(mainHandlerMock.handleCommands(update)).thenReturn(new SendMessage());

        out.onUpdateReceived(update);

        verify(mainHandlerMock, never()).communicationWithVolunteer(any(Message.class));
        verify(mainHandlerMock, only()).handleCommands(updateCaptor.capture());
        verify(telegramExecutorMock, only()).send(any(PartialBotApiMethod.class));

        assertThat(updateCaptor.getValue()).isEqualTo(update);
    }

    @Test
    void applyUnknownActionCall_negativeTest() {

        Update update = testUtils.getUpdateWithMessage("not command string", false, false);

        when(redisTemplateMock.opsForValue()).thenReturn(valueOperationsMock);
        when(valueOperationsMock.get(anyLong())).thenReturn(null);
        when(mainHandlerMock.applyUnknownUserAction(update)).thenReturn(null);

        out.onUpdateReceived(update);

        verify(mainHandlerMock, never()).communicationWithVolunteer(any(Message.class));
        verify(mainHandlerMock, never()).handleCommands(any(Update.class));
        verify(mainHandlerMock, only()).applyUnknownUserAction(updateCaptor.capture());

        assertThat(updateCaptor.getValue()).isEqualTo(update);
    }

    @Test
    void hasPhotoText() {

        Update updateWithMessage = testUtils.getUpdateWithMessage(null, false, true);

        when(telegramExecutorMock.send(any(PartialBotApiMethod.class))).thenReturn(true);

        when(redisTemplateMock.opsForValue()).thenReturn(valueOperationsMock);
        when(valueOperationsMock.get(anyLong())).thenReturn(null);
        CompletableFuture<SendMessage> expected = new CompletableFuture<>();
        when(mainHandlerMock.savePhoto(updateWithMessage.getMessage())).thenReturn(expected);
        expected.complete(new SendMessage());

        out.onUpdateReceived(updateWithMessage);

        verify(mainHandlerMock, never()).handleCommands(any(Update.class));
        verify(mainHandlerMock, never()).communicationWithVolunteer(any(Message.class));
        verify(mainHandlerMock, never()).handleCommands(any(Update.class));
        verify(mainHandlerMock, never()).applyUnknownUserAction(any(Update.class));

        verify(telegramExecutorMock, only()).send(any(PartialBotApiMethod.class));

        verify(mainHandlerMock, only()).savePhoto(messageCaptor.capture());
        assertThat(messageCaptor.getValue()).isEqualTo(updateWithMessage.getMessage());
    }

    @Test
    void callbackQueryCall_positiveTest() {

        Update updateWihCallback = testUtils.getUpdateWithCallback("some callback data");

        when(mainHandlerMock.handleCallbacks(updateWihCallback)).thenReturn(null);

        out.onUpdateReceived(updateWihCallback);

        verify(mainHandlerMock, never()).communicationWithVolunteer(any(Message.class));
        verify(mainHandlerMock, never()).handleCommands(any(Update.class));
        verify(mainHandlerMock, never()).applyUnknownUserAction(any(Update.class));
        verify(mainHandlerMock, never()).savePhoto(any(Message.class));

        verify(mainHandlerMock, only()).handleCallbacks(updateCaptor.capture());
        assertThat(updateCaptor.getValue()).isEqualTo(updateWihCallback);
    }

    @Test
    void distributeFailed_Test() {

        Update updateWithMessage = testUtils.getUpdateWithMessage(null, false, false);

        when(redisTemplateMock.opsForValue()).thenReturn(valueOperationsMock);
        when(valueOperationsMock.get(anyLong())).thenReturn(null);

        out.onUpdateReceived(updateWithMessage);

        verify(telegramExecutorMock, never()).send(any(PartialBotApiMethod.class));
    }
}