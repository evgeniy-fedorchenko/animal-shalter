package com.evgeniyfedorchenko.animalshelter.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdopterOutputDto {

    private long id;
    private long chatId;
    private String name;
    private String phoneNumber;
    private int assignedReportsQuantity;
    private long animalId;
    private String animalName;

}
