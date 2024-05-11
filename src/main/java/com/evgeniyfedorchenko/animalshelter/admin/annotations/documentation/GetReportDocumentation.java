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
@Operation(summary = "Getting existing report by his id",
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successful response of requested report",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = ReportOutputDto.class),
                                examples = @ExampleObject(
                                        name = "Report",
                                        description = "Example of returned object",
                                        // todo Вставить нормальный урл в пример
                                        value = "{\"id\":1,\"diet\":\"Omnivore\",\"health\":\"Healthy\",\"changeBehavior\":\"Playful\",\"photoUrl\":\"https://example.com/animal-photo.jpg\",\"sendingAt\":\"2023-04-18T12:34:56Z\",\"adopterId\":123,\"adopterName\":\"John Doe\",\"animalId\":456,\"animalName\":\"Fluffy\"}"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "No report were found under the requested id. This response has no body",
                        content = @Content
                )
        }
)
public @interface GetReportDocumentation {
}
