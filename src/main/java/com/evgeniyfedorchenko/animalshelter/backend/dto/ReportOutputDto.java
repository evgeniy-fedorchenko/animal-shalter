package com.evgeniyfedorchenko.animalshelter.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ReportOutputDto {

    private long id;
    private String diet;
    private String health;
    private String changeBehavior;
    private String photoUrl;
    private Instant sendingAt;

    private long adopterId;
    private String adopterName;

    private long animalId;
    private String animalName;

}
