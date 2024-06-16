package com.evgeniyfedorchenko.animalshelter.backend.services;

import com.evgeniyfedorchenko.animalshelter.backend.entities.Adopter;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Report;
import com.evgeniyfedorchenko.animalshelter.backend.entities.Volunteer;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.AdopterRepository;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.AnimalRepository;
import com.evgeniyfedorchenko.animalshelter.backend.repositories.VolunteerRepository;
import com.evgeniyfedorchenko.animalshelter.telegram.listener.TelegramExecutor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.evgeniyfedorchenko.animalshelter.backend.services.TelegramServiceImpl.AdaptationDecision.*;

@Slf4j
@Service
@AllArgsConstructor
public class TelegramServiceImpl implements TelegramService {

    private final TelegramExecutor telegramExecutor;
    private final VolunteerRepository volunteerRepository;
    private final AdopterRepository adopterRepository;
    private final AnimalRepository animalRepository;

    @Override
    public boolean sendMessage(String chatId, String message) {
        SendMessage sendMessage = new SendMessage(chatId, message);
        return telegramExecutor.send(sendMessage);
    }

    @Async
    @Override
    @Transactional
    public CompletableFuture<Optional<String>> getFreeVolunteer() {

        CompletableFuture<Optional<String>> f = CompletableFuture.supplyAsync(() ->
                        volunteerRepository.findFirstByFreeIsTrue().map(Volunteer::getChatId));

        f.thenAcceptAsync(chatIdOpt -> chatIdOpt.ifPresent(chatId ->
                volunteerRepository.setStatusToVolunteerWithChatId(false, chatId)));
        return f;
    }

    @Override
    @Transactional
    public void returnVolunteer(String volunteerChatId) {
        volunteerRepository.setStatusToVolunteerWithChatId(true, volunteerChatId);
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 9 * * *")   // Every day at 9:00 am
    public void makeDecisionAboutAdaptation() {
//        Получаем всех адаптеров, накопивших столько отчетов, сколько назначено
        List<Adopter> adopters = adopterRepository.findAdoptersWithMatchingQuantityReports();

        EnumMap<AdaptationDecision, List<Adopter>> decisionsMap = new EnumMap<>(AdaptationDecision.class);

        adopters.forEach(adopter -> {
            double acceptedReportsCount = (double) adopter.getReports().stream()
                    .filter(Report::isAccepted)
                    .count();

           /* Получаем дробь от 0...1 (т.к. acceptedReportsCount <= reports.size), умножаем на 10, чтоб иметь один
              знак перед запятой, а дробную часть выкидываем, так получаем int от 0...10, т.е. % принятых отчетов.
              Далее в зависимости от этого % выбираем ключ (enum) и кладем в лист под этот ключ в decisionsMap */
            int percent = (int) (acceptedReportsCount / adopter.getReports().size() * 10);
            decisionsMap.computeIfAbsent(
                    switch (percent) {
                        case 10, 9 -> SUCCESS;
                        case 8, 7 -> NEED_15_MORE;
                        case 6, 5 -> NEED_30_MORE;
                        default -> FAIL;
                    },
                    _ -> new ArrayList<>()).add(adopter);
        });
//        Мапа для логирования: ключи как есть, значения - списки адоптеров мапим на списки их chatId
        Map<AdaptationDecision, List<String>> decisionChatIdsMap = decisionsMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream().map(Adopter::getChatId).toList()
                ));
        log.info("Decisions taken: {}", decisionChatIdsMap);

        sendComputedMap(decisionsMap);
    }

    private void sendComputedMap(EnumMap<AdaptationDecision, List<Adopter>> adaptationDecisionMap) {

        adaptationDecisionMap.forEach((decision, adoptersList) -> {

            switch (decision) {

                case SUCCESS -> adoptersList.forEach(adopter -> {
                    sendMessage(adopter.getChatId(), SUCCESS.getMessage());
                    adopterRepository.deleteById(adopter.getId());
                    animalRepository.deleteById(adopter.getAnimal().getId());
                });

                case NEED_15_MORE -> adoptersList.forEach(adopter -> {
                    sendMessage(adopter.getChatId(), NEED_15_MORE.getMessage());
                    adopter.setAssignedReportsQuantity(adopter.getAssignedReportsQuantity() + 15);
                    adopterRepository.save(adopter);
                });

                case NEED_30_MORE -> adoptersList.forEach(adopter -> {
                    sendMessage(adopter.getChatId(), NEED_30_MORE.getMessage());
                    adopter.setAssignedReportsQuantity(adopter.getAssignedReportsQuantity() + 30);
                    adopterRepository.save(adopter);
                });

                case FAIL -> adoptersList.forEach(adopter -> {
                    sendMessage(adopter.getChatId(), FAIL.getMessage());
//                    Так же тут можно отправлять сообщение какому-нибудь старшему, что мол обрати внимание
                });
            }
        });
    }

    @Getter
    @AllArgsConstructor
    public enum AdaptationDecision {
        SUCCESS("Поздравляем! \uD83C\uDF89 Испытательный срок успешно завершен. Вы теперь официальный владелец. \uD83E\uDD73 Желаем вам и вашему новому другу долгих и счастливых лет вместе! \uD83D\uDE0A"),
        NEED_15_MORE("К сожалению, испытательный срок по опеке над питомцем продлен еще на 15 дней. Мы надеемся, что за это время вы сможете наладить более тесную связь с питомцем\nПожалуйста, свяжитесь с нами, если у вас возникнут какие-либо вопросы или проблемы"),
        NEED_30_MORE("К сожалению, испытательный срок по опеке над питомцем продлен еще на 30 дней. Мы надеемся, что за это время вы сможете наладить более тесную связь с питомцем\nПожалуйста, свяжитесь с нами, если у вас возникнут какие-либо вопросы или проблемы"),
        FAIL("К сожалению, исходя из ваших отчетов мы приняли решение, что адаптация питомца к вашему дому оказалась неуспешной. Пожалуйста, свяжитесь с нами в ближайшее время, чтобы обсудить дальнейшие шаги по поиску нового, более подходящего владельца для питомца\nСпасибо за понимание");

        private final String message;
    }
}
