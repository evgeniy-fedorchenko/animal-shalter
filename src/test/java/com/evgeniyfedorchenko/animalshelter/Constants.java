package com.evgeniyfedorchenko.animalshelter;

import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Animal;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Report;
import net.datafaker.Faker;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
// todo Создать второй набор констант - бОльшее их количество и разнообразие, скажем штук по 50 и добавить возможность тестирования на этом наборе
public class Constants {

    private static final Faker FAKER = new Faker();

//    todo Убрать отсюда объявление этих 100500 констант
    public static final Adopter ADOPTER_1 = new Adopter();
    public static final Adopter ADOPTER_2 = new Adopter();
    public static final Adopter ADOPTER_3 = new Adopter();
    public static final Adopter ADOPTER_4 = new Adopter();
    public static final Adopter ADOPTER_5 = new Adopter();
    public static Adopter UNSAVED_ADOPTER = new Adopter();

    public static final Animal ANIMAL_1 = new Animal();
    public static final Animal ANIMAL_2 = new Animal();
    public static final Animal ANIMAL_3 = new Animal();
    public static final Animal ANIMAL_4 = new Animal();
    public static final Animal ANIMAL_5 = new Animal();
    public static Animal UNSAVED_ANIMAL = new Animal();

    public static final Report REPORT_1 = new Report();
    public static final Report REPORT_2 = new Report();
    public static final Report REPORT_3 = new Report();
    public static final Report REPORT_4 = new Report();
    public static final Report REPORT_5 = new Report();
    public static Report UNSAVED_REPORT = new Report();

    public static List<Adopter> TEST_5_ADOPTERS = new ArrayList<>(List.of(
            ADOPTER_1, ADOPTER_2, ADOPTER_3, ADOPTER_4, ADOPTER_5));
    public static List<Animal> TEST_5_ANIMALS = new ArrayList<>(List.of(
            ANIMAL_1, ANIMAL_2, ANIMAL_3, ANIMAL_4, ANIMAL_5));
    public static List<Report> TEST_5_REPORTS = new ArrayList<>(List.of(
            REPORT_1, REPORT_2, REPORT_3, REPORT_4, REPORT_5));

    public static void testConstantsInitialize() {
        adopterConstantsInitialize();
        animalConstantsInitialize();
        reportConstantsInitialize();
    }

    private static void adopterConstantsInitialize() {
        Stream.of(ADOPTER_1, ADOPTER_2, ADOPTER_3, ADOPTER_4, ADOPTER_5, UNSAVED_ADOPTER)
                .forEach(adopter -> {
                    adopter.setChatId(FAKER.random().nextLong(5076421775L, 5076421775L));
                    adopter.setName(FAKER.letterify("adopterName?????"));
                    adopter.setPhoneNumber("79" + FAKER.number().digits(9));
                    adopter.setAssignedReportsQuantity(30);
                    adopter.setReports(new ArrayList<>());
                    adopter.setAnimal(null);
                });
    }

    private static void animalConstantsInitialize() {
        Stream.of(ANIMAL_1, ANIMAL_2, ANIMAL_3, ANIMAL_4, ANIMAL_5, UNSAVED_ANIMAL)
                .forEach(animal -> {
                    animal.setName(FAKER.letterify("animalName?????"));
                    animal.setAdult(FAKER.random().nextBoolean());
                    animal.setAdopter(null);
                });
    }

    private static void reportConstantsInitialize() {
        Stream.of(REPORT_1, REPORT_2, REPORT_3, REPORT_4, REPORT_5, UNSAVED_REPORT)
                .forEach(report -> {
                    report.setDiet(FAKER.letterify("reportDiet?????"));
                    report.setHealth(FAKER.letterify("reportHealth?????"));
                    report.setChangeBehavior(FAKER.letterify("reportCB?????"));
                    report.setPhotoData(null);
                    Instant instant = Instant.now();
                    report.setSendingAt(instant);
                    report.setVerified(FAKER.random().nextBoolean());
                    report.setAccepted(false);
                    report.setAdopter(null);
                });
    }
}
