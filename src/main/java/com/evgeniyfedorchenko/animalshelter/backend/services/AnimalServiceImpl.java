package com.evgeniyfedorchenko.animalshelter.backend.services;

import com.evgeniyfedorchenko.animalshelter.admin.controllers.SortOrder;
import com.evgeniyfedorchenko.animalshelter.backend.dto.AnimalInputDto;
import com.evgeniyfedorchenko.animalshelter.backend.dto.AnimalOutputDto;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Animal;
import com.evgeniyfedorchenko.animalshelter.backend.mappers.AnimalMapper;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.AdopterRepository;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.AnimalRepository;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.RepositoryUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@AllArgsConstructor
public class AnimalServiceImpl implements AnimalService {

    private final AnimalRepository animalRepository;
    private final AdopterRepository adopterRepository;
    private final AnimalMapper animalMapper;
    private final RepositoryUtils repositoryUtils;

    @Override
    @Transactional
    public Optional<AnimalOutputDto> addAnimal(AnimalInputDto inputDto) {
        Animal animal = new Animal();

        animal.setName(inputDto.getName());
        animal.setAdult(inputDto.isAdult());
        animal.setType(inputDto.getType());

        Animal savedAnimal = animalRepository.save(animal);
        log.debug("Animal saved: {}", savedAnimal);
        return Optional.of(animalMapper.toOutputDto(savedAnimal));
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<AnimalOutputDto> getAnimal(long id) {
        return animalRepository.findById(id).map(animalMapper::toOutputDto);
    }

    @Override
    @Transactional
    public List<AnimalOutputDto> searchAnimals(String sortParam, SortOrder sortOrder, int pageNumber, int pageSize) {

        int offset = (pageNumber - 1) * pageSize;
        List<Animal> animals =
                (List<Animal>) repositoryUtils.searchEntities(Animal.class, sortParam, sortOrder, pageSize, offset);

        log.trace("Calling searchAdopters with params: sortParam={}, sortOrder={}, pageNumber={}, pageSize={} returned student's ids: {}",
                sortParam, sortOrder, pageNumber, pageSize, animals.stream().map(Animal::getId).toList());

        return animals.stream().map(animalMapper::toOutputDto).toList();
    }

    @Async
    @Override
    @Transactional
    public CompletableFuture<Boolean> assignAnimalToAdopter(long animalId, long adopterId) {

        CompletableFuture<Boolean> futureBool = CompletableFuture.supplyAsync(() -> {

            Optional<Adopter> adopterOpt = adopterRepository.findById(adopterId);
            Optional<Animal> animalOpt = animalRepository.findById(animalId);

            if (animalOpt.isEmpty()) {
                throw new EntityNotFoundException("Animal with id " + animalId + " not found");
            } else if (adopterOpt.isEmpty()) {
                throw new EntityNotFoundException("Adopter with id " + adopterId + " not found");
            }

//            return true ТОЛЬКО если у адоптера нет животного И у животного нет адоптера
            return !adopterOpt.get().hasAnimal() && !animalOpt.get().hasAdopter();
        });

        futureBool.thenAcceptAsync(result -> {
            if (result) {

                Adopter adopter = adopterRepository.findById(adopterId).orElseThrow();
                Animal animal = animalRepository.findById(animalId).orElseThrow();

                adopter.setAnimal(animal);
                animal.setAdopter(adopter);

                adopterRepository.save(adopter);
                animalRepository.save(animal);

                log.debug("Animal {} assigned to adopter {}", animal.getId(), adopter.getId());
            }
        });
        return futureBool;
    }

    @Override
    @Transactional
    public boolean deleteAnimalById(long id) {
        Optional<Animal> animal = animalRepository.findById(id);
        if (animal.isEmpty()) {
            log.error("No animal found with id: {}", id);
            return false;
        }
        animalRepository.deleteById(id);
        log.debug("Animal deleted {}", animal.get());
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Animal getFreeAnimal() {
        return animalRepository.findFirstByAdopterIsNull().orElseThrow();
    }
}
