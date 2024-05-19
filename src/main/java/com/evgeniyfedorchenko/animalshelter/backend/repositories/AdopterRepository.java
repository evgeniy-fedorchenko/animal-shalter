package com.evgeniyfedorchenko.animalshelter.backend.repositories;


import com.evgeniyfedorchenko.animalshelter.admin.controllers.SortOrder;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;


//@AllArgsConstructor
@Component
public interface AdopterRepository extends JpaRepository<Adopter, Long> {

//    @PersistenceContext
//    private final EntityManager entityManager;

//    private final RepositoryUtils repositoryUtils;

    /**
     * Метод предоставляет возможность осуществлять поиск сущностей в таблице adopters по заданному полю этой сущности.
     * Если в качестве целевого поля задано:
     * <ul>
     *     <li>Поле, представляющее Iterable-объект, то сортировка будут осуществляться на основании количества элементов
     *         в объекте (для ASC первыми будут сущности, у которых связанное поле указывает на самую объемную коллекцию)</li>
     *     <li>Поле, являющееся связанной сущностью - сортировка будет осуществляться на основании поля id этой сущности</li>
     *     <li>Иначе будет происходить сортировка по обычным правилам</li>
     * </ul>
     *
     * @param sortParam Строковое название целевого поля для поиска
     * @param sortOrder Порядок сортировки (ASC или DESC), смотри {@link SortOrder}
     * @param limit     Количество возвращаемых полей (используется для пагинации)
     * @param offset    Количество пропускаемых полей (используется для пагинации)
     * @return Список объектов {@link Adopter} сформированный по указанным правилам
     */
    default List<Adopter> searchAdopters(String sortParam, SortOrder sortOrder, int limit, int offset) {
        return null;
    }
}
