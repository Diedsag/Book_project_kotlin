#  Кот-учёный — Android-приложение для чтения книг с заметками

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Room](https://img.shields.io/badge/Room-FFCA28?style=for-the-badge&logo=sqlite&logoColor=black)

> Android-приложение для чтения PDF-книг с системой заметок, авторизацией и offline-first хранением.

##  Основные возможности

-  **Чтение PDF-книг** с сохранением форматирования (iTextPdf)
-  **Система заметок** с привязкой к страницам
-  **Авторизация** с хранением данных о теме и языке в DataStore
-  **Offline-first** — все данные доступны без интернета
-  **Material3 дизайн** в Jetpack Compose

##  Архитектура

Проект построен на принципах **Clean Architecture** с разделением на три слоя:

- **Presentation** — UI (Jetpack Compose), ViewModels, навигация
- **Domain** — Use cases и бизнес-логика (не зависит от Android SDK)
- **Data** — Repository-реализации, Room, работа с файлами через iTextPdf

**Паттерны:**
- MVVM для presentation-слоя
- Repository pattern для data-слоя
- Dependency Injection через Koin

##  Технологический стек

| Категория | Технологии |
|-----------|-----------|
| **Язык** | Kotlin |
| **UI** | Jetpack Compose, Material3 |
| **База данных** | Room |
| **Хранение токенов** | DataStore Preferences |
| **PDF** | iTextPdf |
| **DI** | Koin |
| **Асинхронность** | Coroutines, Flow |

##  Структура проекта

```
app/src/main/java/com/example/mainprojectkt
├── domain/
│   ├── model/            # Data classes
│   ├── repository/       # Интерфейс репозитория
│   └── usecase/          # Use cases
├── data/
│   ├── local/            # Room database, DAOs, iTextPdf стратегия
│   └── repository/       # Реализация репозиториев
└── presentation/
    ├── navigation/       # Навигация
    ├── theme/            # Оформление
    ├── ui/     
    │   ├── component/    # Компоненты
    │   └── screen/       # Экраны
    └── viewmodel/        # ViewModel
```

##  Установка и запуск

### Требования
- Android Studio Koala или новее
- JDK 11+
- Android SDK (minSdk 24)

### Шаги
```bash
# 1. Клонируй репозиторий
git clone https://github.com/Diedsag/Book_project_kotlin.git

# 2. Открой в Android Studio
# File → Open → выбери папку проекта

# 3. Синхронизируй Gradle
# File → Sync Project with Gradle Files

# 4. Запусти на эмуляторе или устройстве
# Run → Run 'app'
```

##  Планы по развитию (TODO)

- [ ]  **Unit-тесты** для use cases (JUnit 5 + MockK)
- [ ]  **UI-тесты** с Compose Test для критичных экранов
- [ ]  **Экспорт заметок** в PDF / Markdown
- [ ]  **Поддержка EPUB** формата
- [ ]  **Интеграция с облачным хранилищем** (Google Drive / Яндекс Диск)
- [ ]  **Статистика чтения** (время, количество страниц)
