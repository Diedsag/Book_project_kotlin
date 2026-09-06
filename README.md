#  Кот-учёный — Приложение для чтения книг с заметками

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)

> Android-приложение для чтения PDF-книг с системой заметок, авторизацией и offline-first хранением.



##  Основные возможности

-  **Чтение PDF-книг** с сохранением форматирования (iTextPdf)
-  **Система заметок** с привязкой к страницам и полнотекстовым поиском
-  **JWT-авторизация** с автоматическим refresh токенов
-  **Offline-first** — все данные доступны без интернета
-  **Material3 дизайн** с dynamic theming
-  **Синхронизация** прогресса чтения и заметок

##  Архитектура

Проект построен на принципах **Clean Architecture** с разделением на слои:

├── presentation/ # UI (Compose), Navigation, ViewModel

├── domain/ # Usecases, бизнес-логика

└── data/ # RepositoryImpl, Room, iTextPdf

**Паттерны:**
- MVVM для UI-слоя
- Repository pattern для data-слоя
- Dependency Injection через Koin

**Ключевые решения:**
- Room для локального хранения с миграциями
- DataStore для токенов авторизации
- Coroutines + Flow для асинхронности

## 🛠️ Технологический стек

| Категория | Технологии |
|-----------|-----------|
| **Язык** | Kotlin |
| **UI** | Jetpack Compose, Material3 |
| **База данных** | Room (с миграциями) |
| **Асинхронность** | Coroutines, Flow |
| **PDF** | iTextPdf |
| **Хранение данных аккаунта** | DataStore Preferences |

##  Установка и запуск

### Требования
- Android Studio Hedgehog или новее
- JDK 11 или выше
- Android SDK (API 24+)

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
