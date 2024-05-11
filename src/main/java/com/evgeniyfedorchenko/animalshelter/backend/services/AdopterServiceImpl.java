package com.evgeniyfedorchenko.animalshelter.backend.services;

import com.evgeniyfedorchenko.animalshelter.admin.controllers.SortOrder;
import com.evgeniyfedorchenko.animalshelter.backend.dto.AdopterInputDto;
import com.evgeniyfedorchenko.animalshelter.backend.dto.AdopterOutputDto;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Report;
import com.evgeniyfedorchenko.animalshelter.backend.mappers.AdopterMapper;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.AdopterRepository;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.AnimalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdopterServiceImpl implements AdopterService {

    private final AdopterRepository adopterRepository;
    private final AnimalRepository animalRepository;
    private final ReportService reportService;
    private final AdopterMapper adopterMapper;
    private final int initialAssignedReportsQuantity = 30;

    @Override
    public Optional<AdopterOutputDto> addAdopter(AdopterInputDto adopterInputDto) {

        Adopter adopter = new Adopter();

        adopter.setChatId(adopterInputDto.getChatId());
        adopter.setName(adopterInputDto.getName());
        adopter.setAssignedReportsQuantity(initialAssignedReportsQuantity);

        try {
            adopter.setPhoneNumber(validatePhoneNumber(adopterInputDto.getPhoneNumber()));
            adopter.setAnimal(animalRepository.findById(adopterInputDto.getAnimalId()).orElseThrow());
        } catch (IllegalArgumentException | NoSuchElementException ex) {
            log.error(ex.getMessage());
            return Optional.empty();
        }

        Adopter savedAdopter = adopterRepository.save(adopter);
        log.info("Saved adopter: {}", savedAdopter);
        return Optional.ofNullable(adopterMapper.toOutputDto(savedAdopter));
    }

    @Override
    public Optional<AdopterOutputDto> getAdopter(long id) {
        return adopterRepository.findById(id).map(adopterMapper::toOutputDto);
    }

    @Override
    public List<AdopterOutputDto> searchAdopters(String sortParam, SortOrder sortOrder, int pageSize, int pageNumber) {

        int offset = (pageNumber - 1) * pageSize;
        List<Adopter> adopters = sortOrder == SortOrder.ASC
                ? adopterRepository.searchAdoptersAscSort(sortParam, pageSize, offset)
                : adopterRepository.searchAdoptersDescSort(sortParam, pageSize, offset);

        log.debug("Calling searchAdopters with params: sortParam={}, sortOrder={}, pageNumber={}, pageSize={} returned student's ids: {}",
                sortParam, sortOrder, pageNumber, pageSize, adopters.stream().map(Adopter::getId).toList());

        return adopters.stream().map(adopterMapper::toOutputDto).toList();
    }

    @Override
    public boolean deleteAdopter(long id) {
        Optional<Adopter> adopterOpt = adopterRepository.findById(id);
        if (adopterOpt.isEmpty()) {
            log.warn("No adopter found with id: {}", id);
            return false;
        }

        Adopter adopter = adopterOpt.get();
        if (adopter.hasReports()) {
            List<Long> ids = adopter.getReports().stream().map(Report::getId).toList();
            reportService.deleteReports(ids);
        }
        adopterRepository.deleteById(id);
        return true;
    }

    private String validatePhoneNumber(String phoneNumber) {

        String replaced = phoneNumber.replaceAll("[\\s()\\-]+", "");

        if (replaced.startsWith("+79")) {
            return replaced.replaceFirst("^..", "7");

        } else if (replaced.startsWith("89") || replaced.startsWith("79")) {
            return replaced.replaceFirst("^.", "7");

        } else {
            throw new IllegalArgumentException("Invalid phone number: " + phoneNumber);
        }
    }
}
