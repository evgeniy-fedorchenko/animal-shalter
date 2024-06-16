package com.evgeniyfedorchenko.animalshelter.backend.repositories;

import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AdopterRepository extends JpaRepository<Adopter, Long> {

    Optional<Adopter> findByChatId(String chatId);

    @Query("SELECT a FROM Adopter a WHERE a.assignedReportsQuantity = (SELECT COUNT(r) FROM Report r WHERE r.adopter = a)")
    List<Adopter> findAdoptersWithMatchingQuantityReports();

//    List<Adopter> findByQuantityEqualsAllReportCount();
}
