package com.evgeniyfedorchenko.animalshelter.admin.annotations.documentation;

import com.evgeniyfedorchenko.animalshelter.backend.dto.AdopterOutputDto;
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
@Operation(summary = "Getting existing adopter by his id",
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successful response of requested adopter",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = AdopterOutputDto.class),
                                examples = @ExampleObject(
                                        name = "Adopter",
                                        description = "Example of returned object",
                                        value = "{\n\"id\": 1,\n\"chatId\": 123456789,\n\"name\": \"John Doe\",\n\"phoneNumber\": \"+79991234567\",\n\"assignedReportsQuantity\": 30,\n\"animalId\": 42,\n\"animalName\": \"Fluffy\"\n}"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "No adopters were found under the requested id. This response has no body",
                        content = @Content
                )
        }
)
public @interface GetAdopterByIdDocumentation {
}
