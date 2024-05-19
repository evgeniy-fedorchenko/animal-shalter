package com.evgeniyfedorchenko.animalshelter;

import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Animal;
import net.datafaker.Faker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
// todo Создать второй набор констант - бОльшее их количество и разнообразие, скажем штук по 50 и добавить возможность тестирования на этом наборе
public class Constants {

    private static final Faker FAKER = new Faker();

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

    public static List<Adopter> TEST_5_ADOPTERS = new ArrayList<>(List.of(
            ADOPTER_1, ADOPTER_2, ADOPTER_3, ADOPTER_4, ADOPTER_5));
    public static List<Animal> TEST_5_ANIMALS = new ArrayList<>(List.of(
            ANIMAL_1, ANIMAL_2, ANIMAL_3, ANIMAL_4, ANIMAL_5));

    public static void testConstantsInitialize() {
        adopterConstantsInitialize();
        animalConstantsInitialize();
    }

    private static void adopterConstantsInitialize() {
        Stream.of(ADOPTER_1, ADOPTER_2, ADOPTER_3, ADOPTER_4, ADOPTER_5, UNSAVED_ADOPTER)
                .forEach(adopter -> {
                    adopter.setChatId(FAKER.random().nextLong(1000000000L, 9999999999L));
                    adopter.setName(FAKER.letterify("adopter?????"));
                    adopter.setPhoneNumber("79" + FAKER.number().digits(9));
                    adopter.setAssignedReportsQuantity(30);
                    adopter.setReports(Collections.emptyList());
                    adopter.setAnimal(null);
                });
    }

    private static void animalConstantsInitialize() {
        Stream.of(ANIMAL_1, ANIMAL_2, ANIMAL_3, ANIMAL_4, ANIMAL_5, UNSAVED_ANIMAL)
                .forEach(animal -> {
                    animal.setName(FAKER.letterify("animal?????"));
                    animal.setAdult(FAKER.random().nextBoolean());
                    animal.setAdopter(null);
                });
    }
}
