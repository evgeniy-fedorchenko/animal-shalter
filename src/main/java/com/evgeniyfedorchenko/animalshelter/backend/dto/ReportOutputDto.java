package com.evgeniyfedorchenko.animalshelter.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Представление объекта Report, сохраненного в базу данных, а так же основных полей зависимых сущностей, если таковые имеются")
public class ReportOutputDto {

    @Schema(description = "Id of this report", example = "1")
    private long id;

    @Schema(description = "Description of the diet that animal adheres to", example = "Homemade diet (e.g. cooked meat, vegetables, rice)")
    private String diet;

    @Schema(description = "Description of the health status that animal adheres to", example = "Presence of chronic conditions (e.g. diabetes, kidney disease, arthritis)")
    private String health;

    @Schema(description = "Description of the behavioral changes that animal adheres to", example = "Changes in appetite (increased or decreased)")
    private String changeBehavior;

    @Schema(description = "Link to the photo of animal", example = "https://myserver.com/reports/1/photo")
    private String photoUrl;

    @Schema(description = "The timestamp of the start of sending the report", example = "2024-06-17T22:02:49.550+05:00")
    private Instant sendingAt;

    @Schema(description = "Id of the adoptive parent who has sent this report", example = "1")
    private long adopterId;

    @Schema(description = "Id name the telegram-chat for communication with adoptive parent who has sent this report", example = "1234567890")
    private String adopterName;

    @Schema(description = "The id of the animal to which this report refers", example = "1")
    private long animalId;

    @Schema(description = "The name of the animal to which this report refers", example = "1")
    private String animalName;

}
