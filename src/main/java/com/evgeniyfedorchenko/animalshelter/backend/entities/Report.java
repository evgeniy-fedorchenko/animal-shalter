package com.evgeniyfedorchenko.animalshelter.backend.entities;


import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private long id;

    @Nullable
    @Size(max = 500, message = "Report's diet length must be less than 500")
    private String diet;

    @Nullable
    @Size(max = 500, message = "Report's diet length must be less than 500")
    private String health;

    @Nullable
    @Size(max = 500, message = "Report's diet length must be less than 500")
    private String changeBehavior;

    @Lob
    @Column(columnDefinition = "oid")
    private byte[] photoData;

    @NotNull
    private String mediaType;

    @NotNull(message = "Report's field 'sendingAt' should not be null")
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant sendingAt;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean verified;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean accepted;

    @NotNull(message = "Report's field 'Adopter' should not be null")
    @ManyToOne
    @JoinColumn(name = "adopter_id")
    private Adopter adopter;

    public boolean hasPhoto() {
        return photoData != null;
    }

    @Override
    public String toString() {
        return "Report{id=%d, diet=%s, health=%s, changeBehavior=%s, hasPhotoData=%b, sendingAt=%s, isVerified=%s, isAccepted=%s, adopter=%s}"
                .formatted(
                        id,
                        diet != null ? diet : "no diet",
                        health != null ? health : "no health",
                        changeBehavior != null ? changeBehavior : "no changeBehavior",
                        photoData,   // при помощи '%b' выводим false если поле равно null и true в противном случае
                        DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").withZone(ZoneId.systemDefault()).format(sendingAt),
                        verified,
                        accepted,
                        adopter
                );
    }
}
