package com.evgeniyfedorchenko.animalshelter.admin.annotations.documentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Operation(summary = "Delete an existing adopter",
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Requested adopter was successful deleted. This response has no body"
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Filed deleting of requested adopter, because its not found. This response has no body"
                )
        }
)
public @interface DeleteAdopterDocumentation {
}
