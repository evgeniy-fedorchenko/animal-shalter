package com.evgeniyfedorchenko.animalshelter;

import com.evgeniyfedorchenko.animalshelter.admin.controllers.SortOrder;
import com.evgeniyfedorchenko.animalshelter.backend.dto.AdopterInputDto;
import com.evgeniyfedorchenko.animalshelter.backend.dto.AnimalInputDto;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Animal;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.serialization.MaybeInaccessibleMessageDeserializer;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
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
        //        ImageIO.write() не желает принимать "image/png", только "png"; аналогично с jpeg
        PNG(MediaType.IMAGE_PNG_VALUE, "png"),
        JPG(MediaType.IMAGE_JPEG_VALUE, "jpg");

        private final String mediaType;
        private final String mediaTypeForImageIO;

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
        inputDto.setType(animal.getType());

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
            queryBuilder.append("WHERE x.").append(sortParam).append(" IS NOT NULL ");
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
     * @throws RuntimeException если во время записи возникает ошибка или не удается создать требуемый ImageOutputStream
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
            ImageIO.write(image, format.getMediaTypeForImageIO(), baos);
            return baos.toByteArray();

        } catch (IOException ex) {
            throw new RuntimeException("Filed to create an image. Ex: ", ex);
        }
    }

    /**
     * Метод возвращает случайно число от 1 до Long.MAX_VALUE,
     * которое не совпадает с айдишниками коллекции переданных сущностей
     *
     * @param entityCollection коллекция сущностей для сравнения. Возвращаемое число
     *                         не будет равняться с id любой из этих сущностей
     * @return положительное число, не совпадающее ни с одним id переданных сущностей
     * @throws IllegalStateException если переданная коллекция параметризована классом, не аннотированным {@link Entity}
     * @throws NullPointerException  если переданная коллекция пуста или не инициализирована
     * @throws RuntimeException      если в классе сущности не доступен метода {@code getId()}
     */
    public long getIdNonExistsIn(List<Object> entityCollection) {

        if (entityCollection == null || entityCollection.isEmpty()) {
            throw new NullPointerException("entityCollection is null or empty");
        }

        if (!entityCollection.getFirst().getClass().isAnnotationPresent(Entity.class)) {
            throw new IllegalStateException("Unexpected value: " + entityCollection);
        }

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


    /**
     * Метод для получения объекта Update из файла json, находящегося в ресурсах. Если {@code withPhoto = true},
     * то {@code isCommand} не имеет значения
     *
     * @param messText  Текст, который будет помещен в Update.getMessage().getText()
     * @param isCommand Указывает, что это команда бота или нет. Если установлено в {@code true}, то помимо прочего
     *                  из параметра {@code messText} будет сгенерирована соответствующая {@link MessageEntity}
     *                  и помещена в массив {@code Update.getMessage().getEntities()}.
     *                  Проверка на {@code isCommand} вернет {@code true}
     * @param withPhoto Указывает должен ли присутствовать в сообщении {@code List<PhotoSize>}.
     *                  Список в этом случае будет содержать один пустой объект
     * @return Объект Update, настроенный по указанным параметрам
     */
    public Update getUpdateWithMessage(@Nullable String messText, boolean isCommand, boolean withPhoto) {

        Update update;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String json = Files.readString(new ClassPathResource("test-message-update.json").getFile().toPath());
            if (messText == null) {
                update = objectMapper.readValue(json, Update.class);
                update.getMessage().setText(null);

                if (withPhoto) {
                    update.getMessage().setPhoto(List.of(new PhotoSize()));
                    return update;
                }
                return update;
            }
            String replaced = json.replace("%toReplace%", messText);
            update = objectMapper.readValue(replaced, Update.class);

        } catch (IOException ex) {
            throw new RuntimeException("Could not read test-message-update.json", ex);
        }

        if (!isCommand) {
            return update;
        }

        /* Чтобы сработал метод Message.isCommand() нужно засетить
           в update новую MessageEntity и указать что это "botCommand" */
        MessageEntity botCommand = new MessageEntity("bot_command", 0, messText.length());
        botCommand.setText(messText);
        update.getMessage().setEntities(Collections.singletonList(botCommand));
        return update;
    }

    public Update getUpdateWithCallback(String callbackData) {

        String fileName = "test-callback-update.json";
        try {
            String json = Files.readString(new ClassPathResource(fileName).getFile().toPath());
            String replaced = json.replace("%toReplace%", callbackData);

            ObjectMapper objectMapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();


            module.addDeserializer(MaybeInaccessibleMessage.class, new MaybeInaccessibleMessageDeserializer());
            objectMapper.registerModule(module);

            return objectMapper.readValue(replaced, Update.class);

        } catch (IOException ex) {
            throw new RuntimeException("Could not read " + fileName, ex);
        }
    }

    public Update getUpdateWithSticker() {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = Files.readString(new ClassPathResource("test-message-update.json").getFile().toPath());

            Update update = objectMapper.readValue(json, Update.class);
            update.getMessage().setText(null);
            update.getMessage().setSticker(new Sticker());

            return update;

        } catch (IOException ex) {
            throw new RuntimeException("Could not read test-message-update.json", ex);
        }
    }
}
