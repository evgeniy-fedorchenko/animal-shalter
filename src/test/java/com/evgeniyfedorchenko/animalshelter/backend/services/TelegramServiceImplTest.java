package com.evgeniyfedorchenko.animalshelter.backend.services;

import com.evgeniyfedorchenko.animalshelter.TestUtils;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Report;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Volunteer;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.AdopterRepository;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.AnimalRepository;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.VolunteerRepository;
import com.evgeniyfedorchenko.animalshelter.telegram.listener.TelegramExecutor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import static com.evgeniyfedorchenko.animalshelter.Constants.generateTestVolunteersInCountOf;
import static com.evgeniyfedorchenko.animalshelter.backend.services.TelegramServiceImpl.AdaptationDecision.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class TelegramServiceImplTest {

    private static final Logger log = LoggerFactory.getLogger(TelegramServiceImplTest.class);
    @MockBean
    private TelegramExecutor telegramExecutorMock;
    @Autowired
    private AdopterRepository adopterRepository;
    @Autowired
    private AnimalRepository animalRepository;
    @Autowired
    private VolunteerRepository volunteerRepository;
    @Autowired
    private TelegramService telegramService;

    @Autowired
    private TestUtils<?> testUtils;

    @Captor
    private ArgumentCaptor<SendMessage> sendMessageCaptor;

    private final Random random = new Random();

    @Container
    private static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
            new PostgreSQLContainer<>("postgres:16.2");

    @DynamicPropertySource
    static void configurePostgres(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
        registry.add("spring.jpa.generate-ddl", () -> true);
    }

    @BeforeEach
    void BeforeEach() {
       /* В этом тестовом классе не настраиваем состояние бд,
          т.к. каждый тест требует свою конфигурацию данных внутри */
    }

    @AfterEach
    public void cleanRepositories() {
        adopterRepository.deleteAll();
        animalRepository.deleteAll();
        volunteerRepository.deleteAll();
    }

    @Test
    void sendMessage_positiveTest() {
        String randomChatId = String.valueOf(random.nextLong(Long.MAX_VALUE));
        String randomMessage = "random message";

        telegramService.sendMessage(randomChatId, randomMessage);

        verify(telegramExecutorMock, times(1)).send(sendMessageCaptor.capture());
        SendMessage actualSendMessage = sendMessageCaptor.getValue();

        assertThat(actualSendMessage.getChatId()).isEqualTo(randomChatId);
        assertThat(actualSendMessage.getText()).isEqualTo(randomMessage);
    }

    @Test
    void makeDecisionAboutAdaptationTest() {
//        В мапе хранится: ключ - объект Adopter, значение - кол-во его отчетов
        Map<Adopter, Integer> stats = testUtils.generateRepositoriesConditionForShedulingTest();

        telegramService.makeDecisionAboutAdaptation();

        int amountAdoptersWith30Reports = (int) stats.values().stream().filter(v -> v == 30).count();
        verify(telegramExecutorMock, times(amountAdoptersWith30Reports)).send(sendMessageCaptor.capture());

        List<SendMessage> actualSendMessages = sendMessageCaptor.getAllValues();
        assertThat(actualSendMessages).hasSize(amountAdoptersWith30Reports);

        stats.keySet().stream()
                .filter(key -> key.getReports().size() == 30)
                .forEach(adopterKey -> {

                    double acceptedCount = (double) adopterKey.getReports().stream().filter(Report::isAccepted).count();
                    int acceptedPercent = (int) (acceptedCount / 30 * 10);

                    if (acceptedPercent >= 9) {
                        testUtils.matchFinder.accept(adopterKey, SUCCESS, actualSendMessages);

                        assertThat(adopterRepository.findById(adopterKey.getId())).isEmpty();
                        assertThat(animalRepository.findById(adopterKey.getAnimal().getId())).isEmpty();

                    } else if (acceptedPercent >= 7) {
                        testUtils.matchFinder.accept(adopterKey, NEED_15_MORE, actualSendMessages);

                        Optional<Adopter> adopterOpt = adopterRepository.findById(adopterKey.getId());
                        assertThat(adopterOpt.orElseThrow().getAssignedReportsQuantity())
                                .isEqualTo(adopterKey.getAssignedReportsQuantity() + 15);

                    } else if (acceptedPercent >= 5) {
                        testUtils.matchFinder.accept(adopterKey, NEED_30_MORE, actualSendMessages);

                        Optional<Adopter> adopterOpt = adopterRepository.findById(adopterKey.getId());
                        assertThat(adopterOpt.orElseThrow().getAssignedReportsQuantity())
                                .isEqualTo(adopterKey.getAssignedReportsQuantity() + 30);

                    } else {
                        testUtils.matchFinder.accept(adopterKey, FAIL, actualSendMessages);
                    }
                });
//        Ничего лишнего не отправилось. Все ожидаемые сообщения удалялись, а значит список должен быть пуст
        assertThat(actualSendMessages).isEmpty();
    }

    @Test
    void getFreeVolunteerPositiveTest() throws InterruptedException {

        Volunteer targetVolunteer = generateTestVolunteersInCountOf(1, 2).getFirst();
        assertThat(targetVolunteer.isFree()).isTrue();

        Volunteer savedVolunteer = volunteerRepository.save(targetVolunteer);

        String calledVolunteerChatId = telegramService.getFreeVolunteer().join().orElseThrow();
        Thread.sleep(2000);

        assertThat(calledVolunteerChatId).isNotEmpty();

        Volunteer volunteerAfterCalling = volunteerRepository.findById(savedVolunteer.getId()).orElseThrow();
        assertThat(volunteerAfterCalling.isFree()).isFalse();
    }

    @Test
    void getFreeVolunteerNegativeTest() {
        List<Volunteer> volunteers = generateTestVolunteersInCountOf(10, 0.0);
        volunteerRepository.saveAll(volunteers);

        CompletableFuture<Optional<String>> futureFreeVolunteerChatId = telegramService.getFreeVolunteer();

        Optional<String> volunteerChatIdOpt = futureFreeVolunteerChatId.join();
        assertThat(futureFreeVolunteerChatId)
                .isNotNull()
                .isNotCancelled()
                .isCompleted();

        assertThat(volunteerChatIdOpt).isEmpty();
        assertThat(
                volunteerRepository.findAll().stream()
                        .map(Volunteer::isFree)
                        .anyMatch(status -> status.equals(Boolean.FALSE))
        ).isTrue();
    }

    @Test
    void returnVolunteerTest() {

        List<Volunteer> src = generateTestVolunteersInCountOf(10, 0.2);
        src.getFirst().setFree(false);
        List<Volunteer> savedVolunteers = volunteerRepository.saveAll(src);

        telegramService.returnVolunteer(src.getFirst().getChatId());

        Volunteer returnedVolunteer = volunteerRepository.findById(savedVolunteers.getFirst().getId()).orElseThrow();
        assertThatCode(() -> log.trace(returnedVolunteer.toString())).doesNotThrowAnyException();
        assertThat(returnedVolunteer.isFree()).isTrue();

    }
}