package com.evgeniyfedorchenko.animalshelter.backend.dto;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class AnimalOutputDto {

    private long id;
    private String name;
    private boolean isAdult;
    private long adopterId;
    private long adopterChatId;
    private String adopterName;

}
