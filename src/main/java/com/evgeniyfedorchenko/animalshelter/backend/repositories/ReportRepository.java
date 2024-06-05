package com.evgeniyfedorchenko.animalshelter.backend.repositories;

import com.evgeniyfedorchenko.animalshelter.backend.entities.Report;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("SELECT report FROM Report report WHERE report.verified = false ORDER BY report.sendingAt ASC")
    @Transactional(readOnly = true)
    List<Report> findOldestUnviewedReports(Pageable pageable);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    @Query("UPDATE Report r SET r.verified = true WHERE r.id IN (:ids)")
    void updateReportsViewedStatus(@Param("ids") List<Long> ids);

}
