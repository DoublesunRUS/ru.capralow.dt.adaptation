# Поддержка префиксов [![Build Status](https://travis-ci.com/DoublesunRUS/ru.capralow.dt.adaptation.svg)](https://travis-ci.com/DoublesunRUS/ru.capralow.dt.adaptation) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=DoublesunRUS_ru.capralow.dt.adaptation&metric=alert_status)](https://sonarcloud.io/dashboard?id=DoublesunRUS_ru.capralow.dt.adaptation) [![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=DoublesunRUS_ru.capralow.dt.adaptation&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=DoublesunRUS_ru.capralow.dt.adaptation) [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=DoublesunRUS_ru.capralow.dt.adaptation&metric=coverage)](https://sonarcloud.io/dashboard?id=DoublesunRUS_ru.capralow.dt.adaptation)


## Поддержка префиксов для [1C:Enterprise Development Tools](http://v8.1c.ru/overview/IDE/) 2021.2

Минимальная версия EDT: 2021.2

Текущий релиз в ветке [master: 1.0.0](https://github.com/DoublesunRUS/ru.capralow.dt.adaptation/tree/master).<br>

В данном репозитории хранятся только исходники.<br>

**ВНИМАНИЕ** Плагин использует новый тестовый API механизма проверок. Данный API пока не вошел в релиз EDT, поэтому плагин работоспособен только в режиме разработки плагинов! Установить его в EDT 2021.1 и ниже нельзя!

Плагин можно установить в EDT через пункт "Установить новое ПО" указав сайт обновления http://capralow.ru/edt/adaptation/latest/ . Для установки может потребоваться запуск EDT под правами администратора.<br>
Для самостоятельной сборки плагина необходимо иметь доступ к сайту https://releases.1c.ru и настроить соответствующим образом Maven. Подробности настройки написаны [здесь](https://github.com/1C-Company/dt-example-plugins/blob/master/simple-plugin/README.md).

### Возможности
Плагин добавляет поддержку префиксов (пока только намерения):
* Сортировка метаданных с учетом префиксов
* Проверка что нетиповые объекты и реквизиты в типовых объектах имеют префиксы
* Проверка что код с комментарием о доработке содержит префиксы