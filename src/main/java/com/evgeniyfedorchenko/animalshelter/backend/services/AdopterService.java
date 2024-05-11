package com.evgeniyfedorchenko.animalshelter.backend.services;

import com.evgeniyfedorchenko.animalshelter.admin.controllers.SortOrder;
import com.evgeniyfedorchenko.animalshelter.backend.dto.AdopterInputDto;
import com.evgeniyfedorchenko.animalshelter.backend.dto.AdopterOutputDto;

import java.util.List;
import java.util.Optional;

public interface AdopterService {

    Optional<AdopterOutputDto> addAdopter(AdopterInputDto adopter);

    Optional<AdopterOutputDto> getAdopter(long id);

    List<AdopterOutputDto> searchAdopters(String sortParam, SortOrder sortOrder, int pageSize, int pageNumber);

    boolean deleteAdopter(long id);
}
