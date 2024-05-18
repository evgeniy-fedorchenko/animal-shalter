package com.evgeniyfedorchenko.animalshelter;

import com.evgeniyfedorchenko.animalshelter.admin.controllers.SortOrder;
import com.evgeniyfedorchenko.animalshelter.backend.dto.AdopterInputDto;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TestUtils {

    @PersistenceContext
    private EntityManager entityManager;

    public AdopterInputDto toInputDto(Adopter adopter) {
        AdopterInputDto inputDto = new AdopterInputDto();

        inputDto.setChatId(adopter.getChatId());
        inputDto.setName(adopter.getName());
        inputDto.setPhoneNumber(adopter.getPhoneNumber());
        Optional.ofNullable(adopter.getAnimal())
                .ifPresent(animal -> inputDto.setAnimalId(animal.getId()));

        return inputDto;
    }

    public List<Adopter> searchAdopters(String entity, String sortParam, SortOrder sortOrder, int limit, int offset) {

        String jpql = "SELECT x FROM " + entity + " x ";

        if (sortParam.equals("reports")) {
            jpql += "INNER JOIN x." + sortParam + " y ";
            if (sortOrder == SortOrder.DESC) {
                jpql += "ORDER BY SIZE(y) ASC";
            } else {
                jpql += "ORDER BY SIZE(y) DESC";
            }

        } else if (sortParam.equals("animal") || sortParam.equals("adopter")) {
            jpql += "INNER JOIN x.animal z ";
            if (sortOrder == SortOrder.DESC) {
                jpql += "ORDER BY z.id DESC";
            } else {
                jpql += "ORDER BY z.id ASC";
            }

        } else {
            jpql += "WHERE x." + sortParam + " IS NOT NULL ";
            if (sortOrder == SortOrder.DESC) {
                jpql += "ORDER BY x." + sortParam + " DESC";
            } else {
                jpql += "ORDER BY x." + sortParam + " ASC";
            }
        }
        return entityManager.createQuery(jpql, Adopter.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();

    }
}
