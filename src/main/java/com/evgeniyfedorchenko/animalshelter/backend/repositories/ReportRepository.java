package com.evgeniyfedorchenko.animalshelter.backend.repositories;

import com.evgeniyfedorchenko.animalshelter.backend.entities.Report;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("SELECT report FROM Report report WHERE report.verified = false ORDER BY report.sendingAt ASC")
    List<Report> findOldestUnviewedReports(Pageable pageable);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional  // TODO 10.06.2024 00:22 - Написать комментарий для Димы, почему тут открывается транзакция
    @Query("UPDATE Report r SET r.verified = true WHERE r.id IN (:ids)")
    void updateReportsVerifiedStatus(@Param("ids") List<Long> ids);

    @Query("SELECT r FROM Report r WHERE r.adopter.chatId = :adopterChatId ORDER BY r.sendingAt DESC")
    Optional<Report> findNewestReportByAdopterChatId(String adopterChatId);

}
