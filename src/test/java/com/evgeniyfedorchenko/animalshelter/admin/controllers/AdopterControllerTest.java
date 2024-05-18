package com.evgeniyfedorchenko.animalshelter.admin.controllers;

import com.evgeniyfedorchenko.animalshelter.Constants;
import com.evgeniyfedorchenko.animalshelter.TestUtils;
import com.evgeniyfedorchenko.animalshelter.backend.dto.AdopterInputDto;
import com.evgeniyfedorchenko.animalshelter.backend.dto.AdopterOutputDto;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Animal;
import com.evgeniyfedorchenko.animalshelter.backend.mappers.AdopterMapper;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.AdopterRepository;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.AnimalRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import static com.evgeniyfedorchenko.animalshelter.Constants.*;
import static com.evgeniyfedorchenko.animalshelter.admin.controllers.SortOrder.ASC;
import static com.evgeniyfedorchenko.animalshelter.admin.controllers.SortOrder.DESC;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AdopterControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private AdopterRepository adopterRepository;
    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private AdopterMapper adopterMapper;
    @Autowired
    private TestUtils testUtils;

    private List<Adopter> savedAdopters;
    private List<Animal> savedAnimals;
    private final Random random = new Random();
    private final Faker faker = new Faker();
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

    private String baseAdopterUrl() {
        return "http://localhost:%d/adopters".formatted(port);
    }

    @BeforeEach
    public void beforeEach() {
        Constants.testConstantsInitialize();

        savedAdopters = adopterRepository.saveAll(TEST_5_ADOPTERS);
        savedAnimals = animalRepository.saveAll(TEST_5_ANIMALS);
    }

    @AfterEach
    public void afterEach() {
        adopterRepository.deleteAll();
        animalRepository.deleteAll();

        savedAdopters.clear();
        savedAnimals.clear();
    }

    @AfterAll
    static void stopContainers() {
        POSTGRES_CONTAINER.stop();
    }

//    Это нужно чтоб полностью покрыть метод, который стандартизирует номер телефона
    private static Stream<Arguments> provideParamsForAddAdopter() {
        return Stream.of(
                Arguments.of("79000000000"),
                Arguments.of("89000000000"),
                Arguments.of("+79000000000")
        );
    }
    @ParameterizedTest
    @MethodSource("provideParamsForAddAdopter")
    void addAdopter_positiveTest(String phoneNumber) {

        UNSAVED_ADOPTER.setPhoneNumber(phoneNumber);
        AdopterInputDto inputDto = testUtils.toInputDto(UNSAVED_ADOPTER);
        ResponseEntity<AdopterOutputDto> responseEntity = testRestTemplate.postForEntity(
                baseAdopterUrl(),
                inputDto,
                AdopterOutputDto.class);

//        Проверка ответа от сервера
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id", "phoneNumber")
                .isEqualTo(adopterMapper.toOutputDto(UNSAVED_ADOPTER));

//        Проверка, что Adopter сохранился в БД
        Optional<Adopter> adopterFromDb = adopterRepository.findById(responseEntity.getBody().getId());
        assertThat(adopterFromDb).isPresent();
        assertThat(adopterFromDb.get())
                .usingRecursiveComparison()
                .ignoringFields("id", "phoneNumber")
                .isEqualTo(UNSAVED_ADOPTER);

        String phoneFromDb = adopterFromDb.get().getPhoneNumber();
        String phoneFromUnsavedAdopter = UNSAVED_ADOPTER.getPhoneNumber();

        assertThat(phoneFromDb).startsWith("79");
        assertThat(phoneFromDb.substring(phoneFromDb.length() - 9))
                .isEqualTo(phoneFromUnsavedAdopter.substring(phoneFromUnsavedAdopter.length() - 9));
    }

    @Test
    void addAdopterWithAnimal_positiveTest() {

        Adopter targetAdopter = UNSAVED_ADOPTER;
        Animal targetAnimal = savedAnimals.getFirst();
        targetAdopter.setAnimal(targetAnimal);

        AdopterInputDto inputDto = testUtils.toInputDto(targetAdopter);
        ResponseEntity<AdopterOutputDto> responseEntity = testRestTemplate.postForEntity(
                baseAdopterUrl(),
                inputDto,
                AdopterOutputDto.class);

//        Проверка ответа от сервера
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(adopterMapper.toOutputDto(targetAdopter));

//        Проверка, что Adopter сохранился в БД
        Optional<Adopter> adopterFromDb = adopterRepository.findById(responseEntity.getBody().getId());
        assertThat(adopterFromDb).isPresent();
        assertThat(adopterFromDb.get())
                .usingRecursiveComparison()
                .ignoringFields("id", "animal.adopter")
                .isEqualTo(targetAdopter);

//        Проверка, что к Animal прицепился нужный Adopter
        assertThat(adopterFromDb.get().hasAnimal()).isTrue();
        Optional<Animal> animalOpt = animalRepository.findById(targetAnimal.getId());
        assertThat(animalOpt).isPresent();
        assertThat(adopterFromDb.get().getAnimal()).isEqualTo(animalOpt.get());
    }

    @Test
    void getAdopterById_positiveTest() {
        Adopter randomSavedAdopter = savedAdopters.get(faker.random().nextInt(savedAdopters.size()));

        ResponseEntity<AdopterOutputDto> responseEntity = testRestTemplate.getForEntity(
                baseAdopterUrl() + "/{id}",
                AdopterOutputDto.class,
                randomSavedAdopter.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(adopterMapper.toOutputDto(randomSavedAdopter));
    }

    @Test
    void getAdopterById_negativeTest() {
        long randomIdx;
        List<Long> existingIds = savedAdopters.stream().map(Adopter::getId).toList();
        do {
            randomIdx = faker.random().nextInt(Integer.MAX_VALUE);
        } while (existingIds.contains(randomIdx));

        ResponseEntity<AdopterOutputDto> responseEntity = testRestTemplate.getForEntity(
                baseAdopterUrl() + "/{id}",
                AdopterOutputDto.class,
                randomIdx);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.hasBody()).isFalse();
    }


    @MethodSource
    private static Stream<Arguments> provideParamsForSearchAdopters() {
        return Stream.of(
                Arguments.of("id", ASC, 1, 3),
                Arguments.of("chatId", DESC, 2, 2),
                Arguments.of("name", ASC, 2, 3),
                Arguments.of("phoneNumber", DESC, 1, 3),
                Arguments.of("assignedReportsQuantity", ASC, 1, 4),
                Arguments.of("reports", DESC, 2, 4),
                Arguments.of("animal", ASC, 1, 5)
        );
    }

    @ParameterizedTest
    @MethodSource("provideParamsForSearchAdopters")
    void searchAdopters_positiveTest(String sortParam, SortOrder sortOrder, int pageNumber, int pageSize) {

//        String randomFieldName = faker.options().option(Adopter.class.getDeclaredFields()).getName();
//        SortOrder sortOrder = faker.random().nextBoolean() ? ASC : DESC;
//        int pageNumber = faker.random().nextInt(1, 3);
//        int pageSize = faker.random().nextInt(1, 5);

        ResponseEntity<List<AdopterOutputDto>> responseEntity = testRestTemplate.exchange(
                baseAdopterUrl() + "?sortParam={sortParam}&sortOrder={sortOrder}&pageNumber={pageNumber}&pageSize={pageSize}",
                HttpMethod.GET,
                RequestEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                },
                sortParam, sortOrder, pageNumber, pageSize
        );

//        Проверка ответа сервера
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<AdopterOutputDto> actual = responseEntity.getBody();
        assertThat(actual).isNotNull();

//        Сравниваем по id и проверяем сортировку  
        List<Long> actualIds = actual.stream().map(AdopterOutputDto::getId).toList();
        List<Long> allIds = savedAdopters.stream().map(Adopter::getId).toList();
        assertThat(allIds).containsAll(actualIds);

//        Совершаем такой же запрос в БД, только напрямую и строго сравниваем резы (но без учета порядка)
        List<Adopter> adopters = testUtils.searchAdopters("Adopter", sortParam, sortOrder, pageSize, (pageNumber - 1) * pageSize);
        List<AdopterOutputDto> expected = adopters.stream().map(adopterMapper::toOutputDto).toList();
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void deleteAdopter_positiveTest() {

        Adopter expected = savedAdopters.getFirst();
        int repoSizeBeforeDeleting = adopterRepository.findAll().size();

        ResponseEntity<Void> responseEntity = testRestTemplate.exchange(
                baseAdopterUrl() + "/{id}",
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class,
                expected.getId()
        );
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNull();

        assertThat(adopterRepository.findById(expected.getId())).isEmpty();
        assertThat(adopterRepository.findAll().size())
                .isEqualTo(repoSizeBeforeDeleting - 1);
    }

    @Test
    void deleteAdopter_negativeTest() {

        List<Long> ids = adopterRepository.findAll().stream().map(Adopter::getId).toList();
        long nonexistentId;
        do {
            nonexistentId = random.nextLong(0, Long.MAX_VALUE);
        } while (ids.contains(nonexistentId));
        int repoSizeBeforeDeleting = adopterRepository.findAll().size();

        ResponseEntity<Void> responseEntity = testRestTemplate.exchange(
                baseAdopterUrl() + "/{id}",
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class,
                nonexistentId
        );
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isNull();

        assertThat(adopterRepository.findAll().size())
                .isEqualTo(repoSizeBeforeDeleting);

    }

}