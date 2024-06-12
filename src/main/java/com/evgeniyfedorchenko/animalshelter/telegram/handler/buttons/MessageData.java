package com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons;

import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks.*;
import com.evgeniyfedorchenko.animalshelter.telegram.handler.buttons.callbacks.report.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


/**
 * Класс перечисления, содержащий всю информацию, необходимую для отправки сообщения
 */
@Getter
@AllArgsConstructor
public enum MessageData {

    /*================================== Menu commands ==================================*/
    START("Start", """
            Добро пожаловать в приют для животных! 🐾
            Здесь вы можете узнать о наших пушистых подопечных, помочь им найти новый дом или просто поддержать нас своим вниманием. Если у вас есть вопросы или вы хотите узнать больше, не стесняйтесь обращаться
            Спасибо, что вы с нами! ❤️
            """),

    HELP("Help", """
            Нужна помощь? Напишите нам! 🐾🤝
            Если у вас возникли вопросы, вы хотите узнать больше о нашей работе или нуждаетесь в помощи, не стесняйтесь обращаться к нам, просто позовите волонтера и он ответит на все ваши вопросы. Или просто можете начать сначала, если вдруг запутались 😉
            Мы всегда готовы помочь вам и нашим пушистым друзьям
            Спасибо за вашу заботу! ❤️
            """),

    VOLUNTEER("Volunteer", """
            Позвали волонтера. Он ответит Вам в этом чате"""),


    /*================================== On "/start" command ==================================*/
    MAIN_ABOUT_SHELTER(MainAboutShelter.class.getSimpleName(), """
            Мы - команда неравнодушных людей, которые посвятили себя заботе о бездомных и брошенных животных, присоединяйтесь! 🫂
            Наш приют был основан в 2018 году с целью предоставить временный дом и необходимую помощь животным, оказавшимся в сложной ситуации. Мы принимаем кошек, собак и других домашних питомцев, которым нужна забота и поддержка 🐾 В нашем приюте животные получают полноценное питание, ветеринарную помощь, а также любовь и внимание наших сотрудников. Мы стараемся найти для каждого питомца заботливого и ответственного хозяина, который сможет подарить ему свой дом навсегда 🏡 Вы можете посетить наш сайт, если вам интересна наша деятельность или получить контактные данные охраны, чтобы, например, оформить пропуск на вашу машину. Так же вы можете почитать о технике безопасности на территории нашего приюта или просто оставить свои контактные данные чтобы мы с ваши связались. Если вы хотите помочь нашему приюту, вы можете сделать пожертвование или стать волонтером. Любая помощь будет очень ценна для нас и наших подопечных. Спасибо, что неравнодушны к судьбе бездомных животных! 💕
            Итак, что бы вы хотели сделать? 😇
            """),

    MAIN_HOW_TAKE_ANIMAL(MainHowTakeAnimal.class.getSimpleName(), """
            Мы любим наших пушистых друзей 🐾, а потому доверяем их только в добрые руки ❤️
            Чтобы усыновить пушистого друга, нужно быть готовым к этому, но не волнуйтесь, это не сложно. Ниже вы найдете список необходимых документов и рекомендаций
            Как будете готовы, просто приезжайте в гости. Адрес и время работы вы можете найти в главном меню
            До встречи! 🥳
            """),

    MAIN_REPORT_MENU(MainReportMenu.class.getSimpleName(), """
            Спасибо, что не забываете о нас! 😊
            И что относитесь к своему новому другу со всей ответственностью, это очень похвально. Если вы готовы прислать отчет, то просто нажмите на "Прислать отчет"; либо вы можете посмотреть как это делать лучше всего. И мы рекомендуем вам посмотреть, если вы делаете это в первый раз 😉      
            🥇🐶
            """),

    /*================================== On MAIN_ABOUT button ==================================*/
    SECURITY_CONTACTS(SecurityContacts.class.getSimpleName(), """
            Охрана приюта работает круглосуточно. Если у вас возникли вопросы или проблемы, пожалуйста, свяжитесь с нами:
                        
            Телефон: +7 (123) 456-78-90
            Электронная почта: security@animalshelter.org
                        
            Наши сотрудники всегда готовы помочь и ответить на ваши обращения. Спасибо за вашу заботу о животных! 🐾
            """),

    SAFETY_AT_SHELTER(SafetyAtShelter.class.getSimpleName(), """
            Правила техники безопасности в нашем уютном приюте 🐾
                        
            1. Будьте осторожны и ласковы при общении с нашими питомцами. Гладьте их только с разрешения наших сотрудников 🐶🐱
            2. Передвигайтесь по специальным дорожкам, чтобы не потревожить наших животных. Не заходите в служебные помещения без сопровождения 🚶‍♀️
            3. Пожалуйста, не курите и не употребляйте алкоголь на территории приюта. Это важно для здоровья и безопасности 🚫
            4. Если что-то случится, сразу сообщите нашей охране - они всегда готовы помочь! 🚨
            5. Давайте вместе поддерживать чистоту и порядок. Выбрасывайте мусор только в специальные контейнеры 🗑️
                        
            Спасибо, что соблюдаете правила и заботитесь о наших питомцах! 💕
            """),


    /*================================== On MAIN_REPORT_MENU button ==================================*/
    GET_PATTERN_REPORT(GetPatternReport.class.getSimpleName(), """
            Отлично, мы очень рады, что вы столь дисциплинированы! Хороший отчет должен состоять из четырех небольших частей. Все просто, смотрите сами, вам нужно прислать:
            1. Фото вашего животного 🖼
            2. Рацион питания 😋
            3. Общее самочувствие и привыкание к новому месту 🫠
            4. И изменения в поведении
                        
            Не волнуйтесь, я буду с вами и буду подсказывать. Еще раз спасибо за ваши заботу о наших пушистых друзьях! Если вы готовы, то мы можем приступать 💕
            """),


    /*================================== On MAIN_HOW_TAKE_ANIMAL button ==================================*/
    DATING_RULES(DatingRules.class.getSimpleName(), """
            Давайте познакомимся с нашими питомцами! 🐶🐱
                       
            1. Подходите к животному медленно и спокойно, не делайте резких движений.
            2. Протяните руку ладонью вверх, чтобы животное могло почувствовать ваш запах.
            3. Погладьте животное аккуратно, избегая резких движений. Начните с головы и шеи.
            4. Будьте внимательны к реакции животного - если оно выглядит напуганным или агрессивным, отойдите.
            5. Никогда не кричите на животных и не пугайте их. Говорите с ними ласковым голосом. 🥰
                       
             Давайте вместе заботиться о наших пушистых друзьях! 
            """),

    HOUSE_FOR_ADULT_ANIMAL(HouseForAdultAnimal.class.getSimpleName(), """
            Готовим дом для нового питомца! 🏠🐶🐱
                         
            1. Обеспечьте животному уютное и безопасное пространство - мягкую лежанку, игрушки, миски для еды и воды.
            2. Выделите специальное место для туалета, регулярно убирайте и меняйте наполнитель.
            3. Позаботьтесь о правильном питании - выбирайте качественные корма, соответствующие возрасту и потребностям питомца.
            4. Уделяйте время для игр и прогулок, чтобы ваш питомец чувствовал себя любимым и счастливым. 🥰
            5. Регулярно проводите осмотр и обработку от паразитов, следите за здоровьем животного.
                         
            Создайте для своего питомца идеальный дом, полный любви и заботы!
             """),

    HOUSE_FOR_SMALL_ANIMAL(HouseForSmallAnimal.class.getSimpleName(), """
            Готовим дом для малыша! 🏠🐶🐱🐾
                         
            1. Создайте безопасное и уютное пространство с мягкими лежанками, игрушками и мисками.
            2. Выделите специальное место для туалета, регулярно убирайте и меняйте наполнитель. 🧹
            3. Подберите сбалансированное питание, подходящее для возраста и потребностей малыша. 🍲
            4. Уделяйте время для игр, ласки и прогулок, чтобы ваш питомец чувствовал себя любимым! 🤗
            5. Регулярно проводите осмотр и обработку от паразитов, следите за здоровьем. 🩺
                         
            Сделайте дом вашего малыша идеальным, полным любви и заботы! 💕
             """),

    ADOPTION_DOCS(AdoptionDocs.class.getSimpleName(), """
            Готовимся к усыновлению нового друга! 🐶🐱🧾 Чтобы забрать питомца домой, вам понадобятся несколько важных документов:
                       
            1. Паспорт или другой официальный документ, удостоверяющий вашу личность. Это необходимо для оформления всех бумаг.
            2. Заявление об усыновлении, которое вы сможете заполнить прямо у нас. Мы поможем вам со всеми необходимыми пунктами.
            3. Согласие на обработку ваших персональных данных. Это стандартная процедура для обеспечения конфиденциальности. 🔒
                       
            Не волнуйтесь, мы сделаем весь процесс максимально простым и быстрым! Вместе мы сделаем вашего нового пушистого друга по-настоящему счастливым! 🥰
            """),

    TRANSPORT_PET(TransportPet.class.getSimpleName(), """
            Перевозим питомца с заботой! 🚗🐶🐱 Чтобы ваш пушистый друг чувствовал себя комфортно во время поездки, соблюдайте несколько простых правил:
                        
            1. Используйте специальную переноску или клетку, подходящую по размеру для вашего питомца. Это обеспечит его безопасность.
            2. Разместите в переноске мягкую подстилку, игрушки и миску с водой, чтобы животному было уютно.
            3. Перевозите питомца в салоне автомобиля, а не в багажнике. Так он будет чувствовать себя спокойнее.
            4. Держите переноску закрытой, чтобы животное не выпрыгнуло во время движения.
            5. Делайте частые остановки, чтобы дать питомцу возможность размяться и сходить в туалет.
                        
            Мы поможем вам организовать безопасную и комфортную перевозку вашего нового друга! 🤗 Надеюсь, этот текст будет полезен для твоего проекта. Желаю успехов в создании замечательного телеграм-бота! 💫
            """),

//    Кнопка юзера, по нажатию он получает это сообщение
    ENDING_VOLUNTEER_CHAT("EndingVolunteerChat", "Диалог завершен"),

    // TODO 12.06.2024 15:07 - Потестить эти две кнопки
//    Колбек для волонтера, показывается ему, когда юзер завершил диалог
    ENDED_VOLUNTEER_CHAT(EndingVolunteerChat.class.getSimpleName(),
            "Диалог завершен. Вы можете продолжить пользоваться ботом в обычном режиме"),

    SEND_REPORT(SendReport.class.getSimpleName(), """
            Спасибо, что вы взяли питомца из нашего приюта! 🐶🐱 Мы рады, что он нашел свой новый дом. Пожалуйста, отправьте нам отчет о состоянии вашего питомца, чтобы мы могли следить за его благополучием. Отчет состоит из трех деталей:
            - Рацион питания
            - Общее самочувствие и привыкание к новому месту
            - Изменения в поведении: отказ от старых привычек, приобретение новых
            - Фото животного 📸
            
            Пожалуйста, нажмите кнопку, в зависимости от того, что вы готовы прислать. Спасибо за вашу заботу и поддержку! 🙏 Мы ценим, что вы помогаете нам следить за благополучием наших подопечных
            """),

    // TODO 12.06.2024 16:24 - подставлять текст еще не присланных частей в рантайме
    SEND_REPORT_CONTINUE_PATTERN(SendReportContinue.class.getSimpleName(), """
            Спасибо, что делитесь с нами этой информацией! 👍🐶 Это очень важно для нас. Продолжим?, чтобы мы могли полностью отследить его благополучие. Для следующего шага, пожалуйста, нажмите на одну из кнопок ниже:
            %s
            Обратите внимание, что вы еще не прислали информацию по этим пунктам. Мы с нетерпением ждем ваше следующее сообщение! 💕"""),

    SEND_REPORT_END(SendReportEnd.class.getSimpleName(), """
            Вы молодец! 👏🎉 Спасибо, что прислали всю необходимую информацию о вашем питомце:
            - Рацион питания
            - Состояние здоровья
            - Изменения в поведении
            - Фото питомца в новом доме
            Мы очень ценим, что вы так тщательно следите за благополучием вашего питомца и делитесь с нами всеми важными деталями. Это очень помогает нам контролировать состояние животных, которых мы пристроили.
            Еще раз большое спасибо! 🙏🐶 Мы рады, что ваш питомец нашел свой новый дом. Ждем завтра еще один отчет от вас!
            """),

    SEND_DIET(SendDiet.class.getSimpleName(), """
            Спасибо, что делитесь рационом питания вашего питомца! 🙏🐶 Пожалуйста, отправьте подробности о кормах, количестве и частоте кормлений. 🍔🍕🍎 Ждем ваше сообщение, чтобы убедиться, что ваш питомец получает сбалансированное питание. 💕 Обратите внимание, ограничение - 500 символов"""),

    SEND_HEALTH(SendHealth.class.getSimpleName(), """
            Спасибо, что сообщаете о здоровье и самочувствии питомца! 🙏🐶 Поделитесь информацией о его состоянии, проблемах со здоровьем и поведением. 💊🩺 Ценим, что следите за его благополучием. 💕 Ограничение - 500 символов"""),

    SEND_BEHAVIOR(SendBehavior.class.getSimpleName(), """
            Окей, расскажите об изменениях в поведении питомца! 🐶🐱 Поделитесь, если он отказался от старых привычек или приобрел новые. 🔄 Эта информация важна для понимания его адаптации. 🏠 Спасибо за внимание к деталям! 💕 Ограничение - 500 символов"""),

    SEND_PHOTO(SendPhoto.class.getSimpleName(), """
            Окей, пришлите нам фото вашего питомца! 📸🐶🐱 Мы будем рады увидеть, как он живет в новом доме. 🏠 Текст прикладывать не нужно - просто отправьте фотографию. 💕 Спасибо, что делитесь с нами!""");

    /**
     * Текст обратного вызова, который можно добавить к кнопке, чтобы при нажатии
     * на нее можно было перехватить, и, таким образом, стало понятно, какую кнопку пользователь нажал в Telegram.
     * Это должно быть название компонента Spring, который будет обрабатывать соответствующую кнопку
     */
    private final String callbackData;

    /**
     * Текст самого сообщения, которое может быть отправлено пользователю с помощью telegram-бота
     */
    @Setter
    private String answer;
}
