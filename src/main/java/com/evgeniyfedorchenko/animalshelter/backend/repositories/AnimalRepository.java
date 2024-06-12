package com.evgeniyfedorchenko.animalshelter.backend.repositories;

import com.evgeniyfedorchenko.animalshelter.backend.entities.Animal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnimalRepository extends JpaRepository<Animal, Long> {

//    @Query("SELECT a FROM Animal a WHERE a.adopter IS NULL")
    Optional<Animal> findFirstByAdopterIsNull();
}
