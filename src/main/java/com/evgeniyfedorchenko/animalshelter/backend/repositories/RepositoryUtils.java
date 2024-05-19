package com.evgeniyfedorchenko.animalshelter.backend.repositories;

import com.evgeniyfedorchenko.animalshelter.admin.controllers.SortOrder;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

@AllArgsConstructor
@Component
public class RepositoryUtils {

    @PersistenceContext
    private final EntityManager entityManager;

//    todo создать объект для этих параметров
    public List<?> searchEntities(Class<?> entity, String sortParam, SortOrder sortOrder, int limit, int offset) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<?> cq = cb.createQuery(entity);
        Root<?> rootAdopter = cq.from(entity);

        Order order;

        if (isIterableEntities(sortParam)) {
              /* Если sortParam - это что-то итерируемое (например список reports - сортируем на основании
                 размера списка): создаем подзапрос и смотрим размер списка, далее создаем JOIN */
            Subquery<Long> subquery = cq.subquery(Long.class);
            Root<?> subRoot = subquery.from(entity);
            Join<?, ?> subJoin = subRoot.join(sortParam, JoinType.INNER);

            subquery.select(cb.count(subJoin));
            subquery.where(cb.equal(rootAdopter, subRoot));

            Expression<Long> countExpression = subquery.getSelection();
            order = sortOrder == SortOrder.DESC
                    ? cb.asc(subquery.getSelection())
                    : cb.desc(subquery.getSelection());

            /* Подзапрос вернул даже те сущности у которых reports.size = 0, а мы хотим именно INNER JOIN.
               Значит надо выкинуть все объекты у которых по сути нет отчетов */
            cq.where(cb.greaterThan(countExpression, 0L));

        } else if (isAssignableEntity(sortParam)) {

//            Если sortParam - это связанная сущность, то сортируем по id этой сущности и выводим в INNER JOIN
            Join<?, ?> joinTarget = rootAdopter.join(sortParam, JoinType.INNER);
            order = sortOrder == SortOrder.DESC
                    ? cb.desc(joinTarget.get("id"))
                    : cb.asc(joinTarget.get("id"));
        } else {
//            Иначе просто обычная сортировка
            order = sortOrder == SortOrder.DESC
                    ? cb.desc(rootAdopter.get(sortParam))
                    : cb.asc(rootAdopter.get(sortParam));
        }

        cq.orderBy(order);

//        Сам запрос тут
        return entityManager.createQuery(cq)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }


    boolean isIterableEntities(String sortParam) {

        try {
            Field field = Adopter.class.getDeclaredField(sortParam);

            if (field.getGenericType() instanceof ParameterizedType paramType) {
                Type[] typeArguments = paramType.getActualTypeArguments();
                if (typeArguments.length == 1 && typeArguments[0] instanceof Class<?> entityClass) {
                    return Iterable.class.isAssignableFrom(field.getType()) && entityClass.isAnnotationPresent(Entity.class);
                }
            }
        } catch (NoSuchFieldException _) {
            return false;
        }
        return false;
    }

    boolean isAssignableEntity(String fieldName) {
        try {
            Field field = Adopter.class.getDeclaredField(fieldName);
            Class<?> fieldType = field.getType();
            return fieldType.isAnnotationPresent(Entity.class);
        } catch (NoSuchFieldException _) {
            return false;
        }
    }


}
