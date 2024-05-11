package com.evgeniyfedorchenko.animalshelter.backend.services;

import com.evgeniyfedorchenko.animalshelter.backend.dto.ReportOutputDto;

import java.util.List;
import java.util.Optional;

public interface ReportService {

    List<ReportOutputDto> getUnverifiedReports(int limit);

    Optional<ReportOutputDto> getReport(long id);

    boolean sendMessageAboutBadReport(long reportId);

     void deleteReports(List<Long> ids);
}
