package com.evgeniyfedorchenko.animalshelter.admin.annotations.documentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.links.Link;
import io.swagger.v3.oas.annotations.links.LinkParameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Operation(summary = "Assign the existing animal to the existing adopter",
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successful response of assign animal to adopter. This response has no body"
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Filed response of assign animal to adopter. Specify whether these objects exist and whether they are available for communication. Each adopter can have only one animal, and vice versa. Use the links to find out",
                        links = {
                                @Link(
                                        name = "Check for adopter",
                                        operationRef = "http://localhost:8080/adopters/{id}",
                                        parameters = @LinkParameter(
                                                name = "id",
                                                expression = "Id of target adopter")
                                ),
                                @Link(
                                        name = "Check for animal",
                                        operationRef = "http://localhost:8080/animals/{id}",
                                        parameters = @LinkParameter(
                                                name = "id",
                                                expression = "Id of the target animal"
                                        )
                                )
                        }
                )
        }
)
public @interface AssignAnimalToAdopterDocumentation {
}
