package com.evgeniyfedorchenko.animalshelter.backend.repositories;

import com.evgeniyfedorchenko.animalshelter.backend.entities.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {

    @Query("SELECT v.chatId FROM Volunteer v WHERE v.free = true")
    Optional<String> getChatIdFreeVolunteer();

    Optional<Volunteer> findFirstByFreeIsTrue();
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    @Query("UPDATE Volunteer v SET v.free = :status WHERE v.chatId = :chatId")
    void setStatusToVolunteerWithChatId(boolean status, String chatId);
}
