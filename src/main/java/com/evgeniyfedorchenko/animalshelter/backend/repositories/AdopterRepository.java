package com.evgeniyfedorchenko.animalshelter.backend.repositories;

import com.evgeniyfedorchenko.animalshelter.admin.controllers.SortOrder;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

public interface AdopterRepository extends JpaRepository<Adopter, Long> {
}
