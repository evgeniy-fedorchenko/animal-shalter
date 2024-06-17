package com.evgeniyfedorchenko.animalshelter.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Сущность, представляющая волонтера для общения с пользователями телеграм бота, когда те
 * зовут волонтеров на помощь
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Entity
@Table(name = "volunteers")
public class Volunteer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private long id;

    @NotNull(message = "Volunteer's name should not be null")
    @Size(max = 30, message = "Volunteer's name must be shorter than 30 symbols")
    private String name;

    @Positive(message = "Volunteer's chatId must be positive")
    @Column(unique = true)
    private String chatId;

    @NotNull(message = "Volunteer's field 'free' should not be null")
    private boolean free;
}
