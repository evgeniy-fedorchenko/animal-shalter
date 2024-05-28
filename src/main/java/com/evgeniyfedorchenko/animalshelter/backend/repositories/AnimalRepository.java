package com.evgeniyfedorchenko.animalshelter.backend.repositories;

import com.evgeniyfedorchenko.animalshelter.backend.entities.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

public interface AnimalRepository extends JpaRepository<Animal, Long> {
}
