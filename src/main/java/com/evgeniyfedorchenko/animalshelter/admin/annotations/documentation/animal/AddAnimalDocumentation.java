package com.evgeniyfedorchenko.animalshelter.admin.annotations.documentation.animal;


import com.evgeniyfedorchenko.animalshelter.backend.dto.AnimalInputDto;
import com.evgeniyfedorchenko.animalshelter.backend.dto.AnimalOutputDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Operation(summary = "Adding a new animal",
        requestBody = @RequestBody(
                description = "Schema of a new animal",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = AnimalInputDto.class),
                        examples = @ExampleObject(
                                name = "Animal",
                                description = "Example of adding object",
                                value =  "{\"name\":\"Fluffy\",\"isAdult\":true,\"adopterId\":123}"
                        )
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successful response of addition of a new adopter",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = AnimalOutputDto.class),
                                examples = @ExampleObject(
                                        name = "Animal",
                                        description = "Example of returned object",
                                        value = "{\"id\":1,\"name\":\"Fluffy\",\"isAdult\":true,\"adopterId\":123,\"adopterChatId\":456,\"adopterName\":\"John Doe\"}"
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Request is bad",
                        content = @Content(
                                mediaType = MediaType.TEXT_PLAIN_VALUE,
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(
                                        name = "Errors description",
                                        description = "Description of the errors made",
                                        value = "Validation errors:\n1. Parameter name. Cause: error's cause\n2. Other parameter name. Cause: error's cause\n..."
                                )
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Filed response. Parameters are correct, but requested adopter's id not found. This response has no body",
                        content = @Content
                ),
                @ApiResponse(
                        responseCode = "409",
                        description = "Filed response. An attempt to violate database's data integrity",
                        content = @Content(
                                mediaType = MediaType.TEXT_PLAIN_VALUE,
                                schema = @Schema(implementation = String.class),
                                examples = @ExampleObject(
                                        name = "Errors description",
                                        description = "Description of the errors made",
                                        value = "Violations of data integrity:\n1. Cause of first error\n2. Cause of second error\n..."
                                )
                        )
                )
        }
)
public @interface AddAnimalDocumentation {
}
