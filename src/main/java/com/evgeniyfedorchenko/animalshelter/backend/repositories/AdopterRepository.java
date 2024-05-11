package com.evgeniyfedorchenko.animalshelter.backend.repositories;

import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdopterRepository extends JpaRepository<Adopter, Long> {

    /*
        К сожалению я не придумал, как сделать эти запросы на JPQL и подставлять туда параметры динамически,
        кажется там можно написать что-то типа:
            SELECT a FROM adopters a ORDER BY CASE
                    WHEN :sortColumn = 'id' THEN a.id
                    WHEN :sortColumn = 'name' THEN a.name
                    WHEN :sortColumn = 'age' THEN s.phoneNumber
            END ASC
        или что-то типа того, но мне не удалось заставить это нормально работать. Нужно больше времени, чтоб разобраться.
        Так что пока используем нативный SQL. И да, почему-то порядок сортировки тоже не подставляется, думаю, это из-за
        того, что ASC и DESC - типа зарезервированные слова, но это не точно
*/
    @Query(value = "SELECT * FROM adopters ORDER BY :sortColumn LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Adopter> searchAdoptersAscSort(@Param("sortColumn") String sortParam,
                                        @Param("limit") int limit,
                                        @Param("offset") int offset);

    @Query(value = "SELECT * FROM adopters ORDER BY :sortColumn DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Adopter> searchAdoptersDescSort(@Param("sortColumn") String sortParam,
                                         @Param("limit") int limit,
                                         @Param("offset") int offset);
}
