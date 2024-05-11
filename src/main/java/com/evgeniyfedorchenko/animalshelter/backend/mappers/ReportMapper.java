package com.evgeniyfedorchenko.animalshelter.backend.mappers;

import com.evgeniyfedorchenko.animalshelter.backend.dto.ReportOutputDto;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Report;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ReportMapper {

    public ReportOutputDto toDto(Report report) {
        ReportOutputDto reportOutputDto = new ReportOutputDto();

        reportOutputDto.setId(report.getId());
        reportOutputDto.setDiet(report.getDiet());
        reportOutputDto.setHealth(report.getHealth());
        reportOutputDto.setChangeBehavior(report.getChangeBehavior());
        reportOutputDto.setPhotoUrl(generateUrl(report));
        reportOutputDto.setSendingAt(report.getSendingAt());

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

    private String generateUrl(Report report) {
//        todo реализовать построение урла
        return null;
//        return UriComponentsBuilder.newInstance()
//                .scheme("http")
//                .host("localhost")
//                .port(port)
//                .path(StudentController.BASE_STUDENTS_URI)
//                .pathSegment(String.valueOf(studentId), "avatar")
//                .queryParam("large", queryParamValue)
//
//                .build()
//                .toUri()
//                .toString();
    }
}
