package com.evgeniyfedorchenko.animalshelter;

import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Animal;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Report;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Volunteer;
import net.datafaker.Faker;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Constants {

    private static final Faker FAKER = new Faker();

    public static List<Adopter> generateTestAdoptersInCountOf(int count) {

        List<Adopter> testAdopters = new ArrayList<>(count);

        Stream.generate(Adopter::new)
                .limit(count)
                .forEach(adopter -> {
                    adopter.setChatId(String.valueOf(FAKER.random().nextLong(1L, 9999999999L)));
                    adopter.setName(FAKER.letterify("adopterName?????"));
                    adopter.setPhoneNumber("79" + FAKER.number().digits(9));
                    adopter.setAssignedReportsQuantity(30);
                    adopter.setReports(new ArrayList<>());
                    adopter.setAnimal(null);

                    testAdopters.add(adopter);
                });

        return testAdopters;
    }

    public static List<Animal> generateTestAnimalsInCountOf(int count) {

        List<Animal> testAnimals = new ArrayList<>(count);

        Stream.generate(Animal::new)
                .limit(count)
                .forEach(animal -> {
                    animal.setName(FAKER.letterify("animalName?????"));
                    animal.setAdult(FAKER.random().nextBoolean());
                    animal.setType(FAKER.random().nextBoolean() ? Animal.Type.CAT : Animal.Type.DOG);
                    animal.setAdopter(null);

                    testAnimals.add(animal);
                });

        return testAnimals;
    }

    public static List<Report> generateTestReportsInCountOf(int count) {

        List<Report> testReports = new ArrayList<>(count);

        Stream.generate(Report::new)
                .limit(count)
                .forEach(report -> {
                    report.setDiet(FAKER.letterify("reportDiet?????"));
                    report.setHealth(FAKER.letterify("reportHealth?????"));
                    report.setChangeBehavior(FAKER.letterify("reportCB?????"));
                    report.setPhotoData(null);
                    report.setMediaType(null);
                    report.setSendingAt(Instant.now());
                    report.setVerified(FAKER.random().nextBoolean());
                    report.setAccepted(false);
                    report.setAdopter(null);

                    testReports.add(report);
                });

        return testReports;
    }

    public static List<Volunteer> generateTestVolunteersInCountOf(int count, double probabilityFree) {
        List<Volunteer> testVolunteers = new ArrayList<>();

        Stream.generate(Volunteer::new)
                .limit(count)
                .forEach(volunteer -> {
                    volunteer.setName(FAKER.letterify("volunteerName?????"));
                    volunteer.setChatId(String.valueOf(FAKER.random().nextLong(1L, 9999999999L)));
                    volunteer.setFree(Math.random() < probabilityFree);

                    testVolunteers.add(volunteer);
                });

        return testVolunteers;
    }
}
