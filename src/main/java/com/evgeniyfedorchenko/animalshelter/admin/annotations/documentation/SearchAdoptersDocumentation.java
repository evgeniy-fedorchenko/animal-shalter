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
@Target(ElementType.METHOD)
@Operation(summary = "Flexible search for existing adopters",
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successful response of searching of adopters",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = AdopterOutputDto[].class),
                                examples = {
                                        @ExampleObject(
                                                name = "Collection of adopters",
                                                description = "Example of returned object",
                                                value = "[\n{\n\"id\": 1,\n\"chatId\": 123456789,\n\"name\": \"John Doe\",\n\"phoneNumber\": \"79991234567\",\n\"assignedReportsQuantity\": 44,\n\"animalId\": 23,\n\"animalName\": \"Mikey\"\n},\n" +
                                                        "{\n\"id\": 12,\n\"chatId\": 987654321,\n\"name\": \"Mark Tishman\",\n\"phoneNumber\": \"79997654321\",\n\"assignedReportsQuantity\": 30,\n\"animalId\": 42,\n\"animalName\": \"Fluffy\"\n}\n]"
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
public @interface SearchAdoptersDocumentation {
}
