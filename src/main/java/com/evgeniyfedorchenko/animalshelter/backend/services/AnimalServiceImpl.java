package com.evgeniyfedorchenko.animalshelter.backend.services;

import com.evgeniyfedorchenko.animalshelter.admin.controllers.SortOrder;
import com.evgeniyfedorchenko.animalshelter.backend.dto.AnimalInputDto;
import com.evgeniyfedorchenko.animalshelter.backend.dto.AnimalOutputDto;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Animal;
import com.evgeniyfedorchenko.animalshelter.backend.mappers.AnimalMapper;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.AdopterRepository;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.AnimalRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class AnimalServiceImpl implements AnimalService {

    private final AnimalRepository animalRepository;
    private final AdopterRepository adopterRepository;
    private final AnimalMapper animalMapper;

    @Override
    public Optional<AnimalOutputDto> getAnimal(long id) {
        return animalRepository.findById(id).map(animalMapper::toOutputDto);
    }

    @Override
    public List<AnimalOutputDto> searchAnimals(String sortParam, SortOrder sortOrder, int pageNumber, int pageSize) {

        int offset = (pageNumber - 1) * pageSize;
        List<Animal> animals = sortOrder == SortOrder.ASC
                ? animalRepository.searchAnimalsAscSort(sortParam, pageSize, offset)
                : animalRepository.searchAnimalsDescSort(sortParam, pageSize, offset);

        log.debug("Calling searchAdopters with params: sortParam={}, sortOrder={}, pageNumber={}, pageSize={} returned student's ids: {}",
                sortParam, sortOrder, pageNumber, pageSize, animals.stream().map(Animal::getId).toList());

        return animals.stream().map(animalMapper::toOutputDto).toList();
    }

    @Override
    public boolean assignAnimalToAdopter(long adopterId, long animalId) {

        Optional<Adopter> adopterOpt = adopterRepository.findById(adopterId);
        Optional<Animal> animalOpt = animalRepository.findById(adopterId);

        if (adopterOpt.isEmpty() || animalOpt.isEmpty()) {
            return false;
        }
        Adopter adopter = adopterOpt.get();
        Animal animal = animalOpt.get();

//        todo Придумать как вместо false возвращать причину, почему не получилось связать объекты
        if (adopter.hasAnimal() || animal.hasAdopter()) {
            return false;
        }

        adopter.setAnimal(animal);
        animal.setAdopter(adopter);

        adopterRepository.save(adopter);
        animalRepository.save(animal);

        log.info("Animal {} assigned to adopter {}", animal.getId(), adopter.getId());
        return true;
    }

    @Override
    public boolean deleteAnimalById(long id) {
        Optional<Animal> animal = animalRepository.findById(id);
        if (animal.isEmpty()) {
            log.error("No animal found with id: {}", id);
            return false;
        }
        animalRepository.deleteById(id);
        return true;
    }

    @Override
    public Optional<AnimalOutputDto> addAnimal(AnimalInputDto inputDto) {
        Animal animal = new Animal();

        animal.setName(inputDto.getName());
        animal.setAdult(inputDto.isAdult());
        adopterRepository.findById(inputDto.getAdopterId()).ifPresent(animal::setAdopter);

        Animal savedAnimal = animalRepository.save(animal);
        log.info("Successfully saved: {}", savedAnimal);
        return Optional.of(animalMapper.toOutputDto(savedAnimal));
    }
}
