package com.evgeniyfedorchenko.animalshelter.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AnimalInputDto {

    @NotBlank(message = "Animal's name should not be blank")
    @Size(max = 30)
    private String name;

    @NotNull(message = "Animal's 'isAdult' should not be null")
    private boolean isAdult;

    @Positive(message = "Adopter's id must be positive")
    private long adopterId;

}
