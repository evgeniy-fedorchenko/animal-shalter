package com.evgeniyfedorchenko.animalshelter.backend.services;

import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Report;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.evgeniyfedorchenko.animalshelter.Constants.generateTestAdoptersInCountOf;
import static com.evgeniyfedorchenko.animalshelter.Constants.generateTestReportsInCountOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private ReportRepository reportRepositoryMock;
    @Mock
    private TelegramServiceImpl telegramServiceMock;
    @InjectMocks
    private ReportServiceImpl out;

    @Captor
    private ArgumentCaptor<String> adopterChatIdCaptor;
    @Captor
    private ArgumentCaptor<String> messageCaptor;

    private final Adopter testAdopter = generateTestAdoptersInCountOf(1).getFirst();
    private final Report testReport = generateTestReportsInCountOf(1).getFirst();


    @BeforeEach
    void BeforeEach() {
    }

    @Test
    void sendMessageAboutBadReport_positiveTest() {
        Adopter targetAdopter = testAdopter;
        Report targetReport = testReport;
        String targetMessage = "Bad report";
        targetAdopter.addReport(targetReport);

        when(reportRepositoryMock.findById(targetReport.getId())).thenReturn(Optional.of(targetReport));
        when(telegramServiceMock.sendMessage(targetAdopter.getChatId(), targetMessage)).thenReturn(true);

        boolean actual = out.sendMessageAboutBadReport(targetReport.getId());

        assertThat(actual).isTrue();

        verify(reportRepositoryMock, times(1)).findById(targetReport.getId());
        verify(telegramServiceMock, times(1))
                .sendMessage(adopterChatIdCaptor.capture(), messageCaptor.capture());

        assertThat(adopterChatIdCaptor.getValue()).isEqualTo(targetAdopter.getChatId());
        assertThat(messageCaptor.getValue()).isEqualTo(targetMessage);
    }

    @Test
    void sendMessageAboutBadReportWhenReportNotFound_negativeTest() {
        Report targetReport = testReport;
        when(reportRepositoryMock.findById(targetReport.getId())).thenReturn(Optional.empty());

        boolean actual = out.sendMessageAboutBadReport(targetReport.getId());
        assertThat(actual).isFalse();

        verify(reportRepositoryMock, times(1)).findById(targetReport.getId());
        verify(telegramServiceMock, never()).sendMessage(anyString(), anyString());
    }
}