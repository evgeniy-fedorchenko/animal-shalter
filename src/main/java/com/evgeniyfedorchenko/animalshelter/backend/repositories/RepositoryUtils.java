package com.evgeniyfedorchenko.animalshelter.backend.repositories;

import com.evgeniyfedorchenko.animalshelter.admin.controllers.SortOrder;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

@AllArgsConstructor
@Component
public class RepositoryUtils {

    @PersistenceContext
    private final EntityManager entityManager;

    /**
     * Метод предлагает гибкий поиск и отображение различных сущностей, находящимся в целевой базе данных.
     * Поддерживается множество параметров
     * @param entity Экземпляр класса искомых сущностей. Определяет тип объектов, среди которых будет происходить поиск
     *               Итоговый результат будет параметризован этим классом
     * @param sortParam Название поля сущности, на которое следует опираться по поиске. Если под этим названием в
     *                  представленном классе лежит итерируемый объект, то сортировка результатов поиска будет
     *                  происходить на основе количества элементов. Если под этим названием лежит связанная сущность -
     *                  - сортировка будет происходить по полю id этих сущностей
     * @param sortOrder Порядок сортировки (ASC или DESC), константа перечисления {@link SortOrder}
     * @param limit Количество элементов для отображения (для пагинации)
     * @param offset Количество элементов, которые нужно пропустить (для пагинации)
     * @return Список объектов, найденных и отсортированных в соответствии с указанными параметрами
     */
    @Transactional(readOnly = true)
    public List<?> searchEntities(Class<?> entity, String sortParam, SortOrder sortOrder, int limit, int offset) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<?> cq = cb.createQuery(entity);
        Root<?> rootAdopter = cq.from(entity);

        Order order;

        if (isIterableEntities(entity, sortParam)) {
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

        } else if (isAssignableEntity(entity, sortParam)) {

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


    /* Этот метод, создан для того, чтобы быстро детектить такие поля, как например 'List<Report>'
       В этом случае в sortParam будет лежать 'reports'. Мы смотрим, что у класса есть такое поле, что оно
       является коллекцией и что оно параметризовано СУЩНОСТЬЮ (ведь мы будем его искать в бд)*/

    /**
     * Метод проверяет, содержится ли в поле какой-то итерируемый и параметризованный сущностью объект. Алгоритм:
     * <ul>
     *     <li>1. У класса {@code entityClass} получаем поле под названием, переданным в {@code fieldName}</li>
     *     <li>2. Если оно параметризовано, то смотрим чем именно (смотрим, что там один аргумент, а не
     *            два например, как в {@code Map}) и смотрим что поле параметризовано именно классом</li>
     *     <li>3. Проверяем что fieldName реализует интерфейс Iterable и что entityClass является
     *            сущностью (имеет аннотацию {@code @Entity})</li>
     * </ul>
     * Только если все условия вернули true мы можем сказать, что поле {@code fieldName} является итерируемым
     * и что оно параметризовано классом {@code entityClass}
     *
     * @param entityClass класс, представляющий собой сущность, которой должно быть параметризовано поле,
     *                    представленное в {@code fieldName}
     * @param fieldName   строковое название поля, которое проверяется на параметризацию и итерируемость
     * @return true, если fieldName содержит название итерируемого поля, которое параметризовано сущностью entityClass
     */
    boolean isIterableEntities(Class<?> entityClass, String fieldName) {

        try {
            Field field = entityClass.getDeclaredField(fieldName);

            if (field.getGenericType() instanceof ParameterizedType paramType) {
                Type[] typeArguments = paramType.getActualTypeArguments();
                if (typeArguments.length == 1 && typeArguments[0] instanceof Class<?> parametrizedClass) {
                    return Iterable.class.isAssignableFrom(field.getType())
                            && parametrizedClass.isAnnotationPresent(Entity.class);
                }
            }
        } catch (NoSuchFieldException _) {
            return false;
        }
        return false;
    }

    /**
     * Метод проверяет, что поле представленное в {@code fieldName} содержит сущность и само находится
     * в классе сущности. А так же что поле помечено аннотацией отношений (ассоциаций) между сущностями
     * @param entityClass класс сущности, которой должно принадлежать поле
     * @param fieldName название поля, которое должно иметь аннотацию ассоциаций сущностей содержать объект сущности
     * @return true, если в поле {@code fieldName} содержится сущность класса {@code entityClass}
     */
    boolean isAssignableEntity(Class<?> entityClass, String fieldName) {
        try {
            Field field = entityClass.getDeclaredField(fieldName);
            Class<?> fieldType = field.getType();
            boolean isFieldAssign = field.isAnnotationPresent(OneToOne.class)
                                 || field.isAnnotationPresent(OneToMany.class)
                                 || field.isAnnotationPresent(ManyToOne.class)
                                 || field.isAnnotationPresent(ManyToMany.class);
// FIXME 25.05.2024 15:19 - добавить реальную проверку связи, а не просто наличие аннотации
            return isFieldAssign
                    && entityClass.isAnnotationPresent(Entity.class)
                    && fieldType.isAnnotationPresent(Entity.class);
        } catch (NoSuchFieldException _) {
            return false;
        }
    }
}
