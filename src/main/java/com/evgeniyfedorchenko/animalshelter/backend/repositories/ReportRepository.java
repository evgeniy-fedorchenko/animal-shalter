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


    /**
     * Насчет аннотации @Transactional - поскольку это обращение в бд выполняется в другом потоке, то там предыдущая
     * транзакция недоступна. Spring не распространяет транзакционный контекст между потоками. В Spring все транзакции
     * в конечном счете попадают в какой-то TransactionManager, который изолирует их между потоками, тут только если
     * вручную заниматься открытием-закрытием транзакций, но я не хочу.
     *
     * Например, вот один из ресурсов, который это демонстрирует:
     * https://stackoverflow.com/questions/30079486/spring-hibernate-transactions-joining-a-transaction-in-thread-b-created-in-a/30091149#30091149
     * Это логично, не стоит ведь глобальное состояние в нескольких местах одновременно. Так что в будущем удалю
     * асинхронку из того сервисного метода, где происходит это обращение к бд
     * Аналогично для VolunteerRepository
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    @Query("UPDATE Report r SET r.verified = true WHERE r.id IN (:ids)")
    void updateReportsVerifiedStatus(@Param("ids") List<Long> ids);

    @Query("SELECT r FROM Report r WHERE r.adopter.chatId = :adopterChatId ORDER BY r.sendingAt DESC")
    Optional<Report> findNewestReportByAdopterChatId(String adopterChatId);

}
