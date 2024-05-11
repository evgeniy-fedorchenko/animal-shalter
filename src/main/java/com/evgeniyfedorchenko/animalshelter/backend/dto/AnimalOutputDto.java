package com.evgeniyfedorchenko.animalshelter.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AnimalOutputDto {

    private long id;
    private String name;
    private boolean isAdult;
    private long adopterId;
    private long adopterChatId;
    private String adopterName;

}
