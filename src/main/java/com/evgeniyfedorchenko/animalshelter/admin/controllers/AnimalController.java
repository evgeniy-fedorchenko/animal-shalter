package com.evgeniyfedorchenko.animalshelter.admin.controllers;

import com.evgeniyfedorchenko.animalshelter.admin.annotations.IsFieldOf;
import com.evgeniyfedorchenko.animalshelter.admin.annotations.documentation.animal.*;
import com.evgeniyfedorchenko.animalshelter.backend.dto.AnimalInputDto;
import com.evgeniyfedorchenko.animalshelter.backend.dto.AnimalOutputDto;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Animal;
import com.evgeniyfedorchenko.animalshelter.backend.services.AnimalService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletionException;

@Tag(name = "Animals", description = "Controller for work with animals: adding, search, assigned to adopters and deleting ")
@Validated
@RestController
@RequestMapping(path = "/animals")
@AllArgsConstructor
public class AnimalController {

    private final AnimalService animalService;

    @AddAnimalDocumentation
    @PostMapping
    public ResponseEntity<AnimalOutputDto> addAnimal(@Valid @RequestBody AnimalInputDto animal) {
        return ResponseEntity.of(animalService.addAnimal(animal));
    }

    @GetAnimalByIdDocumentation
    @GetMapping(path = "/{id}")
    public ResponseEntity<AnimalOutputDto> getAnimalById(
            @Positive(message = "Animal's id must be positive")
            @Parameter(description = "Id of the requested animal", example = "1")
            @PathVariable long id) {

        return ResponseEntity.of(animalService.getAnimal(id));

    }

    @SearchAnimalsDocumentation
    @GetMapping
    public List<AnimalOutputDto> searchAnimals(
            @IsFieldOf(Animal.class)
            @Parameter(description = "Name of the field to be searched for")
            @RequestParam(required = false, defaultValue = "id") String sortParam,

            @Parameter(description = "The sorting order of the found adopters")
            @RequestParam(required = false, defaultValue = "ASC") SortOrder sortOrder,

            @Parameter(description = "The page number of the found adopters. It works in conjunction with the page size")
            @Positive(message = "Number of page must be positive")
            @RequestParam(required = false, defaultValue = "1") int pageNumber,

            @Parameter(description = "The page size of the found adopters. It works in conjunction with the page number")
            @Positive(message = "Size of page must be positive")
            @RequestParam(required = false, defaultValue = "1") int pageSize
    ) {
        return animalService.searchAnimals(sortParam, sortOrder, pageNumber, pageSize);
    }


    @AssignAnimalToAdopterDocumentation
    @PatchMapping
    public ResponseEntity<Void> assignAnimalToAdopter(
            @Parameter(description = "Existing animal's id that should be assigned to the specified adopter", example = "1")
            @Positive(message = "Animal's id must be positive")
            @RequestParam long animalId,

            @Parameter(description = "Existing adopter's id that should be assigned to the specified animal", example = "1")
            @Positive(message = "Adopter's id must be positive")
            @RequestParam long adopterId) {
        /* Если сущности не найдены - это 404 notFound
           Если найдены, но заняты - 400 badRequest
           Иначе - 200 ok */
        try {
//            todo Возможно стоит возвращать просто ComplFuture и нафиг эту возню убрать убрать
            return animalService.assignAnimalToAdopter(animalId, adopterId).join()
                    ? ResponseEntity.ok().build()
                    : ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException _) {
            return ResponseEntity.notFound().build();
        } catch (CompletionException ex) {
            if (ex.getCause() instanceof EntityNotFoundException) {
                return ResponseEntity.notFound().build();
            }
        }
//        fixme
        throw new RuntimeException();
    }

    @DeleteAnimalDocumentation
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteAnimal(@Parameter(description = "Id of the animal for delete", example = "1")
                                             @Positive(message = "Animal's id must be positive")
                                             @PathVariable long id) {
        return animalService.deleteAnimalById(id)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }
}
