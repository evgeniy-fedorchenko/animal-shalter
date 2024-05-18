package com.evgeniyfedorchenko.animalshelter.backend.services;

import com.evgeniyfedorchenko.animalshelter.admin.controllers.SortOrder;
import com.evgeniyfedorchenko.animalshelter.backend.dto.AdopterInputDto;
import com.evgeniyfedorchenko.animalshelter.backend.dto.AdopterOutputDto;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Animal;
import com.evgeniyfedorchenko.animalshelter.backend.mappers.AdopterMapper;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.AdopterRepository;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.AdopterRepositoryHelper;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.AnimalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdopterServiceImpl implements AdopterService {

    private final AdopterRepository adopterRepository;
    private final AnimalRepository animalRepository;
    private final AdopterMapper adopterMapper;
    private final AdopterRepositoryHelper adopterRepositoryHelper;
    private final int initialAssignedReportsQuantity = 30;

    @Override
    public Optional<AdopterOutputDto> addAdopter(AdopterInputDto adopterInputDto) {

        Adopter adopter = new Adopter();

        adopter.setChatId(adopterInputDto.getChatId());
        adopter.setName(adopterInputDto.getName());
        adopter.setAssignedReportsQuantity(initialAssignedReportsQuantity);
        adopter.setPhoneNumber(validatePhoneNumber(adopterInputDto.getPhoneNumber()));

        Optional<Animal> animalOpt = Optional.empty();
        if (adopterInputDto.getAnimalId() != null) {
            animalOpt = animalRepository.findById(adopterInputDto.getAnimalId());
            animalOpt.ifPresent(adopter::setAnimal);
        }

        Adopter savedAdopter = adopterRepository.save(adopter);
        log.info("Saved adopter: {}", savedAdopter);

        animalOpt.ifPresent(animal -> animal.setAdopter(adopter));
        return Optional.ofNullable(adopterMapper.toOutputDto(savedAdopter));
    }

    @Override
    public Optional<AdopterOutputDto> getAdopter(long id) {
        return adopterRepository.findById(id).map(adopterMapper::toOutputDto);
    }

    @Override
    public List<AdopterOutputDto> searchAdopters(String sortParam, SortOrder sortOrder, int pageSize, int pageNumber) {

        int offset = (pageNumber - 1) * pageSize;
        List<Adopter> adopters = adopterRepositoryHelper.searchAdopters(sortParam, sortOrder, pageSize, offset);

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
        adopterRepository.deleteById(id);
        return true;
    }

    private String validatePhoneNumber(String phoneNumber) {

//        Regexp: Заменяем на пустую строку все пробельные символы, круглые скобки и знак минус
        String replaced = phoneNumber.replaceAll("[\\s()\\-]+", "");

          /* Возможны только три случая - "+79...", "79..." и "89...", все остальное не пройдет
          валидацию в контроллере. В любом случае приводим номер к виду "79..." */
        return replaced.startsWith("+79")
                ? replaced.replaceFirst("^..", "7")
                : replaced.replaceFirst("^.", "7");
    }
}

