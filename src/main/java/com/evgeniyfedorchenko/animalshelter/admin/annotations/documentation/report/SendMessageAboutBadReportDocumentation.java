package com.evgeniyfedorchenko.animalshelter.admin.annotations.documentation.report;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.links.Link;
import io.swagger.v3.oas.annotations.links.LinkParameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Operation(summary = "Send message to the adopter in telegram a warning about a low-quality report",
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "The requested message was successfully sent. This response has no body"
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "The message could not be sent, please check if there is a report with the specified number. Use the links to find out",
                        links = {
                                @Link(
                                        name = "Check for report",
                                        operationRef = "http://localhost:8080/reports/{id}",
                                        parameters = @LinkParameter(
                                                name = "id",
                                                expression = "Id of target report")

                                )
                        }
                )
        }
)
public @interface SendMessageAboutBadReportDocumentation {
}
