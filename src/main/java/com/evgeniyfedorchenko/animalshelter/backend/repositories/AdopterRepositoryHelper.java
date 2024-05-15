package com.evgeniyfedorchenko.animalshelter.backend.repositories;


import com.evgeniyfedorchenko.animalshelter.admin.controllers.SortOrder;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdopterRepositoryHelper {

    private final EntityManager entityManager;

    public AdopterRepositoryHelper(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Adopter> searchAdopters(String sortParam, SortOrder sortOrder, int limit, int offset) {

//        todo Проверить, как работает

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Adopter> cq = cb.createQuery(Adopter.class);
        Root<Adopter> root = cq.from(Adopter.class);
//        cq.select(root);

        Root<Adopter> rootAdopter = entityManager.getCriteriaBuilder()
                .createQuery(Adopter.class)
                .from(Adopter.class);


        if (sortOrder == SortOrder.ASC) {
            cq.orderBy(cb.asc(root.get(sortParam)));
        } else {
            cq.orderBy(cb.desc(root.get(sortParam)));
        }

        return entityManager.createQuery(cq)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
}
