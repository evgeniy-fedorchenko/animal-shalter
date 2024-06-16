package com.evgeniyfedorchenko.animalshelter.admin.controllers;

import com.evgeniyfedorchenko.animalshelter.TestUtils;
import com.evgeniyfedorchenko.animalshelter.backend.dto.ReportOutputDto;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Report;
import com.evgeniyfedorchenko.animalshelter.backend.mappers.ReportMapper;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.AdopterRepository;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.ReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static com.evgeniyfedorchenko.animalshelter.Constants.generateTestAdoptersInCountOf;
import static com.evgeniyfedorchenko.animalshelter.Constants.generateTestReportsInCountOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@Slf4j
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private AdopterRepository adopterRepository;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private TestUtils<Report> testUtils;

    private List<Adopter> savedAdopters;
    private final List<Report> savedReports = new ArrayList<>();
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

    private String baseReportUrl() {
        return "http://localhost:%d/reports".formatted(port);
    }

    @BeforeEach
    public void beforeEach() {

        savedAdopters = adopterRepository.saveAll(generateTestAdoptersInCountOf(5));
        generateTestReportsInCountOf(5).forEach(report -> {
            Adopter randomAdopter = savedAdopters.get(random.nextInt(1, savedAdopters.size()));
            report.setAdopter(randomAdopter);
            Report savedReport = reportRepository.save(report);
            savedReports.add(savedReport);
        });
    }

    @AfterEach
    public void afterEach() {
        reportRepository.deleteAll();
        adopterRepository.deleteAll();

        savedReports.clear();
        savedAdopters.clear();
    }

    @AfterAll
    static void stopContainers() {
        POSTGRES_CONTAINER.stop();
    }

    @Test
    void getUnverifiedReportsTest() {

        int limit = random.nextInt(1, 5);

        /* Собираем объекты у которых поле verified = false, сортируем их, обрезаем по указанному лимиту.
           .limit(limit) не в самом начале, потому что мы можем не сразу найти объекты у которых verified = false */
        List<ReportOutputDto> oldestUnverifiedReports = reportRepository.findAll().stream()
                .filter(report -> !report.isVerified())
                .sorted(Comparator.comparing(Report::getSendingAt))
                .map(reportMapper::toDto)
                .limit(limit)
                .toList();

        ResponseEntity<List<ReportOutputDto>> responseEntity = testRestTemplate.exchange(
                baseReportUrl() + "?limit={limit}",
                HttpMethod.GET,
                RequestEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                },
                limit
        );
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();

        assertThatCode(() -> log.trace(responseEntity.getBody().toString())).doesNotThrowAnyException();

//        Проверка, что приложение вернуло те же объекты
        assertThat(responseEntity.getBody())
                .containsExactlyInAnyOrderElementsOf(oldestUnverifiedReports);

//        Проверка, что объекты изменили verified на true
        oldestUnverifiedReports.forEach(reportOutputDto -> {
            Report verifyingReport = reportRepository.findById(reportOutputDto.getId()).orElseThrow();
            assertThat(verifyingReport.isVerified()).isTrue();
        });
    }

    @Test
    void getReportById_positiveTest() {
        Report randomSavedReport = savedReports.get(random.nextInt(1, savedReports.size()));

        ResponseEntity<ReportOutputDto> responseEntity = testRestTemplate.getForEntity(
                baseReportUrl() + "/{id}",
                ReportOutputDto.class,
                randomSavedReport.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(reportMapper.toDto(randomSavedReport));
    }

    @Test
    void getReportById_negativeTest() {
        long nonExistReportId = testUtils.getIdNonExistsIn(new ArrayList<>(savedReports));

        ResponseEntity<ReportOutputDto> responseEntity = testRestTemplate.getForEntity(
                baseReportUrl() + "/{id}",
                ReportOutputDto.class,
                nonExistReportId);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.hasBody()).isFalse();
    }

    @ParameterizedTest
    @EnumSource(TestUtils.Format.class)
    void getPhoto_positiveTest(TestUtils.Format format) {

        byte[] imageBytes = testUtils.createImage(format);

        Report randomReport = savedReports.get(random.nextInt(1, savedReports.size()));
        randomReport.setPhotoData(imageBytes);
        randomReport.setMediaType(format.getMediaType());
        reportRepository.save(randomReport);

        ResponseEntity<byte[]> responseEntity = testRestTemplate.exchange(
                baseReportUrl() + "/{id}/photo",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                byte[].class,
                randomReport.getId()
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().length).isGreaterThan(0);

        assertThat(responseEntity.getHeaders().toSingleValueMap())
                .containsEntry("Content-Type", format.getMediaType())
                .containsEntry("Content-Length", String.valueOf(imageBytes.length));

        assertThat(responseEntity.getBody()).isEqualTo(imageBytes);
    }

    @Test
    void getPhoto_negativeTest() {
        long nonExistReportId = testUtils.getIdNonExistsIn(new ArrayList<>(savedReports));

        ResponseEntity<byte[]> responseEntity = testRestTemplate.exchange(
                baseReportUrl() + "/{id}/photo",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                byte[].class,
                nonExistReportId
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.hasBody()).isFalse();
    }
}
