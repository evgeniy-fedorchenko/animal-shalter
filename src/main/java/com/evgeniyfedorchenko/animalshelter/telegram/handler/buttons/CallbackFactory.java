package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.evgeniyfedorchenko.animalshelter.telegram.handler.CallType.*;

@Component
public class CallbackFactory {

    private final MessageUtils messageUtils = new MessageUtils();
    public final Map<String, String> keyboardData = new LinkedHashMap<>();


    /*================================== From "/start" command ==================================*/
    @Component("MainAboutShelter")
    public class MainAboutShelter implements Callback {
        @Override
        public EditMessageText apply(String chatId, Integer messageId) {
            keyboardData.clear();
            keyboardData.put("Наш сайт",                      "will be link");
            keyboardData.put("Контактные данные охраны",      SECURITY_CONTACTS.getTitle());
            keyboardData.put("Правила безопасности в приюте", SAFETY_AT_SHELTER.getTitle());
            keyboardData.put("Запишите мои данные (backend)", START.getTitle());
            keyboardData.put("Назад",                         START.getTitle());

            MessageModel messageModel = new MessageModel(chatId, messageId, MAIN_ABOUT_SHELTER, keyboardData);
            EditMessageText editMessageText = messageUtils.applyCallback(messageModel);
            messageUtils.setUrlToButton(editMessageText);
            return editMessageText;
        }
    }

    @Component("MainHowTakeAnimal")
    public class MainHowTakeAnimal implements Callback {
        @Override
        public EditMessageText apply(String chatId, Integer messageId) {
            keyboardData.clear();
            keyboardData.put("Правила знакомства с животным",             DATING_RULES.getTitle());
            keyboardData.put("Обустройство дома для взрослого животного", HOUSE_FOR_ADULT_ANIMAL.getTitle());
            keyboardData.put("Обустройство дома для щенка/котенка",       HOUSE_FOR_SMALL_ANIMAL.getTitle());
            keyboardData.put("Документы для усыновления питомца",         ADOPTION_DOCS.getTitle());
            keyboardData.put("Транспортировка животного",                 TRANSPORT_PET.getTitle());
            keyboardData.put("Назад",                                     START.getTitle());

            return messageUtils.applyCallback(new MessageModel(chatId, messageId, MAIN_HOW_TAKE_ANIMAL, keyboardData));
        }
    }

    @Component("MainReportMenu")
    public class MainReportMenu implements Callback {
        @Override
        public EditMessageText apply(String chatId, Integer messageId) {
            keyboardData.clear();
            keyboardData.put("Прислать отчет (backend)", START.getTitle());
            keyboardData.put("Получить шаблон отчета",   GET_PATTERN_REPORT.getTitle());
            keyboardData.put("Назад",                    START.getTitle());

            return messageUtils.applyCallback(new MessageModel(chatId, messageId, MAIN_REPORT_MENU, keyboardData));
        }
    }

    /*================================== From MAIN_ABOUT callback ==================================*/
    @Component("SecurityContacts")
    public class SecurityContacts implements Callback {
        @Override
        public EditMessageText apply(String chatId, Integer messageId) {
            keyboardData.clear();
            keyboardData.put("Назад", MAIN_ABOUT_SHELTER.getTitle());
            return messageUtils.applyCallback(new MessageModel(chatId, messageId, SECURITY_CONTACTS, keyboardData));
        }
    }

    @Component("SafetyAtShelter")
    public class SafetyAtShelter implements Callback {
        @Override
        public EditMessageText apply(String chatId, Integer messageId) {
            keyboardData.clear();
            keyboardData.put("Назад", MAIN_ABOUT_SHELTER.getTitle());
            return messageUtils.applyCallback(new MessageModel(chatId, messageId, SAFETY_AT_SHELTER, keyboardData));
        }
    }

    /*================================== From MAIN_REPORT_MENU callback ==================================*/
    @Component("GetPatternReport")
    public class GetPatternReport implements Callback {
        @Override
        public EditMessageText apply(String chatId, Integer messageId) {
            keyboardData.clear();
            keyboardData.put("Начнем! (backend)", START.getTitle());
            keyboardData.put("Назад",             MAIN_REPORT_MENU.getTitle());

            return messageUtils.applyCallback(new MessageModel(chatId, messageId, GET_PATTERN_REPORT, keyboardData));
        }
    }

    /*================================== From MAIN_HOW_TAKE_ANIMAL callback ==================================*/
    @Component("DatingRules")
    public class DatingRules implements Callback {
        @Override
        public EditMessageText apply(String chatId, Integer messageId) {
            keyboardData.clear();
            keyboardData.put("Назад", MAIN_HOW_TAKE_ANIMAL.getTitle());
            return messageUtils.applyCallback(new MessageModel(chatId, messageId, DATING_RULES, keyboardData));
        }
    }

    @Component("HouseForAdultAnimal")
    public class HouseForAdultAnimal implements Callback {
        @Override
        public EditMessageText apply(String chatId, Integer messageId) {
            keyboardData.clear();
            keyboardData.put("Назад", MAIN_HOW_TAKE_ANIMAL.getTitle());
            return messageUtils.applyCallback(new MessageModel(chatId, messageId, HOUSE_FOR_ADULT_ANIMAL, keyboardData));
        }
    }

    @Component("HouseForSmallAnimal")
    public class HouseForSmallAnimal implements Callback {
        @Override
        public EditMessageText apply(String chatId, Integer messageId) {
            keyboardData.clear();
            keyboardData.put("Назад", MAIN_HOW_TAKE_ANIMAL.getTitle());
            return messageUtils.applyCallback(new MessageModel(chatId, messageId, HOUSE_FOR_SMALL_ANIMAL, keyboardData));
        }
    }

    @Component("AdoptionDocs")
    public class AdoptionDocs implements Callback {
        @Override
        public EditMessageText apply(String chatId, Integer messageId) {
            keyboardData.clear();
            keyboardData.put("Назад", MAIN_HOW_TAKE_ANIMAL.getTitle());
            return messageUtils.applyCallback(new MessageModel(chatId, messageId, ADOPTION_DOCS, keyboardData));
        }
    }

    @Component("TransportPet")
    public class TransportPet implements Callback {
        @Override
        public EditMessageText apply(String chatId, Integer messageId) {
            keyboardData.clear();
            keyboardData.put("Назад", MAIN_HOW_TAKE_ANIMAL.getTitle());
            return messageUtils.applyCallback(new MessageModel(chatId, messageId, TRANSPORT_PET, keyboardData));
        }
    }

    /*================================== Command's callbacks ==================================*/
    @Component("Start")
    public class BackToStart implements Callback {
        @Override
        public String getCallbackName() {
            return "Start";
        }

        @Override
        public EditMessageText apply(String chatId, Integer messageId) {

            MessageModel messageModel = new MessageModel(chatId, messageId, START, null);
            EditMessageText editMessage = messageUtils.applyCallback(messageModel);

            /* К сожалению из объекта SendMessage нельзя вытащить клавиатуру в виде коллекции кнопок (или мапы), можно
               только в виде ReplyKeyboard. А метод создания клавиатуры принимает только мапу, потому создаем клавиатуру
               через отдельный объект new Start, забираем её в виде объекта ReplyKeyboard и сетим в сообщение */

            ReplyKeyboard replyMarkup = new CommandFactory().new Start().apply(chatId).getReplyMarkup();
            editMessage.setReplyMarkup((InlineKeyboardMarkup) replyMarkup);
            return editMessage;
        }
    }

    @Component("Volunteer")
    public class VolunteerCallback implements Callback {

//        TODO Может быть объединить классы VolunteerCallback и VolunteerCommand и реализовать в них оба интерфейса
//         Аналогично с командой Start, но как-то нелогично, что CommandFactory реализует Callback в двух кнопках из трех
//        @Override
//        public String getCallbackName() {
//            return "Volunteer";
//        }
        @Override
        public EditMessageText apply(String chatId, Integer messageId) {
            return messageUtils.applyCallback(new MessageModel(chatId, messageId, VOLUNTEER, null));
        }
    }

    @Component
    public class Blank implements Callback {
        @Override
        public EditMessageText apply(String chatId, Integer messageId) {
            return messageUtils.applyCallback(new MessageModel(chatId, messageId, BLANK, null));
        }
    }
}
