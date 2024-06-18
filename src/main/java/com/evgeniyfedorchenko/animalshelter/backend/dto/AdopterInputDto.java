package com.evgeniyfedorchenko.animalshelter.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Представление объекта Adopter, для сохранения в базу данных и привязки к объекту Animal (опционально)")
public class AdopterInputDto {

    @Schema(description = "Id of the telegram-chat for communication with this adoptive parent", example = "1234567890")
    @Positive(message = "Adopter's chatId must be positive")
    private String chatId;

    @Schema(description = "The name of this adoptive parent", example = "Will Smith")
    @NotBlank(message = "Adopter's name should not be blank")
    @Size(max = 50)
    private String name;

    @Schema(description = "The phone number of this adoptive parent", example = "79123456789")
    @Pattern(regexp = "^(\\+79|79|89)\\d{9}",
            message = "Adopter's phone number is invalid. Must be starts with '+79' or '79' or '89', and contain 11 digits")
    private String phoneNumber;

    @Schema(description = "Id of the animal that is being adopted by this adoptive parent", example = "1")
    @Nullable
    @Positive(message = "Animal's id must be greater than zero")
    private Long animalId;

}
