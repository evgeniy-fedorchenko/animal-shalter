package com.evgeniyfedorchenko.animalshelter.admin.annotations.documentation;


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
@Target({ElementType.METHOD})
@Operation(summary = "Getting existing animal by his id",
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successful response of requested animal",
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
                        responseCode = "404",
                        description = "No animal were found under the requested id. This response has no body",
                        content = @Content
                )
        }
)
public @interface GetAnimalByIdDocumentation {
}
