package com.evgeniyfedorchenko.animalshelter.backend.services;

import com.evgeniyfedorchenko.animalshelter.admin.controllers.SortOrder;
import com.evgeniyfedorchenko.animalshelter.backend.dto.AdopterInputDto;
import com.evgeniyfedorchenko.animalshelter.backend.dto.AdopterOutputDto;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Animal;
import com.evgeniyfedorchenko.animalshelter.backend.mappers.AdopterMapper;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.AdopterRepository;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.AnimalRepository;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.RepositoryUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdopterServiceImpl implements AdopterService {

    private final AnimalRepository animalRepository;
    private final AdopterMapper adopterMapper;
    private final AdopterRepository adopterRepository;
    private final int initialAssignedReportsQuantity = 30;
    private final RepositoryUtils repositoryUtils;

    @Override
    @Transactional
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
        log.debug("Saved adopter: {}", savedAdopter);

        animalOpt.ifPresent(animal -> animal.setAdopter(adopter));
        return Optional.ofNullable(adopterMapper.toOutputDto(savedAdopter));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AdopterOutputDto> getAdopter(long id) {
        return adopterRepository.findById(id).map(adopterMapper::toOutputDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdopterOutputDto> searchAdopters(String sortParam, SortOrder sortOrder, int pageSize, int pageNumber) {

        int offset = (pageNumber - 1) * pageSize;
        List<Adopter> adopters =
                (List<Adopter>) repositoryUtils.searchEntities(Adopter.class, sortParam, sortOrder, pageSize, offset);

        log.debug("Calling searchAdopters with params: sortParam={}, sortOrder={}, pageNumber={}, pageSize={} returned student's ids: {}",
                sortParam, sortOrder, pageNumber, pageSize, adopters.stream().map(Adopter::getId).toList());

        return adopters.stream().map(adopterMapper::toOutputDto).toList();
    }

    @Override
    @Transactional
    public boolean deleteAdopter(long id) {
        Optional<Adopter> adopterOpt = adopterRepository.findById(id);
        if (adopterOpt.isEmpty()) {
            log.warn("No adopter found with id: {}", id);
            return false;
        }
        log.debug("Deleted adopter: {} with all his reports", adopterOpt.get());
        adopterRepository.deleteById(id);
        return true;
    }

    @Override
    @Transactional
    public void addTrialAdopter(Message message) {
        Adopter adopter = new Adopter();
        adopter.setChatId(String.valueOf(message.getChatId()));
        adopter.setName(message.getFrom().getUserName());
        adopter.setPhoneNumber("79123456789");
        adopter.setAssignedReportsQuantity(initialAssignedReportsQuantity);

        Animal freeAnimal = animalRepository.findFirstByAdopterIsNull().orElseThrow();
        adopter.setAnimal(freeAnimal);
        Adopter savedAdopter = adopterRepository.save(adopter);

        freeAnimal.setAdopter(savedAdopter); // TODO 14.06.2024 20:50 - проверить как сетится анимал
        Animal savedAnimal = animalRepository.save(freeAnimal);
        log.debug("Trial adopter saved with assigned animal. Adopter:{}, animal:{}",
                savedAdopter.getId(), savedAnimal.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existAdopterWithChatId(String chatId) {
        return adopterRepository.findByChatId(chatId).isPresent();
    }


    private String validatePhoneNumber(String phoneNumber) {

//        Regexp: Заменяем на пустую строку все пробельные символы, круглые скобки и знак минус
        String replaced = phoneNumber.replaceAll("[\\s()-]+", "");

          /* Возможны только три случая - "+79...", "79..." и "89...", все остальное не пройдет
          валидацию в контроллере. В любом случае приводим номер к виду "79..." */
        return replaced.startsWith("+79")
                ? replaced.replaceFirst("^..", "7")
                : replaced.replaceFirst("^.", "7");
    }
}

