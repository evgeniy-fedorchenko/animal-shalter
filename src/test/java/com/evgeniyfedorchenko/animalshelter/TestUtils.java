package com.evgeniyfedorchenko.animalshelter;

import com.evgeniyfedorchenko.animalshelter.admin.controllers.SortOrder;
import com.evgeniyfedorchenko.animalshelter.backend.dto.AdopterInputDto;
import com.evgeniyfedorchenko.animalshelter.backend.dto.AnimalInputDto;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Animal;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component
public class TestUtils<E> {

    private final Random random = new Random();

    @PersistenceContext
    private EntityManager entityManager;

    @Getter
    @AllArgsConstructor
    public enum Format {
        PNG("png", MediaType.IMAGE_PNG),
        JPG("jpg", MediaType.IMAGE_JPEG);

        private final String imageFormat;
        private final MediaType mediaType;
    }

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

        return inputDto;
    }

    /**
     * Попытка создать более-менее сложный запрос в БД еще каким-нибудь новым способом.
     * Построить строку, а потом кинуть ее в {@code EntityManager.createQuery}
     *
     * @param entity    Класс, чьи сущности будем искать. Нужен, чтоб указать {@code EntityManager}, кого искать вообще
     * @param sortParam Название поля по которому нужно сделать поиск
     * @param sortOrder Константа {@link SortOrder}. Указывает порядок результатов (ASC или DESC)
     * @param limit     Задает требуемое кол-во сущностей (для пагинации)
     * @param offset    Задает кол-во сущностей, которые нужно пропустить (для пагинации)
     * @return Список сущностей, найденных по заданным параметрам. Имеет тайп-параметр, переданный
     * в первом параметре {@code  Class<E> entity} ({@code E} - сокращение от <b>Entity</b>)
     */
    public List<E> searchEntities(Class<E> entity, String sortParam, SortOrder sortOrder, int limit, int offset) {
        StringBuilder queryBuilder = new StringBuilder("SELECT x FROM ");
        queryBuilder.append(entity.getName()).append(" x ");

        if (sortParam.equals("reports")) {
            queryBuilder.append("INNER JOIN x.").append(sortParam).append(" y ");
            if (sortOrder == SortOrder.DESC) {
                queryBuilder.append("ORDER BY SIZE(y) ASC");
            } else {
                queryBuilder.append("ORDER BY SIZE(y) DESC");
            }

        } else if (sortParam.equals("animal") || sortParam.equals("adopter")) {
            queryBuilder.append("INNER JOIN x.").append(sortParam).append(" z ");
            if (sortOrder == SortOrder.DESC) {
                queryBuilder.append("ORDER BY z.id DESC");
            } else {
                queryBuilder.append("ORDER BY z.id ASC");
            }

        } else {
            queryBuilder.append("WHERE x." ).append(sortParam).append(" IS NOT NULL ");
            if (sortOrder == SortOrder.DESC) {
                queryBuilder.append("ORDER BY x.").append(sortParam).append(" DESC");
            } else {
                queryBuilder.append("ORDER BY x.").append(sortParam).append(" ASC");
            }
        }
        return entityManager.createQuery(queryBuilder.toString(), entity)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public RestTemplate patchRestTemplate(TestRestTemplate testRestTemplate) {
        RestTemplate patchedRestTemplate = testRestTemplate.getRestTemplate();
        HttpClient httpClient = HttpClientBuilder.create().build();
        patchedRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
        return patchedRestTemplate;
    }


    /**
     * Метод для генерации изображения в виде байтового массива. Нужен для тестирования методов связанных
     * с изображениями. Массив байт не записывается в файл, а удерживается в памяти.
     *
     * @param format Константа перечисления {@link Format}, задает формат выходного изображения
     * @return массив байт, созданный по правилам указанного формата
     * @throws IOException если во время записи возникает ошибка или не удается создать требуемый ImageOutputStream
     */
    public byte[] createImage(Format format) {
        int width = random.nextInt(500, 1000);
        int height = width + 1;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(Color.BLUE);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 36));
        g2d.drawString("Fake Image " + format, 200, 200);

        g2d.dispose();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, format.getImageFormat(), baos);
            return baos.toByteArray();

        } catch (IOException ex) {
            throw new RuntimeException("Filed to create an image. Ex: ", ex);
        }
    }

    /**
     * Метод возвращает случайно число от 1 до Long.MAX_VALUE,
     * которое не совпадает с айдишниками коллекции переданных сущностей
     * @param entityCollection коллекция сущностей для сравнения. Возвращаемое число
     *                         не будет равняться с id любой из этих сущностей
     * @return положительное число, не совпадающее ни с одним id переданных сущностей
     * @throws IllegalArgumentException если переданная коллекция параметризована классом неизвестной сущности
     * @throws NullPointerException если переданная коллекция пуста или не инициализирована
     */
    public long getIdNonExistsIn(List<Object> entityCollection) {
        /* Сначала просто хотел вернуть entityCollection.size() + 1, но потом понял, id могут быть любые, а не строго
           по порядку. Да, в этом проекте id генерятся по порядку, но все же, лучше пусть будет более универсально */

        if (entityCollection == null || entityCollection.isEmpty()) {
            throw new NullPointerException("entityCollection is null or empty");
        }

//        List<Long> existingIdx = switch (entityCollection) {
//            case List<?> adopters when adopters.getFirst() instanceof Adopter ->
//                new ArrayList<>((List<? extends Adopter>) adopters).stream().map(Adopter::getId).toList();
//
//            case List<?> animals when animals instanceof Animal ->
//                    animals.stream().map(obj -> (Animal) obj).map(Animal::getId).toList();
//
//            case List<?> reports when reports.getFirst() instanceof Report ->
//                    reports.stream().map(obj -> (Report) obj).map(Report::getId).toList();
//
//            default -> throw new IllegalStateException("Unexpected value: " + entityCollection);
//        };

        if (!entityCollection.getFirst().getClass().isAnnotationPresent(Entity.class)) {
            throw new IllegalStateException("Unexpected value: " + entityCollection);
        }
//        Дурацкие checked-исключения вообще не дружат со stream api :(
        List<Long> existingIds = entityCollection.stream()
                .map(entity -> {
                    try {
                        return (Long) entity.getClass().getMethod("getId").invoke(entity);
                    } catch (ReflectiveOperationException _) {
                        throw new RuntimeException("Reflect operation <.getMethod(\"getId\")> filed. Class: %s"
                                .formatted(entity.getClass().getSimpleName()));
                    }
                }).toList();

        long nonExistId;
        do {
            nonExistId = random.nextInt(1, Integer.MAX_VALUE);
        } while (existingIds.contains(nonExistId));
        return nonExistId;
    }
}
