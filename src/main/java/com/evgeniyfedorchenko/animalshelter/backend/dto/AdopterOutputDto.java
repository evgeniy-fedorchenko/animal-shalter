package com.evgeniyfedorchenko.animalshelter.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Представление объекта Adopter, сохраненного в базу данных, а так же основных полей зависимых сущностей, если таковые имеются")
public class AdopterOutputDto {

    @Schema(description = "Id of this adoptive parent", example = "1")
    private long id;

    @Schema(description = "Id of the telegram-chat for communication with this adoptive parent", example = "1234567890")
    private String chatId;

    @Schema(description = "The name of this adoptive parent", example = "Will Smith")
    private String name;

    @Schema(description = "The phone number of this adoptive parent", example = "79123456789")
    private String phoneNumber;

    @Schema(description = "The number of reports assigned to this adoptive parent", example = "30")
    private int assignedReportsQuantity;

    @Schema(description = "The number of reports that this adoptive parent has sent", example = "30")
    private int reportsQuantity;

    @Schema(description = "Id of the animal that is being adopted by this adoptive parent", example = "1")
    private long animalId;

    @Schema(description = "Id of the animal that is being adopted by this adoptive parent", example = "Fluffy")
    private String animalName;

}
