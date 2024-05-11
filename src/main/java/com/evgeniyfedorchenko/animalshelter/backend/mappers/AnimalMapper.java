package com.evgeniyfedorchenko.animalshelter.backend.mappers;

import com.evgeniyfedorchenko.animalshelter.backend.dto.AnimalOutputDto;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Animal;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AnimalMapper {

    public AnimalOutputDto toOutputDto(Animal animal) {
        AnimalOutputDto animalOutputDto = new AnimalOutputDto();

        animalOutputDto.setId(animal.getId());
        animalOutputDto.setName(animal.getName());
        animalOutputDto.setAdult(animal.isAdult());

        Optional.ofNullable(animal.getAdopter()).ifPresent(adopter -> {
                    animalOutputDto.setAdopterId(adopter.getId());
                    animalOutputDto.setAdopterName(adopter.getName());
                });

        return  animalOutputDto;
    }
}
