package com.evgeniyfedorchenko.animalshelter.backend.repositories;

import com.evgeniyfedorchenko.animalshelter.backend.entities.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnimalRepository extends JpaRepository<Animal, Long> {

    @Query(value = "SELECT * FROM animals ORDER BY :sortColumn LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Animal> searchAnimalsAscSort(@Param("sortColumn") String sortParam,
                                      @Param("limit") int limit,
                                      @Param("offset") int offset);

    @Query(value = "SELECT * FROM animals ORDER BY :sortColumn DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Animal> searchAnimalsDescSort(@Param("sortColumn") String sortParam,
                                       @Param("limit") int limit,
                                       @Param("offset") int offset);

}
