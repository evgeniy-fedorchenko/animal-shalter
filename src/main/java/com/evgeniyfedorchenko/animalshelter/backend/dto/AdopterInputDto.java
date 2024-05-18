package com.evgeniyfedorchenko.animalshelter.backend.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AdopterInputDto {

    @Positive(message = "Adopter's chatId must be positive")
    private long chatId;

    @NotBlank(message = "Adopter's name should not be blank")
    @Size(max = 50)
    private String name;

    @Pattern(regexp = "^(\\+79|79|89)\\d{9}",
            message = "Adopter's phone number is invalid. Must be starts with '+79' or '79' or '89', and contain 11 digits")
    private String phoneNumber;

    @Nullable
    @Positive(message = "Animal's id must be greater than zero")
    private Long animalId;

}
