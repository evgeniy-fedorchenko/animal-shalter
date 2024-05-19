package com.evgeniyfedorchenko.animalshelter;

import com.evgeniyfedorchenko.animalshelter.admin.controllers.SortOrder;
import com.evgeniyfedorchenko.animalshelter.backend.dto.AdopterInputDto;
import com.evgeniyfedorchenko.animalshelter.backend.dto.AnimalInputDto;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Animal;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Component
public class TestUtils<E> {

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

    public AnimalInputDto toInputDto(Animal animal) {
        AnimalInputDto inputDto = new AnimalInputDto();

        inputDto.setName(animal.getName());
        inputDto.setAdult(animal.isAdult());
//        Optional.ofNullable(animal.getAdopter())
//                .ifPresent(adopter -> {
//                    inputDto.setAdopterId(adopter.getId());
//                });

        return inputDto;
    }

    public List<E> searchEntities(Class<E> entity, String sortParam, SortOrder sortOrder, int limit, int offset) {

        String jpql = "SELECT x FROM " + entity.getSimpleName() + " x ";

        if (sortParam.equals("reports")) {
            jpql += "INNER JOIN x." + sortParam + " y ";
            if (sortOrder == SortOrder.DESC) {
                jpql += "ORDER BY SIZE(y) ASC";
            } else {
                jpql += "ORDER BY SIZE(y) DESC";
            }

        } else if (sortParam.equals("animal") || sortParam.equals("adopter")) {
            jpql += "INNER JOIN x." + sortParam + " z ";
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
        return entityManager.createQuery(jpql, entity)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();

    }

    public RestTemplate patchedRestTemplate(TestRestTemplate testRestTemplate) {
        RestTemplate patchRestTemplate = testRestTemplate.getRestTemplate();
        HttpClient httpClient = HttpClientBuilder.create().build();
        patchRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
        return patchRestTemplate;
    }
}
