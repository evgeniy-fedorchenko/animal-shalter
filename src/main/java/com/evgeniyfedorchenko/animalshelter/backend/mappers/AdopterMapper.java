package com.evgeniyfedorchenko.animalshelter.backend.mappers;

import com.evgeniyfedorchenko.animalshelter.backend.dto.AdopterOutputDto;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AdopterMapper {

    public AdopterOutputDto toOutputDto(Adopter adopter) {
        AdopterOutputDto outputDto = new AdopterOutputDto();

        outputDto.setId(adopter.getId());
        outputDto.setChatId(adopter.getChatId());
        outputDto.setName(adopter.getName());
        outputDto.setPhoneNumber(adopter.getPhoneNumber());
        outputDto.setAssignedReportsQuantity(adopter.getAssignedReportsQuantity());

        Optional.ofNullable(adopter.getAnimal())
                .ifPresent(animal -> {
                    outputDto.setAnimalId(animal.getId());
                    outputDto.setAnimalName(animal.getName());
                });

        return outputDto;
    }
}
