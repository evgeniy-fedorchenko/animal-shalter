package com.evgeniyfedorchenko.animalshelter.admin.annotations.documentation.report;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.links.Link;
import io.swagger.v3.oas.annotations.links.LinkParameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Operation(summary = "View a photo of an existing report",
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successful response of the requested photo",
                        headers = {
                                @Header(
                                        name = "Content-Type",
                                        description = "Type og the  returned content",
                                        schema = @Schema(type = "string", example = "image/png")
                                ),
                                @Header(
                                        name = "Content-Length",
                                        description = "Length or the returned content",
                                        schema = @Schema(type = "integer", format = "int64"
                                        )
                                )
                        },
                        content = @Content(
                                mediaType = MediaType.IMAGE_PNG_VALUE,
                                schema = @Schema(
                                        type = "string",
                                        format = "binary"
                                )

                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Please check if there is a report with this id",
                        content = @Content,
                        links = @Link(
                                name = "Check for report",
                                operationRef = "http://localhost:8080/reports/{id}",
                                parameters = @LinkParameter(
                                        name = "id",
                                        expression = "Id of target adopter")
                        )
                )
        }
)
public @interface GetPhotoDocumentation {
}
