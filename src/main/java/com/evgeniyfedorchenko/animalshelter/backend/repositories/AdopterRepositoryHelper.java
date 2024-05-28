package com.evgeniyfedorchenko.animalshelter.backend.repositories;


import com.evgeniyfedorchenko.animalshelter.admin.controllers.SortOrder;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;


@Component
public class AdopterRepositoryHelper {

    @PersistenceContext
    private final EntityManager entityManager;

    public AdopterRepositoryHelper(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Метод предоставляет возможность осуществлять поиск сущностей в таблице adopters по заданному полю этой сущности.
     * Если в качестве целевого поля задано:
     * <ul>
     *     <li>Поле, представляющее Iterable-объект, то сортировка будут осуществляться на основании количества элементов
     *         в объекте (для ASC первыми будут сущности, у которых связанное поле указывает на самую объемную коллекцию)</li>
     *     <li>Поле, являющееся связанной сущностью - сортировка будет осуществляться на основании поля id этой сущности</li>
     *     <li>Иначе будет происходить сортировка по обычным правилам</li>
     * </ul>
     * @param sortParam Строковое название целевого поля для поиска
     * @param sortOrder Порядок сортировки (ASC или DESC), смотри {@link SortOrder}
     * @param limit Количество возвращаемых полей (используется для пагинации)
     * @param offset Количество пропускаемых полей (используется для пагинации)
     * @return Список объектов {@link Adopter} сформированный по указанным правилам
     */
    public List<Adopter> searchAdopters(String sortParam, SortOrder sortOrder, int limit, int offset) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Adopter> cq = cb.createQuery(Adopter.class);
        Root<Adopter> rootAdopter = cq.from(Adopter.class);

        Order order;

        if (isIterableEntities(sortParam)) {
              /* Если sortParam - это что-то итерируемое (например список reports - сортируем на основании размера списка):
                 создаем подзапрос и смотри размер списка, далее создаем JOIN */
            Subquery<Long> subquery = cq.subquery(Long.class);
            Root<Adopter> subRoot = subquery.from(Adopter.class);
            Join<Adopter, ?> subJoin = subRoot.join(sortParam, JoinType.INNER);

            subquery.select(cb.count(subJoin));
            subquery.where(cb.equal(rootAdopter, subRoot));

            Expression<Long> countExpression = subquery.getSelection();
            if (sortOrder == SortOrder.DESC) {
                order = cb.asc(countExpression);
            } else {
                order = cb.desc(countExpression);
            }
              /* Подзапрос вернул даже те сущности у которых reports.size = 0, а мы хотим именно INNER JOIN.
                 Значит надо выкинуть все объекты у которых по сути нет отчетов */
            cq.where(cb.greaterThan(countExpression, 0L));

        } else if (isAssignableEntity(sortParam)) {
            /* Если sortParam - это связанная сущность - то сортируем по id этой сущности и выводим в INNER JOIN */
            Join<Adopter, ?> joinTarget = rootAdopter.join(sortParam, JoinType.INNER);
            if (sortOrder == SortOrder.DESC) {
                order = cb.desc(joinTarget.get("id"));
            } else {
                order = cb.asc(joinTarget.get("id"));
            }

        } else {
//            Иначе просто обычная сортировка
            if (sortOrder == SortOrder.DESC) {
                order = cb.desc(rootAdopter.get(sortParam));
            } else {
                order = cb.asc(rootAdopter.get(sortParam));
            }
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
            Type fieldType = field.getGenericType();

            if (fieldType instanceof ParameterizedType paramType) {
                Type[] typeArguments = paramType.getActualTypeArguments();
                if (typeArguments.length == 1 && typeArguments[0] instanceof Class<?> entityClass) {
                    return Collection.class.isAssignableFrom(field.getType()) && entityClass.isAnnotationPresent(Entity.class);
                }
            }
        } catch (NoSuchFieldException e) {
            return false;
        }
        return false;
    }

    private boolean isAssignableEntity(String fieldName) {
        try {
            Field field = Adopter.class.getDeclaredField(fieldName);
            Class<?> fieldType = field.getType();
            return fieldType.isAnnotationPresent(Entity.class);
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

}
