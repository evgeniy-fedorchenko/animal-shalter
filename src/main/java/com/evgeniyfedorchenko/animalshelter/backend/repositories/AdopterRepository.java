package com.evgeniyfedorchenko.animalshelter.backend.repositories;

import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AdopterRepository extends JpaRepository<Adopter, Long> {

    @Query(value = "SELECT * FROM adopters ORDER BY :sortColumn LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Adopter> searchAdoptersAscSort(@Param("sortColumn") String sortParam,
                                        @Param("limit") int limit,
                                        @Param("offset") int offset);

    @Query(value = "SELECT * FROM adopters ORDER BY :sortColumn DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Adopter> searchAdoptersDescSort(@Param("sortColumn") String sortParam,
                                         @Param("limit") int limit,
                                         @Param("offset") int offset);
}
