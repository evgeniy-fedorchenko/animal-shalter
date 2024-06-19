package com.evgeniyfedorchenko.animalshelter.admin.controllers;

import com.evgeniyfedorchenko.animalshelter.TestUtils;
import com.evgeniyfedorchenko.animalshelter.backend.dto.AnimalInputDto;
import com.evgeniyfedorchenko.animalshelter.backend.dto.AnimalOutputDto;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Animal;
import com.evgeniyfedorchenko.animalshelter.backend.mappers.AnimalMapper;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.AdopterRepository;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.AnimalRepository;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import static com.evgeniyfedorchenko.animalshelter.Constants.generateTestAdoptersInCountOf;
import static com.evgeniyfedorchenko.animalshelter.Constants.generateTestAnimalsInCountOf;
import static com.evgeniyfedorchenko.animalshelter.admin.controllers.SortOrder.ASC;
import static com.evgeniyfedorchenko.animalshelter.admin.controllers.SortOrder.DESC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@Slf4j
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AnimalControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private AdopterRepository adopterRepository;
    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private AnimalMapper animalMapper;
    @Autowired
    private TestUtils<Animal> testUtils;

    private List<Adopter> savedAdopters;
    private List<Animal> savedAnimals;
    private final Random random = new Random();
    private final Animal specialAnimal = generateTestAnimalsInCountOf(1).getFirst();

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

    private String baseAnimalUrl() {
        return "http://localhost:%d/animals".formatted(port);
    }

    @BeforeEach
    public void beforeEach() {
        savedAdopters = adopterRepository.saveAll(generateTestAdoptersInCountOf(5));
        savedAnimals = animalRepository.saveAll(generateTestAnimalsInCountOf(5));
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

    @Test
    void addAnimal_positiveTest() {
        AnimalInputDto inputDto = testUtils.toInputDto(specialAnimal);
        ResponseEntity<AnimalOutputDto> responseEntity = testRestTemplate.postForEntity(
                baseAnimalUrl(),
                inputDto,
                AnimalOutputDto.class);

//        Проверка ответа от сервера
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(animalMapper.toOutputDto(specialAnimal));

        assertThatCode(() -> log.trace(responseEntity.getBody().toString())).doesNotThrowAnyException();

//        Проверка, что Animal сохранился в БД
        Optional<Animal> animalFromDb = animalRepository.findById(responseEntity.getBody().getId());
        assertThat(animalFromDb).isPresent();
        assertThat(animalFromDb.get())
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(specialAnimal);
    }

    @Test
    void getAnimalById_positiveTest() {
        Animal randomSavedAnimal = savedAnimals.get(random.nextInt(1, savedAnimals.size()));

        ResponseEntity<AnimalOutputDto> responseEntity = testRestTemplate.getForEntity(
                baseAnimalUrl() + "/{id}",
                AnimalOutputDto.class,
                randomSavedAnimal.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        AnimalOutputDto randomSavedAnimalODto = animalMapper.toOutputDto(randomSavedAnimal);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(randomSavedAnimalODto);
        assertThat(responseEntity.getBody().hashCode()).isEqualTo(randomSavedAnimalODto.hashCode());
    }

    @Test
    void getAnimalById_negativeTest() {
        long nonExistAnimalId = testUtils.getIdNonExistsIn(new ArrayList<>(savedAnimals));

        ResponseEntity<AnimalOutputDto> responseEntity = testRestTemplate.getForEntity(
                baseAnimalUrl() + "/{id}",
                AnimalOutputDto.class,
                nonExistAnimalId);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.hasBody()).isFalse();
    }

    @MethodSource
    private static Stream<Arguments> provideParamsForSearchAnimals() {
        return Stream.of(
                Arguments.of("id", ASC, 1, 3),
                Arguments.of("name", DESC, 2, 2),
                Arguments.of("adult", DESC, 1, 4),
                Arguments.of("adopter", ASC, 1, 5)
        );
    }

    @ParameterizedTest
    @MethodSource("provideParamsForSearchAnimals")
    void searchAnimals_positiveTest(String sortParam, SortOrder sortOrder, int pageNumber, int pageSize) {

        ResponseEntity<List<AnimalOutputDto>> responseEntity = testRestTemplate.exchange(
                baseAnimalUrl() + "?sortParam={sortParam}&sortOrder={sortOrder}&pageNumber={pageNumber}&pageSize={pageSize}",
                HttpMethod.GET,
                RequestEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                },
                sortParam, sortOrder, pageNumber, pageSize
        );

//        Проверка ответа сервера
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<AnimalOutputDto> actual = responseEntity.getBody();
        assertThat(actual).isNotNull();

//        Сравниваем по id
        List<Long> actualIds = actual.stream().map(AnimalOutputDto::getId).toList();
        List<Long> allIds = savedAnimals.stream().map(Animal::getId).toList();
        assertThat(allIds).containsAll(actualIds);

//        Совершаем такой же запрос в БД, только напрямую и строго сравниваем результаты
        List<Animal> animals = testUtils.searchEntities(Animal.class, sortParam, sortOrder, pageSize, (pageNumber - 1) * pageSize);
        List<AnimalOutputDto> expected = animals.stream().map(animalMapper::toOutputDto).toList();
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void assignedAnimalToAnimal_positiveTest() throws InterruptedException {
//        Берем любой объект Adopter и любой Animal. Сбрасываем у них связь на null
        Animal randomSavedAnimal = savedAnimals.get(random.nextInt(1, savedAnimals.size()));
        Adopter randomSavedAdopter = savedAdopters.get(random.nextInt(1, savedAdopters.size()));
        randomSavedAnimal.setAdopter(null);
        randomSavedAdopter.setAnimal(null);
        animalRepository.save(randomSavedAnimal);
        adopterRepository.save(randomSavedAdopter);

//        Проверяем, что связь сбросилась
        assertThat(animalRepository.findById(randomSavedAnimal.getId()).orElseThrow().hasAdopter()).isFalse();
        assertThat(adopterRepository.findById(randomSavedAdopter.getId()).orElseThrow().hasAnimal()).isFalse();
        ResponseEntity<Void> responseEntity = testRestTemplate.exchange(
                baseAnimalUrl() + "?adopterId={adopterId}&animalId={animalId}",
                HttpMethod.PATCH,
                HttpEntity.EMPTY,
                Void.class,
                randomSavedAdopter.getId(),
                randomSavedAnimal.getId()
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.hasBody()).isFalse();

//         Хочется немного подождать, пока асинхронный поток сохранит обновленные сущности или нет в этом смысла?
        Thread.sleep(2000);
        Animal newAnimal = animalRepository.findById(randomSavedAnimal.getId()).orElseThrow();
        Adopter newAdopter = adopterRepository.findById(randomSavedAdopter.getId()).orElseThrow();

//        Проверяем что они связаны друг с другом
        assertThat(newAnimal.getAdopter()).isEqualTo(newAdopter);
        assertThat(newAdopter.getAnimal()).isEqualTo(newAnimal);

    }

    @Test
    void assignedAnimalToAdopterIfAnimalNotFound_negativeTest() {
        long nonExistAnimalId = testUtils.getIdNonExistsIn(new ArrayList<>(savedAnimals));

        Adopter randomSavedAdopter = savedAdopters.get(random.nextInt(1, savedAdopters.size()));
        Animal oldAnimalOfAdopter = randomSavedAdopter.getAnimal();

        ResponseEntity<Void> responseEntity = testRestTemplate.exchange(
                baseAnimalUrl() + "?adopterId={adopterId}&animalId={animalId}",
                HttpMethod.PATCH,
                HttpEntity.EMPTY,
                Void.class,
                randomSavedAdopter.getId(),
                nonExistAnimalId
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.hasBody()).isFalse();

//        Смотрим, что объект животное у Adopter не изменилось
        assertThat(adopterRepository.findById(randomSavedAdopter.getId()).orElseThrow().getAnimal())
                .isEqualTo(oldAnimalOfAdopter);
    }


    @Test
    void assignedAnimalToAnimalIfAdopterNotFound_negativeTest() {
        long nonExistAdopterId = testUtils.getIdNonExistsIn(new ArrayList<>(savedAdopters));
        Animal randomSavedAnimal = savedAnimals.get(random.nextInt(1, savedAnimals.size()));
        Adopter oldAdopterOfAnimal = randomSavedAnimal.getAdopter();


        ResponseEntity<Void> responseEntity = testRestTemplate.exchange(
                baseAnimalUrl() + "?adopterId={adopterId}&animalId={animalId}",
                HttpMethod.PATCH,
                HttpEntity.EMPTY,
                Void.class,
                nonExistAdopterId,
                randomSavedAnimal.getId()
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.hasBody()).isFalse();

//        Смотрим, что объект Adopter у животного не изменился
        assertThat(animalRepository.findById(randomSavedAnimal.getId()).orElseThrow().getAdopter())
                .isEqualTo(oldAdopterOfAnimal);
    }

    @Test
    void assignedAnimalToAdopterIfAnimalIsOccupied_negativeTest() {

//        Нам нужен существующий Adopter без животного и существующий Animal у которого есть усыновитель (другой)
        Adopter randonSavedAdopter = savedAdopters.get(random.nextInt(1, savedAdopters.size()));
        randonSavedAdopter.setAnimal(null);
        adopterRepository.save(randonSavedAdopter);

        Animal occupiedAnimal = savedAnimals.get(random.nextInt(1, savedAnimals.size()));
        Adopter anotherAdopter;
        do {
            anotherAdopter = savedAdopters.get(random.nextInt(1, savedAdopters.size()));
        } while (anotherAdopter.equals(randonSavedAdopter));
        anotherAdopter.setAnimal(occupiedAnimal);
        adopterRepository.save(anotherAdopter);
        occupiedAnimal.setAdopter(anotherAdopter);
        animalRepository.save(occupiedAnimal);

        RestTemplate patchedRestTemplate = testUtils.patchRestTemplate(testRestTemplate);
        ResponseEntity<Void> responseEntity = patchedRestTemplate.exchange(
                baseAnimalUrl() + "?adopterId={adopterId}&animalId={animalId}",
                HttpMethod.PATCH,
                HttpEntity.EMPTY,
                Void.class,
                randonSavedAdopter.getId(),
                occupiedAnimal.getId()
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.hasBody()).isFalse();

//        Смотрим, что объект Adopter у животного не изменился
        assertThat(animalRepository.findById(occupiedAnimal.getId()).orElseThrow().getAdopter())
                .isEqualTo(anotherAdopter);
    }

    @Test
    void assignedAnimalToAdopterIfAdopterIsOccupied_negativeTest() {

//        Нам нужен существующий Animal без усыновителя и существующий Adopter у которого есть животное (другое)
        Animal targetAnimal = savedAnimals.get(random.nextInt(1, savedAnimals.size()));
        targetAnimal.setAdopter(null);
        animalRepository.save(targetAnimal);

        Adopter occupiedAdopter = savedAdopters.get(random.nextInt(1, savedAdopters.size()));
        Animal anotherAnimal;
        do {
            anotherAnimal = savedAnimals.get(random.nextInt(1, savedAnimals.size()));
        } while (anotherAnimal.equals(targetAnimal));

        occupiedAdopter.setAnimal(anotherAnimal);
        adopterRepository.save(occupiedAdopter);
        anotherAnimal.setAdopter(occupiedAdopter);
        animalRepository.save(anotherAnimal);

        RestTemplate patchedRestTemplate = testUtils.patchRestTemplate(testRestTemplate);
        ResponseEntity<Void> responseEntity = patchedRestTemplate.exchange(
                baseAnimalUrl() + "?animalId={animalId}&adopterId={adopterId}",
                HttpMethod.PATCH,
                HttpEntity.EMPTY,
                Void.class,
                targetAnimal.getId(),
                occupiedAdopter.getId()
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.hasBody()).isFalse();

//        Смотрим, что объект Animal у усыновителя не изменился
        assertThat(adopterRepository.findById(occupiedAdopter.getId()).orElseThrow().getAnimal())
                .isEqualTo(anotherAnimal);
    }

    @Test
    void deleteAnimal_positiveTest() {

        Animal expected = savedAnimals.getFirst();
        int repoSizeBeforeDeleting = animalRepository.findAll().size();

        ResponseEntity<Void> responseEntity = testRestTemplate.exchange(
                baseAnimalUrl() + "/{id}",
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class,
                expected.getId()
        );
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNull();

        assertThat(animalRepository.findById(expected.getId())).isEmpty();
        assertThat(animalRepository.findAll().size())
                .isEqualTo(repoSizeBeforeDeleting - 1);
    }

    @Test
    void deleteAnimal_negativeTest() {

        long nonExistAnimalId = testUtils.getIdNonExistsIn(new ArrayList<>(savedAnimals));
        int repoSizeBeforeDeleting = animalRepository.findAll().size();

        ResponseEntity<Void> responseEntity = testRestTemplate.exchange(
                baseAnimalUrl() + "/{id}",
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class,
                nonExistAnimalId
        );
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isNull();

        assertThat(animalRepository.findAll().size())
                .isEqualTo(repoSizeBeforeDeleting);
    }
}
