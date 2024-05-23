package com.evgeniyfedorchenko.animalshelter.backend.mappers;

import com.evgeniyfedorchenko.animalshelter.admin.controllers.ReportController;
import com.evgeniyfedorchenko.animalshelter.backend.dto.ReportOutputDto;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Report;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Component
public class ReportMapper {

    @Value("${server.port}")
    private int port;

    public ReportOutputDto toDto(Report report) {
        ReportOutputDto reportOutputDto = new ReportOutputDto();

        reportOutputDto.setId(report.getId());
        reportOutputDto.setDiet(report.getDiet());
        reportOutputDto.setHealth(report.getHealth());
        reportOutputDto.setChangeBehavior(report.getChangeBehavior());
        reportOutputDto.setSendingAt(report.getSendingAt());

        reportOutputDto.setPhotoUrl(
                report.hasPhoto()
                        ? generateUrl(String.valueOf(report.getId()))
                        : "no photo"
        );

        Adopter adopter = report.getAdopter();
        reportOutputDto.setAdopterId(adopter.getId());
        reportOutputDto.setAdopterName(adopter.getName());

        Optional.ofNullable(adopter.getAnimal())
                .ifPresent(animal -> {
                    reportOutputDto.setAnimalId(animal.getId());
                    reportOutputDto.setAnimalName(animal.getName());
                });

        return reportOutputDto;
    }

    private String generateUrl(String reportId) {
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(port)
                .path(ReportController.BASE_REPORT_URI)
                .pathSegment(String.valueOf(reportId), "photo")

                .build()
                .toUri()
                .toString();
    }
}
