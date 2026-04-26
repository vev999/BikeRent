# BikeRent

BikeRent to aplikacja mobilna na Androida do wypożyczania rowerów. Projekt jest napisany w Kotlinie, używa Jetpack Compose do interfejsu i zachowuje architekturę MVVM z Repository Pattern.

Aplikacja działa lokalnie na bazie Room. Zewnętrzny backend (np. AWS) jest planowany, ale jeszcze nie podłączony.

## Funkcje

- przeglądanie dostępnych rowerów z wyszukiwaniem i filtrowaniem,
- podgląd szczegółów roweru — karuzela zdjęć, opis, cena, ocena,
- podgląd profilu wypożyczalni,
- rejestracja i logowanie przez e-mail i hasło,
- hashowanie haseł SHA-256,
- wypożyczanie roweru i śledzenie czasu w czasie rzeczywistym,
- zwrot roweru — czas trwania i koszt obliczane na podstawie rzeczywistego czasu wypożyczenia,
- historia wypożyczeń z datą, czasem trwania i kosztem,
- dodawanie ocen i komentarzy do rowerów (1–5 gwiazdek),
- dynamicznie obliczana średnia ocena roweru,
- podgląd własnych ocen w profilu użytkownika,
- profil użytkownika z avatarem i statystykami,
- edycja danych konta,
- panel administratora z zarządzaniem rowerami i moderacją opinii,
- dodawanie nowych rowerów przez administratora (nazwa, opis, cena, kategoria, sklep, zdjęcie),
- wybór zdjęcia z urządzenia — plik jest kopiowany do lokalnego magazynu aplikacji,
- usuwanie opinii przez administratora z potwierdzeniem,
- dostęp do panelu administratora ograniczony tylko do konta admina,
- przycisk logowania Google jako nieaktywna zaślepka.

## Technologie

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose 2.9.0
- MVVM + Repository Pattern
- Room 2.8.4 (baza lokalna SQLite)
- KSP 2.3.2
- Kotlin Coroutines i StateFlow
- Coil 3.1.0 (ładowanie zdjęć — URL i lokalne pliki)
- Activity Compose 1.10.1 (file picker)
- Gradle Version Catalog

## Wymagania

- Android Studio Meerkat 2025.3.3 lub nowsze
- Android SDK z API 36
- JDK 21
- Android 7.0 (API 24) lub nowszy na emulatorze lub urządzeniu
- Dostęp do internetu przy pierwszym Gradle Sync do pobrania zależności

Projekt używa Gradle Wrappera — Gradle nie trzeba instalować ręcznie.

## Uruchomienie

1. Sklonuj repozytorium:

```bash
git clone <URL_REPOZYTORIUM>
cd BikeRent
```

2. Otwórz projekt w Android Studio.

3. Poczekaj na Gradle Sync.

4. Uruchom aplikację na emulatorze albo telefonie.

Alternatywnie z terminala:

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

- 6 rowerów (2 sklepy),
- 2 sklepy,
- konto administratora.

Konto administratora:

- e-mail: `admin@bikerent.local`
- hasło: `Admin123!`

Hasło w kodzie jest przechowywane wyłącznie jako hash SHA-256 w `DataSource.kt`.

Nie seedujemy zwykłych kont użytkowników, opinii ani historii wypożyczeń.

Użytkownicy tworzeni przez ekran rejestracji są zapisywani lokalnie w Room.

> **Uwaga:** Baza jest w wersji `2` i używa `fallbackToDestructiveMigration`. Jeśli aplikacja była uruchamiana wcześniej (wersja `1`), Room usunie starą bazę i utworzy nową. Trzeba zalogować się lub zarejestrować ponownie.

## Zdjęcia rowerów

Zdjęcia seedowanych rowerów są pobierane z URL-i Unsplash — wymaga połączenia z internetem.

Zdjęcia rowerów dodanych przez administratora są kopiowane z urządzenia do wewnętrznego magazynu aplikacji (`filesDir/bike_images/`) i ładowane lokalnie przez Coil bez dostępu do sieci.

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

Pełny opis warstw, bazy danych, repozytoriów i przepływów danych znajduje się w `PROJECT_STRUCTURE.md`.

## Architektura

```text
UI → ViewModel → Repository → DAO → Room
```

ViewModele nie odwołują się bezpośrednio do Room. Korzystają z interfejsów repozytoriów, dzięki czemu późniejsze przejście na zewnętrzne API powinno wymagać głównie wymiany implementacji repozytoriów.

## Lokalna baza danych

Nazwa bazy: `bikerent.db`  
Wersja: `2`

Tabele:

- `users`
- `bikes`
- `shops`
- `active_rentals`
- `rental_history`
- `reviews`

Hasła użytkowników są zapisywane w tabeli `users` jako `passwordHash` (SHA-256).

## Pliki lokalne i Git

Do repozytorium nie trafiają pliki generowane przez Android Studio i Gradle:

- `local.properties`
- `.gradle/`
- `.idea/`
- `.claude/`
- `app/build/`
- pliki `.apk` i `.aab`

Pliki te są ignorowane przez `.gitignore`.
