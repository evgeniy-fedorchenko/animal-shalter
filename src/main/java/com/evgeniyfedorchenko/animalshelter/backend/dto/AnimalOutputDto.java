package com.evgeniyfedorchenko.animalshelter.backend.dto;

import com.evgeniyfedorchenko.animalshelter.backend.entities.Animal;
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
    private Animal.Type type;
    private long adopterId;
    private String adopterChatId;
    private String adopterName;

}
