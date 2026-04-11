# BikeRent

BikeRent to aplikacja mobilna na Androida do wypożyczania rowerów. Projekt jest napisany w Kotlinie, używa Jetpack Compose do interfejsu i zachowuje architekturę MVVM z Repository Pattern.

Aktualnie aplikacja działa lokalnie na bazie Room. Zewnętrzny backend AWS jest planowany, ale nie jest jeszcze podłączony.

## Funkcje

- przeglądanie dostępnych rowerów,
- podgląd szczegółów roweru,
- podgląd profilu wypożyczalni,
- rejestracja użytkownika przez e-mail i hasło,
- logowanie przez e-mail i hasło,
- lokalne przechowywanie użytkowników w Room,
- hashowanie haseł przez SHA-256,
- wypożyczanie i zwracanie rowerów,
- historia wypożyczeń,
- profil użytkownika,
- panel administratora,
- przycisk logowania Google jako nieaktywna zaślepka.

## Technologie

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- MVVM
- Repository Pattern
- Room
- KSP
- Kotlin Coroutines i StateFlow
- Coil
- Gradle Version Catalog

## Wymagania

- Android Studio Meerkat 2025.3.3 albo nowsze
- Android SDK z API 36 / 36.1
- JDK 21
- Android 7.0, API 24, albo nowszy na emulatorze lub urządzeniu
- Dostęp do internetu przy pierwszym Gradle Sync, żeby pobrać zależności

Projekt używa Gradle Wrappera, więc nie trzeba instalować Gradle ręcznie.

## Uruchomienie

1. Sklonuj repozytorium:

```bash
git clone <URL_REPOZYTORIUM>
cd BikeRent
```

2. Otwórz projekt w Android Studio.

3. Poczekaj na Gradle Sync.

4. Uruchom aplikację na emulatorze albo telefonie.

Alternatywnie można sprawdzić build z terminala:

```bash
./gradlew assembleDebug
```

Na Windowsie:

```bat
gradlew.bat assembleDebug
```

Testy jednostkowe:

```bash
./gradlew testDebugUnitTest
```

## Dane startowe

Przy pierwszym utworzeniu lokalnej bazy Room aplikacja seeduje:

- testowe rowery,
- testowe sklepy,
- konto administratora.

Konto administratora:

- e-mail: `admin@bikerent.local`
- hasło: `Admin123!`

Hasło administratora w kodzie nie jest przechowywane jako tekst jawny. W `DataSource.kt` zapisany jest hash SHA-256.

Nie seedujemy:

- zwykłych kont użytkowników,
- recenzji,
- komentarzy,
- przykładowej historii wypożyczeń zwykłych użytkowników.

Użytkownicy tworzeni przez ekran rejestracji zapisują się lokalnie w Room na urządzeniu osoby uruchamiającej aplikację.

Jeśli aplikacja była już wcześniej uruchamiana na emulatorze lub telefonie, lokalna baza mogła zachować stare dane. Wtedy trzeba odinstalować aplikację albo wyczyścić dane aplikacji, żeby seed wykonał się od nowa.

## Struktura projektu

```text
BikeRent/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/example/bikerent/
│       │   │   ├── BikeRentApp.kt
│       │   │   ├── MainActivity.kt
│       │   │   ├── data/
│       │   │   ├── navigation/
│       │   │   ├── ui/
│       │   │   └── viewmodel/
│       │   └── res/
│       ├── androidTest/
│       └── test/
├── gradle/
├── build.gradle.kts
├── gradle.properties
├── settings.gradle.kts
├── PROJECT_STRUCTURE.md
└── README.md
```

Pełny opis warstw, bazy danych, repozytoriów i przepływów znajduje się w `PROJECT_STRUCTURE.md`.

## Architektura

Projekt używa układu:

```text
UI -> ViewModel -> Repository -> DAO -> Room
```

ViewModele nie odwołują się bezpośrednio do Room. Korzystają z interfejsów repozytoriów, dzięki czemu późniejsze przejście z lokalnej bazy na AWS API powinno wymagać głównie wymiany implementacji repozytoriów.

## Lokalna baza danych

Baza Room ma nazwę:

```text
bikerent.db
```

Główne tabele:

- `users`
- `bikes`
- `shops`
- `active_rentals`
- `rental_history`

Hasła użytkowników są zapisywane w tabeli `users` jako `passwordHash`.

## Pliki lokalne i Git

Do repozytorium nie powinny trafiać pliki lokalne generowane przez Android Studio i Gradle, m.in.:

- `local.properties`
- `.gradle/`
- `.idea/`
- `.claude/`
- `app/build/`
- wygenerowane pliki `.apk` i `.aab`

Te pliki są ignorowane przez `.gitignore`.

