package com.evgeniyfedorchenko.animalshelter.backend.dto;

import com.evgeniyfedorchenko.animalshelter.backend.entities.Animal;
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
public class AnimalInputDto {

    @NotBlank(message = "Animal's name should not be blank")
    @Size(max = 30)
    private String name;

    @NotNull(message = "Animal's 'isAdult' should not be null")
    private boolean adult;

    @NotNull(message = "Any animal must have a type")
    private Animal.Type type;

}
