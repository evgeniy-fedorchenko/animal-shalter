package com.evgeniyfedorchenko.animalshelter.backend.services;

import com.evgeniyfedorchenko.animalshelter.admin.controllers.SortOrder;
import com.evgeniyfedorchenko.animalshelter.backend.dto.AnimalInputDto;
import com.evgeniyfedorchenko.animalshelter.backend.dto.AnimalOutputDto;

import java.util.List;
import java.util.Optional;

public interface AnimalService {

    Optional<AnimalOutputDto> addAnimal(AnimalInputDto inputDto);

    Optional<AnimalOutputDto> getAnimal(long id);

    List<AnimalOutputDto> searchAnimals(String sortParam, SortOrder sortOrder, int pageNumber, int pageSize);

    boolean assignAnimalToAdopter(long adopterId, long animalId);

    boolean deleteAnimalById(long id);
}
