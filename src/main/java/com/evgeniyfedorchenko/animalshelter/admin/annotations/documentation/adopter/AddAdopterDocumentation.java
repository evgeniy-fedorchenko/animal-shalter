package com.evgeniyfedorchenko.animalshelter.admin.annotations.documentation.adopter;

import com.evgeniyfedorchenko.animalshelter.backend.dto.AdopterInputDto;
import com.evgeniyfedorchenko.animalshelter.backend.dto.AdopterOutputDto;
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
@Operation(summary = "Adding a new adopter",
        requestBody = @RequestBody(
                description = "Schema of a new adopter",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = AdopterInputDto.class),
                        examples = {
                                @ExampleObject(
                                        name = "Adopter with animal",
                                        description = "Example of adding object",
                                        value =  "{\n\"chatId\": 123456789,\n\"name\": \"John Doe\",\n\"phoneNumber\": \"+79991234567\",\n\"animalId\": 42\n}"
                                ),
                                @ExampleObject(
                                        name = "Adopter without animal",
                                        description = "Example of adding object",
                                        value =  "{\n\"chatId\": 123456789,\n\"name\": \"John Doe\",\n\"phoneNumber\": \"+79991234567\"}"
                                )

                        }
                )
        ),
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successful response of addition of a new adopter",
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
                        description = "<b>Filed response</b>. Parameters are correct, but requested animal's id not found. This response has no body",
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
public @interface AddAdopterDocumentation {
}
