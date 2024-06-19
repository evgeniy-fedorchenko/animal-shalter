package com.evgeniyfedorchenko.animalshelter.backend.dto;

import com.evgeniyfedorchenko.animalshelter.backend.entities.Animal;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Представление объекта Animal, для сохранения в базу данных")
public class AnimalInputDto {

    @Schema(description = "The name of this animal", example = "Flatty")
    @NotBlank(message = "Animal's name should not be blank")
    @Size(max = 30)
    private String name;

    @Schema(description = "The  boolean-pointer to the adulthood of the animal", example = "true")
    @NotNull(message = "Animal's 'isAdult' should not be null")
    private boolean adult;

    @Schema(description = "Name of the animal species. CAT or DOG", example = "CAT")
    @NotNull(message = "Any animal must have a type")
    private Animal.Type type;
}
