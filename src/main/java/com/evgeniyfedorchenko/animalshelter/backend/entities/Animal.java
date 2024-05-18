package com.evgeniyfedorchenko.animalshelter.backend.entities;


import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "animals")
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private long id;

    @NotBlank(message = "Animal's name should not be blank")
    @Size(max = 30)
    private String name;

    @NotNull(message = "Animal's 'isAdult' should not be null")
    private boolean adult;

    @Nullable
    @OneToOne(mappedBy = "animal")
    private Adopter adopter;

    public boolean hasAdopter() {
        return adopter != null;
    }

    @Override
    public String toString() {
        return "Animal{id=%d, name=%s, isAdult=%s, adopterId=%s}".formatted(
                id,
                name,
                adult,
                this.hasAdopter() ? adopter.getId() : "no adopter"
        );
    }
}
