# BikeRent

BikeRent to aplikacja mobilna na Androida do wypożyczania rowerów. Projekt napisany w Kotlinie, używa Jetpack Compose do interfejsu i zachowuje architekturę MVVM z Repository Pattern.

Aplikacja działa lokalnie na bazie Room.

## Funkcje

- przeglądanie dostępnych rowerów z wyszukiwaniem i filtrowaniem po kategorii
- podgląd szczegółów roweru — karuzela zdjęć, opis, cena, ocena, film promocyjny (VideoView), dzwonek (MediaPlayer)
- podgląd profilu sklepu z dynamicznie obliczaną oceną na podstawie ocen jego rowerów
- rejestracja i logowanie przez e-mail i hasło (hasła przechowywane jako SHA-256)
- wypożyczanie roweru i śledzenie czasu w czasie rzeczywistym
- zwrot roweru — czas trwania i koszt obliczane na podstawie rzeczywistego czasu wypożyczenia
- historia wypożyczeń z datą, czasem trwania i kosztem
- dodawanie ocen i komentarzy do rowerów (1–5 gwiazdek)
- dynamicznie obliczana średnia ocena roweru
- przeglądanie profilu innych użytkowników przez kliknięcie na ich recenzję
- profil użytkownika z awatarem (wybór zdjęcia z urządzenia), statystykami i listą wystawionych ocen
- edycja danych konta
- panel administratora: zarządzanie rowerami, moderacja opinii
- dodawanie nowych rowerów przez administratora (nazwa, opis, cena, kategoria, sklep, zdjęcie z urządzenia)
- usuwanie opinii przez administratora z potwierdzeniem
- dostęp do panelu administratora ograniczony do konta admina
- animacja przejścia między ekranami — logo BikeRent z kręcącymi się kołami (500 ms)

## Technologie

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose 2.9.0
- MVVM + Repository Pattern
- Room 2.8.4 (lokalna baza SQLite)
- KSP 2.3.2
- Kotlin Coroutines i StateFlow
- Coil 3.1.0 (ładowanie zdjęć — drawable i lokalne pliki)
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

## Dane startowe

Przy pierwszym uruchomieniu aplikacja seeduje lokalną bazę Room:

- 12 rowerów w 4 sklepach
- 4 sklepy
- 3 konta użytkowników
- 10 startowych recenzji

**Konta:**

| E-mail | Hasło | Rola |
|--------|-------|------|
| `admin@bikerent.local` | `Admin123!` | Administrator |
| `anna.k@mail.com` | `rower2024` | Użytkownik |
| `pawel.n@mail.com` | `haslo123` | Użytkownik |

## Multimedia

Zasoby multimedialne (`app/src/main/res/raw/`):

- `bike_bell.mp3` — dzwonek roweru (~19 KB), odtwarzany przez `MediaPlayer` po naciśnięciu „Dzwonek" w szczegółach roweru.
- `bike_video.mp4` — film promocyjny (~846 KB), odtwarzany przez `VideoView` w sekcji „Film promocyjny". Film startuje wstrzymany — użytkownik klika play samodzielnie.

## Zdjęcia

Zdjęcia seedowanych rowerów i sklepów są bundlowane w APK jako zasoby drawable (`res/drawable-nodpi/*.jpg`) — działają bez połączenia z internetem.

Zdjęcia rowerów dodanych przez administratora są kopiowane z urządzenia do wewnętrznego magazynu aplikacji (`filesDir/bike_images/`) i ładowane lokalnie przez Coil.

## Struktura projektu i architektura

Opis warstw, ekranów, bazy danych, repozytoriów i przepływów danych: [ARCHITEKTURA.md](ARCHITEKTURA.md)

Szczegółowy opis tabel i seedowania bazy: [BAZA_DANYCH.md](BAZA_DANYCH.md)

## Lokalne pliki i Git

Do repozytorium nie trafiają pliki generowane przez Android Studio i Gradle:

- `local.properties`
- `.gradle/`
- `.idea/`
- `.claude/`
- `app/build/`
- pliki `.apk` i `.aab`

Pliki te są ignorowane przez `.gitignore`.
