package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.CallType.*;

@Slf4j
@Component
public class CommandFactory {

    private final MessageUtils messageUtils = new MessageUtils();
    private final Map<String, String> keyboardData = new LinkedHashMap<>();

    @Component("/start")
    public class Start implements Command {
        @Override
        public SendMessage apply(String chatId) {
            keyboardData.clear();
            keyboardData.put("О нашем приюте",               MAIN_ABOUT_SHELTER.getTitle());
            keyboardData.put("Как взять животное из приюта", MAIN_HOW_TAKE_ANIMAL.getTitle());
            keyboardData.put("Прислать отчет о питомце",     MAIN_REPORT_MENU.getTitle());

            return messageUtils.applyCommand(new MessageModel(chatId, START, keyboardData));
        }
    }

    @Component("/help")
    public class Help implements Command {
        @Override
        public SendMessage apply(String chatId) {
            keyboardData.clear();
            keyboardData.put("Позвать волонтера", VOLUNTEER.getTitle());
            keyboardData.put("Начать сначала",    START.getTitle());

            return messageUtils.applyCommand(new MessageModel(chatId, HELP, keyboardData));
        }
    }

    @Component("/volunteer")
    public class VolunteerCommand implements Command {
//        @Override
//        public String getTitle() {
//            return "/volunteer";
//        }

        @Override
        public SendMessage apply(String chatId) {
            new Thread(this::callVolunteer).start();
            return messageUtils.applyCommand(new MessageModel(chatId, VOLUNTEER, null));
        }

        private void callVolunteer() {
            // todo Реализовать вызов волонтера
            log.info("Calling volunteer...");
        }
    }
}
