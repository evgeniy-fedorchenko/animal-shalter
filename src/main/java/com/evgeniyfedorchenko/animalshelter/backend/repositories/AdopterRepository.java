package com.evgeniyfedorchenko.animalshelter.backend.repositories;

import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdopterRepository extends JpaRepository<Adopter, Long> {
}
