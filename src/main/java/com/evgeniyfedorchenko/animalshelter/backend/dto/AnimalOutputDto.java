package com.evgeniyfedorchenko.animalshelter.backend.dto;

import com.evgeniyfedorchenko.animalshelter.backend.entities.Animal;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Представление объекта Animal, сохраненного в базу данных, а так же основных полей связанного с ним объекта Adopter, если таковой имеется")
public class AnimalOutputDto {

    @Schema(description = "Id of this animal", example = "1")
    private long id;

    @Schema(description = "The name of this animal", example = "Fluffy")
    private String name;

    @Schema(description = "The  boolean-pointer to the adulthood of the animal", example = "true")
    private boolean adult;

    @Schema(description = "Name of the animal species. CAT or DOG", example = "CAT")
    private Animal.Type type;

    @Schema(description = "Id of the adoptive parent who has this animal", example = "1")
    private long adopterId;

    @Schema(description = "Id of the telegram-chat for communication with adoptive parent who has this animal", example = "1234567890")
    private String adopterChatId;

    @Schema(description = "Id name the telegram-chat for communication with adoptive parent who has this animal", example = "1234567890")
    private String adopterName;
}
