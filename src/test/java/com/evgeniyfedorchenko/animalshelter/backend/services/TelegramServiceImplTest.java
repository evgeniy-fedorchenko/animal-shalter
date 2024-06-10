package com.evgeniyfedorchenko.animalshelter.backend.services;

import com.evgeniyfedorchenko.animalshelter.telegram.listener.TelegramExecutor;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelegramServiceImplTest {

    @Mock
    private TelegramExecutor telegramExecutorMock;
    @InjectMocks
    private TelegramServiceImpl out;

    private final Faker faker = new Faker();

    private long randomChatId;
    private String randomString;

    @BeforeEach
    void BeforeEach() {
        randomChatId = faker.random().nextLong(Long.MAX_VALUE);
        randomString = faker.random().hex();
    }

    @Test
    void sendMessage_positiveTest() {

        when(telegramExecutorMock.send(any(SendMessage.class))).thenReturn(true);
        out.sendMessage(randomChatId, randomString);
        verify(telegramExecutorMock, times(1)).send(any(SendMessage.class));
    }

    @Test
    void sendMessage_negativeTest() {
        long randomChatId = faker.random().nextLong(Long.MAX_VALUE);
        String randomString = faker.random().hex();

        when(telegramExecutorMock.send(any(SendMessage.class))).thenReturn(false);
        out.sendMessage(randomChatId, randomString);
        verify(telegramExecutorMock, times(1)).send(any(SendMessage.class));
    }

    @Test
    void savePhoto_positiveTest() {

    }
}