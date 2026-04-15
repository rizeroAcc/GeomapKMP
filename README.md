# GeomapKMP 🗺️

**Kotlin Multiplatform** приложение для работы с геопроектами и картами.

Кросс-платформенное приложение на **Compose Multiplatform**, которое позволяет авторизоваться, выбирать проекты и работать с географическими картами на **Android**, **iOS** и **Desktop**.

## ✨ Возможности

- Полностью общая кодовая база благодаря **Kotlin Multiplatform**
- Современный UI на **Compose Multiplatform**
- Модульная архитектура по фичам (Feature Modularization)
- Авторизация и регистрация пользователей
- Просмотр и выбор проектов
- Интерактивная карта
- Профиль пользователя

## 🛠 Технологический стек

- **Kotlin Multiplatform**
- **Compose Multiplatform** (Android, iOS, Desktop)
- **Modular Architecture** (feature + shared-core модули)
- **Decompose**
- **MVIKotlin**
- **Ktor**
- **Koin**
- **Room**


### Модули проекта

| Модуль                        | Назначение |
|------------------------------|----------|
| `composeApp`                 | Общий Compose UI и точка входа |
| `androidApp`                 | Android-entry point |
| `feature-authorization`      | Авторизация |
| `feature-registration`       | Регистрация |
| `feature-project-select`     | Выбор проекта |
| `feature-project-mapview`    | Просмотр карты проектов |
| `feature-user-profile`       | Профиль пользователя |
| `shared-core-*`              | Общие слои (data, network, database, utils, components) |
