# **Animal shelter** 🐶

Это приложение создано, для вымышленного приюта для животных "Animal Shelter". Чтобы потенциальные или действующие
усыновители животных моги с легкостью получить необходимую информацию о приюте и о животных, которые тут содержатся.
А так же чтобы администрация приюта могла с большим удобством управлять своим приютом и могла помогать усыновителям
в адаптиции животныхками приюта для животных.
___
### *Техническое описание* 📃
*Документацию по методам и классам смотри в Notion:*
[Техническое описание Animal Shelter](https://www.notion.so/Animal-Shelter-0bc3f0041241463a970f5c110f339995)

Приложение представляет собой сервис на Spring boot c интегрированным Telegram-ботом. Приложение состоит из трех 
основных частей:
- **Telegram-бот:** это основная и единственная точка взаимодействия гостей приюта (или усыновителей)
и самого сервиса приюта. Архитектура бота (кнопка - ответ) построена на имплементации интерфейсов отдельными классами 
кнопок, что позволяет следить за типами и легко масштабировать этот функционал. Так можно узнать информацию о самом 
приюте или же связаться с волонтером для получения доп информации - общение происходит непосредственно через самого
бота посредством пересылки сообщений, таким образом аккаунты волонтером и юзеров не взаимодействуют напрямую.
Подреживаемые форматы сообщений:
  - Текстовое
  - Фото
  - Текст + фото
  - Стикер
Так же предусмотрена возможность для масштабирования форматов сообщений. Храниение связи во время такого диалога
реализовано посредством Redis и перехвата им всех входящих сообщения еще до последующего распределения

[Ссылка на бота](https://t.me/animal_shelter_helper_bot)


- **API сервиса**: Предусмотено API для администрирования и управления данными, контроля животных,
действующих усыновителей и их отчетов. Предусмотрена эндпоинты для
  - Регистрирования вышеупомянутых сущностей (DTO)
  - Установки связей между ними, например для "выдачи" животных усыновителям
  - Просмотр отчетов о содержании, отправленных усыновителями
  - Реакция на ненадлежащее качество отчета путем отправки алера в телеграм целевому пользователю
  - Масштабный поиск и навигирование по базе данных, поиск по связанным полям с различной сортировкой и пагинацией


  *Полный список эндпоинтов смотри в Notion*


- **Сервисная часть**: для выполнения логики взаимодействия как с гостями приюта (через Telegram-бота),
так и администраторами (через API) реализованы Spring-сервисы, как имплементации интерфейсов + JPA репозитории.
Здесь инкапсулирована вся внутренняя логика приложения - валидация параметров, создание сущностей и установка связей,
получение объект из базы данных для закрытия потребностей контроллеров и бота.
В сервисах присутствуют Scheduled-методы для автоматического принятия решения по адаптации животных у усыновителей,
а так же для валидирования состояния кеша Redis

___
#### Тестирование
Интеграционные тесты покрывают 70% кода, включая все основные цепочки алгоритмов, прилагаю статистику покрытия, снятую
при помощи плагина Jacoco:
![Покрытие Jacoco](https://raw.githubusercontent.com/evgeniy-fedorchenko/animal-shalter/release-1.0/coverage-21-06-24.png)
___
### *Используемые инструменты и технологии* 💻

- Java 21, Spring boot, Maven
- Spring Data JPA, PostgreSQL, Hibernate, JPQL, liquibase, Criteria API, ручное управление транзакциями (open-in-view)
- RedisTemplate
- Spring Validation
- OpenAPI documentation
- JUnit, Mockito, Jacoco
- Spring profiles
- CompletableFuture
- Java reflect
- Lombok
- Slf4j

Документация представлено этой обзорной страницей, подробным тех.описанием в Notion, так же имеется Javadoc
и исчерпывающая документация OpenAPI (описаны DTO, модели, параметры, возвожные запросы и ответы)


**Напоминаю, что больше технических деалей вы найдете в *Notion* на старнице проекта, еще раз ссылка:**
**[Страница Animal Shelter в Notion](https://www.notion.so/Animal-Shelter-0bc3f0041241463a970f5c110f339995)**
___
### *Контакты* 👥
  - Github: https://github.com/evgeniy-fedorchenko
  - Email: jecky432@gmail.com
  - Телефон: 8 (902) 643-66-42


## ***Спасибо*** 👌