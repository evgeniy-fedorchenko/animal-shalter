package com.evgeniyfedorchenko.animalshelter.admin.annotations.documentation.animal;

import com.evgeniyfedorchenko.animalshelter.backend.dto.AnimalOutputDto;
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
@Target(ElementType.METHOD)
@Operation(summary = "Flexible search for existing animals",
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successful response of searching of animals",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = AnimalOutputDto[].class),
                                examples = {
                                        @ExampleObject(
                                                name = "Collection of adopters",
                                                description = "Example of returned object",
                                                value = "[{\"id\":1,\"name\":\"Fluffy\",\"isAdult\":true,\"adopterId\":123,\"adopterChatId\":456,\"adopterName\":\"John Doe\"}," +
                                                        "{\"id\":2,\"name\":\"Whiskers\",\"isAdult\":false,\"adopterId\":789,\"adopterChatId\":321,\"adopterName\":\"Jane Smith\"}]"
                                        ),
                                        @ExampleObject(
                                                name = "Empty collection",
                                                description = "Returned if search with such parameters didn't turn up anything",
                                                value = "{}"
                                        )
                                }
                        )
                )
        }
)
public @interface SearchAnimalsDocumentation {
}
