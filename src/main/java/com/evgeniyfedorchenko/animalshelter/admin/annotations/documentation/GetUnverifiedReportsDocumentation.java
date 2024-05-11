package com.evgeniyfedorchenko.animalshelter.admin.annotations.documentation;

import com.evgeniyfedorchenko.animalshelter.backend.dto.ReportOutputDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Operation(summary = "Get reports that have not yet been verified by volunteers",
        responses = @ApiResponse(
                responseCode = "200",
                description = "Schema of a returned array of objects", // todo проверить это поле везде, там может быть collection
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = ReportOutputDto[].class),
                        examples = {
                                @ExampleObject(
                                        name = "Unverified reports",
                                        description = "Example of array of objects. If more reports are requested than are available, all of them will be returned",
//                                todo Сюда тоже урлы нормальные вставить в экзамплы. Бляя, еще надо ендпинт для получения картинок же
                                        value = "[{\"id\":1,\"diet\":\"Omnivore\",\"health\":\"Healthy\",\"changeBehavior\":\"Playful\",\"photoUrl\":\"https://example.com/animal-photo.jpg\",\"sendingAt\":\"2023-04-18T12:34:56Z\",\"adopterId\":123,\"adopterName\":\"John Doe\",\"animalId\":456,\"animalName\":\"Fluffy\"}," +
                                                "{\"id\":2,\"diet\":\"Carnivore\",\"health\":\"Sick\",\"changeBehavior\":\"Aggressive\",\"photoUrl\":\"https://example.com/another-animal-photo.jpg\",\"sendingAt\":\"2023-04-19T09:45:12Z\",\"adopterId\":789,\"adopterName\":\"Jane Smith\",\"animalId\":789,\"animalName\":\"Whiskers\"}]"
                                ),
                                @ExampleObject(
                                        name = "Empty array",
                                        description = "Returned if the reports are verified",
                                        value = "{}"
                                )
                        }
                )
        )
)
public @interface GetUnverifiedReportsDocumentation {
}
