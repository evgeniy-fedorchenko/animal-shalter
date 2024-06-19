package com.evgeniyfedorchenko.animalshelter.backend.entities;


import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
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

    @Pattern(regexp = "^\\d{5,15}$", message = "Adopter's chatId must contain from 5 to 15 digits and be positive")
    @Column(unique = true)
    private String chatId;

    @NotBlank(message = "Adopter's name should not be blank")
    @Size(max = 50)
    private String name;

    @NotNull
    @Pattern(regexp = "^(\\+79|79|89)\\d{9}", message = "Adopter's phone number is invalid. Must be matches ^(\\+79|79|89)\\d{9}")
    private String phoneNumber;

    @Min(value = 30, message = "Adopter's quantity of assigned reports must be greater than 30")
    private int assignedReportsQuantity;

    @Nullable
    @OneToMany(mappedBy = "adopter", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Report> reports = new ArrayList<>();

    @Nullable
    @OneToOne
    @JoinColumn(name = "animal_id")
    private Animal animal;

    /**
     * Метод удаляет переданный в параметре объект из коллекции {@code this.reports}, а так же устанавливает
     * {@code null} в поле {@code Adopter adopter} у переданного экземпляра класса {@code Report}
     * Удаление происходит локально, необходимо обновление в базе данных
     * @param report объект, который нужно удалить из коллекции {@code this.reports}
     * @return объект {@code Adopter} с обновленной коллекцией <b>reports</b>, из которой локально удален
     * переданный в параметре объект {@code Report}. Если переданный объект не был найден в коллекции,
     * то коллекция не изменится
     */
    public Adopter removeStudent(Report report) {
        report.setAdopter(null);
        this.reports.remove(report);
        return this;
    }

    /**
     * Метод добавляет переданный в параметре объект в коллекцию {@code this.reports}, а так же устанавливает
     * объект {@code this} в поле {@code Adopter adopter} у переданного экземпляра {@code Report}
     * Добавление происходит локально, необходимо обновление в базе данных
     * @param report объект, который нужно добавить в коллекцию {@code this.reports}
     * @return объект {@code Adopter} с обновленной коллекцией <b>reports</b>, в которую локально добавлен
     * переданный в параметре объект {@code Report}
     */
    public Adopter addReport(Report report) {
        report.setAdopter(this);
        this.reports.add(report);
        return this;
    }

    public boolean hasAnimal() {
        return animal != null;
    }

    public boolean hasReports() {
        return reports != null && !reports.isEmpty();
    }

    @Override
    public String toString() {
        return "Adopter{id=%d, chatId=%s, name=%s, phoneNumber=%s, assignedReportsQuantity=%d, reportsSize=%d, animal=%s}"
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
