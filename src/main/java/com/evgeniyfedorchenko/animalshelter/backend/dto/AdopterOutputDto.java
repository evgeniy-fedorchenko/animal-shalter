package com.evgeniyfedorchenko.animalshelter.backend.dto;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class AdopterOutputDto {

    private long id;
    private String chatId;
    private String name;
    private String phoneNumber;
    private int assignedReportsQuantity;
    private int reportsQuantity;
    private long animalId;
    private String animalName;

}
