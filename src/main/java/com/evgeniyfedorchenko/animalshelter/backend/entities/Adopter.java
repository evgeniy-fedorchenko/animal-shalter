package com.evgeniyfedorchenko.animalshelter.backend.entities;


import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


/**
 * Класс представляет сущность гостевого пользователя приложения (усыновителя)
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "adopters")
public class Adopter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private long id;

    @Positive(message = "Adopter's chatId must be positive")
    @Column(unique = true)
    private long chatId;

    @NotBlank(message = "Adopter's name should not be blank")
    @Size(max = 50)
    private String name;

    @Pattern(regexp = "^(\\+79|79|89)\\d{9}", message = "Adopter's phone number is invalid. Must be matches ^(\\+79|79|89)\\d{9}")
    private String phoneNumber;

    @Size(min = 30, message = "Adopter's quantity of assigned reports must be greater than 30")
    private int assignedReportsQuantity;

    @Nullable
    @OneToMany(mappedBy = "adopter", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Report> reports;

    @Nullable
    @OneToOne
    @JoinColumn(name = "animal_id")
    private Animal animal;

    public boolean hasAnimal() {
        return animal != null;
    }

    public boolean hasReports() {
        return reports != null && !reports.isEmpty();
    }

    @Override
    public String toString() {
        return "Adopter{id=%d, chatId=%d, name=%s, phoneNumber=%s, assignedReportsQuantity=%d, reportsSize=%d, animal=%s}"
                .formatted(
                        id,
                        chatId,
                        name,
                        phoneNumber,
                        assignedReportsQuantity,
                        this.hasReports() ? reports.size() : 0,
                        this.hasAnimal() ? animal : "no animal");
    }
}
