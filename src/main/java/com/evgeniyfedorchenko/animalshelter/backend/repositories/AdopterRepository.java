package com.evgeniyfedorchenko.animalshelter.backend.repositories;

import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdopterRepository extends JpaRepository<Adopter, Long> {

    Optional<Adopter> findByChatId(long chatId);
}
