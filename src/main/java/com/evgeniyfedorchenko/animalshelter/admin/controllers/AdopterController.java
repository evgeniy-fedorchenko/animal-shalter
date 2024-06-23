package com.evgeniyfedorchenko.animalshelter.admin.controllers;

import com.evgeniyfedorchenko.animalshelter.admin.annotations.IsFieldOf;
import com.evgeniyfedorchenko.animalshelter.admin.annotations.documentation.adopter.AddAdopterDocumentation;
import com.evgeniyfedorchenko.animalshelter.admin.annotations.documentation.adopter.DeleteAdopterDocumentation;
import com.evgeniyfedorchenko.animalshelter.admin.annotations.documentation.adopter.GetAdopterByIdDocumentation;
import com.evgeniyfedorchenko.animalshelter.admin.annotations.documentation.adopter.SearchAdoptersDocumentation;
import com.evgeniyfedorchenko.animalshelter.backend.dto.AdopterInputDto;
import com.evgeniyfedorchenko.animalshelter.backend.dto.AdopterOutputDto;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import com.evgeniyfedorchenko.animalshelter.backend.services.AdopterService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Adopters", description = "Controller for work with adopters: adding, searching and deleting")
@Validated
@RestController
@RequestMapping(path = "/adopters")
@AllArgsConstructor
public class AdopterController {

    private final AdopterService adopterService;

    @AddAdopterDocumentation
    @PostMapping
    public ResponseEntity<AdopterOutputDto> addAdopter(@Valid @RequestBody AdopterInputDto adopterInputDto) {
        return ResponseEntity.of(adopterService.addAdopter(adopterInputDto));
    }

    @GetAdopterByIdDocumentation
    @GetMapping(path = "/{id}")
    public ResponseEntity<AdopterOutputDto> getAdopterById(
            @Positive(message = "Adopter's id must be positive")
            @Parameter(description = "Id of the requested adopter", example = "1")
            @PathVariable long id) {

        return ResponseEntity.of(adopterService.getAdopter(id));
    }

    @SearchAdoptersDocumentation
    @GetMapping
    public List<AdopterOutputDto> searchAdopters(
            @IsFieldOf(Adopter.class)
            @Parameter(description = "Name of the field to be searched for")
            @RequestParam(required = false, defaultValue = "id") String sortParam,

            @Parameter(description = "The sorting order of the found adopters")
            @RequestParam(required = false, defaultValue = "ASC") SortOrder sortOrder,

            @Parameter(description = "The page number of the found adopters. It works in conjunction with the page size")
            @Positive(message = "Number of page must be positive")
            @RequestParam(required = false, defaultValue = "1") int pageNumber,

            @Parameter(description = "The page size of the found adopters. It works in conjunction with the page number")
            @Positive(message = "Size of page must be positive")
            @RequestParam(required = false, defaultValue = "2147483647") int pageSize
    ) {
        return adopterService.searchAdopters(sortParam, sortOrder, pageSize, pageNumber);
    }

    @DeleteAdopterDocumentation
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteAdopter(
            @Parameter(description = "Id of the adopter for delete", example = "1")
            @Positive(message = "Adopter's id must be positive") @PathVariable long id) {

        return adopterService.deleteAdopter(id)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }
}
