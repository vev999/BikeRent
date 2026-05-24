# BikeRent - struktura projektu

Ten dokument opisuje aktualną strukturę aplikacji BikeRent: warstwy projektu, sposób przechowywania danych, lokalny backend, bazę danych, frontend w Jetpack Compose oraz najważniejsze pliki.

## 1. Cel aplikacji

BikeRent to aplikacja mobilna na Androida do wypożyczania rowerów. Aplikacja jest napisana w Kotlinie, używa Jetpack Compose do UI i zachowuje architekturę MVVM z Repository Pattern.

Aktualnie aplikacja działa lokalnie:

- dane użytkowników, rowerów, sklepów, wypożyczeń i opinii są przechowywane w lokalnej bazie Room,
- zdjęcia dodanych przez administratora rowerów są kopiowane do wewnętrznego magazynu aplikacji,
- początkowe dane rowerów, sklepów i konta administratora są seedowane z `DataSource.kt`,
- nie ma jeszcze zewnętrznego backendu AWS,
- kod jest przygotowany tak, żeby później można było wymienić implementacje repozytoriów na wywołania API.

## 2. Najważniejsze technologie

- Kotlin 2.2.10 - główny język aplikacji.
- Jetpack Compose (BOM 2026.02.01) - warstwa interfejsu użytkownika.
- Material 3 - komponenty UI.
- Navigation Compose 2.9.0 - nawigacja między ekranami.
- MVVM - podział na UI, ViewModel i warstwę danych.
- Repository Pattern - ekrany i ViewModele nie odwołują się bezpośrednio do Room.
- Room 2.8.4 - lokalna baza SQLite.
- KSP 2.3.2 - generowanie kodu dla Room.
- Kotlin Coroutines i StateFlow - obsługa stanu i operacji asynchronicznych.
- Coil 3.1.0 - ładowanie obrazów z URL i z lokalnych plików na urządzeniu.
- Activity Compose 1.10.1 - file picker do wyboru zdjęć z urządzenia.

## 3. Struktura katalogów

```text
BikeRent/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
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
│       │       ├── drawable/
│       │       │   ├── ic_launcher_background.xml
│       │       │   └── ic_launcher_foreground.xml
│       │       ├── mipmap-*/
│       │       ├── raw/
│       │       │   ├── bike_bell.mp3
│       │       │   └── bike_video.mp4
│       │       ├── values/
│       │       └── xml/
│       ├── androidTest/
│       └── test/
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/
├── build.gradle.kts
├── gradle.properties
├── settings.gradle.kts
├── ARCHITEKTURA.md
├── BAZA_DANYCH.md
├── PROJECT_STRUCTURE.md
└── README.md
```

## 4. Punkt startowy aplikacji

### `MainActivity.kt`

Główna aktywność Androida. Uruchamia motyw aplikacji i `AppNavigation`.

### `BikeRentApp.kt`

Klasa `Application`, ustawiona w `AndroidManifest.xml` jako:

```xml
android:name=".BikeRentApp"
```

Odpowiada za utworzenie singletona bazy danych i repozytoriów:

- `BikeRentDatabase`
- `UserRepositoryImpl`
- `BikeRepositoryImpl`
- `ShopRepositoryImpl`
- `RentalRepositoryImpl`
- `ReviewRepositoryImpl`

To jest prosta forma ręcznego dependency injection. ViewModele dostają repozytoria przez fabryki `ViewModelProvider.Factory`.

## 5. Frontend - Jetpack Compose

Warstwa UI znajduje się w:

```text
app/src/main/java/com/example/bikerent/ui/
```

### Ekrany

```text
ui/screens/
├── LoginScreen.kt
├── HomeScreen.kt
├── BikeDetailScreen.kt
├── ShopProfileScreen.kt
├── RentalsScreen.kt
├── ProfileScreen.kt
├── UserSettingsScreen.kt
├── MyReviewsScreen.kt
└── AdminPanelScreen.kt
```

Opis:

- `LoginScreen.kt` - ekran logowania i rejestracji. Logo: `Icons.Filled.DirectionsBike` (60 dp, zielony) + napis „BikeRent". Zakładki Logowanie / Rejestracja.
- `HomeScreen.kt` - ekran główny z listą rowerów, wyszukiwarką i filtrowaniem po kategorii.
- `BikeDetailScreen.kt` - szczegóły roweru, karuzela zdjęć (HorizontalPager), dzwonek (MediaPlayer), film (VideoView, startuje paused), lista opinii z dynamiczną średnią, formularz dodawania opinii, akcja wypożyczenia.
- `ShopProfileScreen.kt` - profil wypożyczalni z listą dostępnych rowerów.
- `RentalsScreen.kt` - aktywne wypożyczenia z licznikiem czasu i historia wypożyczeń. Używa `ScreenHeader`.
- `ProfileScreen.kt` - profil użytkownika z awatarem (AsyncImage lub zielone kółko z inicjałem). Dostęp do panelu administratora tylko dla konta admina. Używa `ScreenHeader`.
- `UserSettingsScreen.kt` - edycja danych konta, zmiana awatara przez file picker (`GetContent`). Używa `ScreenHeader`.
- `MyReviewsScreen.kt` - lista wszystkich opinii wystawionych przez zalogowanego użytkownika. Używa `ScreenHeader`.
- `AdminPanelScreen.kt` - panel administratora: dodawanie rowerów (formularz z file pickerem) i moderacja opinii z możliwością usuwania.

### Komponenty

```text
ui/components/
├── BottomNavBar.kt
├── ScreenHeader.kt
└── BikeTransitionOverlay.kt
```

- `BottomNavBar.kt` - dolna nawigacja między głównymi ekranami (Home, Wypożyczenia, Profil, Ustawienia).
- `ScreenHeader.kt` - wspólny zielony nagłówek używany przez Profile, Rentals, UserSettings, MyReviews. Zapewnia jednolitą wysokość na wszystkich ekranach. Ekrany bez przycisku wstecz dostają `Spacer(52.dp)`.
- `BikeTransitionOverlay.kt` - semi-transparentny overlay wyświetlany 500 ms przy każdej zmianie ekranu. Wyświetla `Icons.Filled.DirectionsBike` (80 dp, zielony) z animowanymi spinning kołami (Canvas, 6 szprych/koło, 700 ms/obrót) i napisem „BikeRent".

### Motyw

```text
ui/theme/
├── Color.kt
├── Theme.kt
└── Type.kt
```

Zawiera kolory (`Green800 = #2E7D32`, `Green100`), typografię i konfigurację motywu Compose.

## 6. Nawigacja

Nawigacja znajduje się w:

```text
app/src/main/java/com/example/bikerent/navigation/AppNavigation.kt
```

Zdefiniowane trasy (sealed class `Screen`):

- `login`
- `home`
- `bike/{bikeId}`
- `shop/{shopId}`
- `rentals`
- `profile`
- `settings`
- `my_reviews`
- `admin`

`NavHost` jest opakowany w `Box`. `BikeTransitionOverlay` jest renderowany nad `NavHost` — przy każdej zmianie `currentBackStackEntry` overlay pojawia się na 500 ms (fadeIn 80 ms / fadeOut 180 ms).

Aplikacja startuje od ekranu `login`. Po udanym logowaniu `AuthViewModel` ustawia `AuthState.Success`, a `AppNavigation` inicjalizuje `AppViewModel` z `userId` i `name`.

## 7. ViewModel

```text
app/src/main/java/com/example/bikerent/viewmodel/
├── AuthViewModel.kt
└── AppViewModel.kt
```

### `AuthViewModel.kt`

Odpowiada za:

- logowanie i rejestrację,
- wylogowanie,
- aktualizację danych użytkownika,
- zarządzanie awatarem użytkownika (`currentAvatarUri`, `updateAvatarUri`),
- trzymanie aktualnej sesji w pamięci,
- sprawdzanie czy zalogowany użytkownik jest administratorem (`isAdmin`).

Stan autoryzacji (`AuthState`): `Idle` / `Loading` / `Success(userId, name, email)` / `Error(message)`.

Hasło jest hashowane przez `HashUtils.sha256` przed porównaniem z bazą. `isAdmin` porównuje e-mail z `DataSource.seededAdminUsers`.

### `AppViewModel.kt`

Odpowiada za główne dane aplikacji:

- lista rowerów, sklepów, aktywnych wypożyczeń, historii wypożyczeń,
- opinie: dla wybranego roweru, zalogowanego użytkownika, wszystkie (admin),
- wypożyczanie i zwrot roweru,
- dodawanie i usuwanie opinii (z automatyczną aktualizacją średniej oceny roweru),
- dodawanie nowych rowerów przez administratora.

## 8. Backend

Aktualnie nie ma zewnętrznego backendu. Rola backendu: lokalna baza Room.

```text
Room Database → DAO → RepositoryImpl → Repository interface → ViewModel → UI
```

Dzięki Repository Pattern przyszła integracja z AWS może polegać na wymianie `*RepositoryImpl` bez zmian w ViewModelach i ekranach.

## 9. Warstwa danych

```text
app/src/main/java/com/example/bikerent/data/
├── DataSource.kt
├── Models.kt
├── db/
├── repository/
└── util/
    ├── HashUtils.kt
    └── ImageUtils.kt
```

### `Models.kt`

Modele domenowe (niezależne od Room):
- `Bike`, `Shop`, `ActiveRental`, `RentalHistory`, `Review`, `SeedUser`

### `DataSource.kt`

Dane startowe:
- 12 rowerów (wszystkie `available = true`), 4 sklepy, 1 konto administratora.

Seedowane przy każdym otwarciu bazy (`IGNORE` conflict strategy — nie nadpisuje).

### `util/ImageUtils.kt`

- `copyToAppStorage(context, uri)` — kopiuje plik do `filesDir/bike_images/`.
- `imageModel(path)` — `File(path)` dla ścieżek lokalnych, `String` dla URL-i.

## 10. Baza danych Room

```text
app/src/main/java/com/example/bikerent/data/db/
├── BikeRentDatabase.kt       ← version=3, fallbackToDestructiveMigration
├── converter/Converters.kt   ← List<String> ↔ String (separator "|")
├── dao/
│   ├── UserDao.kt            ← + updateAvatarUri (v3)
│   ├── BikeDao.kt
│   ├── ShopDao.kt
│   ├── ActiveRentalDao.kt
│   ├── RentalHistoryDao.kt
│   └── ReviewDao.kt
└── entity/
    ├── UserEntity.kt         ← + avatarUri: String? (v3)
    ├── BikeEntity.kt
    ├── ShopEntity.kt
    ├── ActiveRentalEntity.kt
    ├── RentalHistoryEntity.kt
    └── ReviewEntity.kt
```

**Wersja bazy: 3** (`fallbackToDestructiveMigration` — przy zmianie wersji dane są kasowane i seedowane od nowa).

Historia wersji:
- v1: users, bikes, shops, active_rentals, rental_history
- v2: dodano tabelę `reviews`
- v3: dodano kolumnę `avatarUri TEXT NULL` w tabeli `users`

Tabele: `users`, `bikes`, `shops`, `active_rentals`, `rental_history`, `reviews`.

`Converters` serializuje `List<String>` do TEXT rozdzielonego `|` — używane dla `bikes.images` i `shops.bikeIds`.

## 11. Repozytoria

```text
data/repository/
├── UserRepository.kt         ← + updateAvatarUri
├── BikeRepository.kt
├── ShopRepository.kt
├── RentalRepository.kt
├── ReviewRepository.kt
└── impl/
    ├── UserRepositoryImpl.kt ← + updateAvatarUri
    ├── BikeRepositoryImpl.kt
    ├── ShopRepositoryImpl.kt
    ├── RentalRepositoryImpl.kt
    └── ReviewRepositoryImpl.kt
```

Każda implementacja mapuje encje Room na modele domenowe (`Entity.toDomain()`).

`RentalRepositoryImpl.returnBike` — oblicza czas trwania i koszt (`cena × ceil(godziny)`, min. 1 h), przenosi wpis z `active_rentals` do `rental_history`.

## 12. Autoryzacja

Przepływ logowania:
```
LoginScreen → AuthViewModel.login → HashUtils.sha256 → UserRepository.login
    → UserDao.findByEmailAndPassword → Room
    → currentAvatarUri = user.avatarUri
```

Przepływ rejestracji:
```
LoginScreen → AuthViewModel.register → HashUtils.sha256 → UserRepository.register
    → UserDao.insert → Room
```

Hasła przechowywane wyłącznie jako SHA-256 w kolumnie `passwordHash`.

Administrator: konto seedowane z `DataSource` (`admin@bikerent.local`). `isAdmin` sprawdza e-mail przy logowaniu.

Sesja: trzymana w pamięci `AuthViewModel`. Po restarcie aplikacji wymagane ponowne logowanie.

## 13. Awatar użytkownika

Przepływ ustawiania awatara:
```
UserSettingsScreen → launcher GetContent() → URI zdjęcia
    → ImageUtils.copyToAppStorage → filesDir/bike_images/ścieżka.jpg
    → authViewModel.updateAvatarUri(ścieżka)
    → UserRepository.updateAvatarUri → UserDao.updateAvatarUri → users.avatarUri
```

Wyświetlanie awatara (ProfileScreen, UserSettingsScreen):
- jeśli `currentAvatarUri != null` → `AsyncImage(File(uri))` (Coil, kółko 96 dp)
- jeśli null → zielone kółko z inicjałem imienia

## 14. Multimedia (`res/raw/`)

- `bike_bell.mp3` (~19 KB, Wikimedia Commons) — `MediaPlayer.create(context, R.raw.bike_bell)`, odtwarzany na kliknięcie „Dzwonek" w BikeDetailScreen, zwalniany przez `setOnCompletionListener { release() }`.
- `bike_video.mp4` (~846 KB, Wikimedia Commons) — `VideoView` + `MediaController`, film startuje **wstrzymany** (brak `setOnPreparedListener { start() }`).

## 15. Ikonka aplikacji

```text
res/drawable/
├── ic_launcher_background.xml   ← białe tło #FFFFFF
└── ic_launcher_foreground.xml   ← zielony kolarz #2E7D32

res/mipmap-anydpi-v26/
├── ic_launcher.xml              ← adaptive icon
└── ic_launcher_round.xml        ← adaptive icon (okrągła)
```

Foreground: ścieżki skopiowane z `Icons.Filled.DirectionsBike` (Material Icons library, 24×24 viewport), `fillType="evenOdd"` tworzy pierścienie kół. `<group scaleX="3" scaleY="3" translateX="18" translateY="18">` — skaluje ikonę do safe zone 18–90 w 108×108 dp.

## 16. Wypożyczanie i zwrot

Przepływ wypożyczenia:
```
BikeDetailScreen → appViewModel.rentBike(bike, shop)
    → RentalRepository.addRental → ActiveRentalDao.insert → active_rentals
```

Przepływ zwrotu:
```
RentalsScreen → appViewModel.returnBike(rental)
    → RentalRepository.returnBike
    → ActiveRentalDao.deleteById
    → RentalHistoryDao.insert (z obliczonym czasem i kosztem)
```

## 17. Opinie i oceny

Przepływ dodawania:
```
BikeDetailScreen → appViewModel.addReview → ReviewRepository.addReview
    → ReviewDao.insert
    → pobierz wszystkie opinie roweru → oblicz średnią → BikeDao.updateRating
```

Jeden użytkownik = jedna opinia na rower (sprawdzane przez `findByUserAndBike`). Po wystawieniu formularz znika.

Administrator może usunąć dowolną opinię z `AdminPanelScreen`. Po usunięciu średnia ocena roweru jest przeliczana automatycznie.

## 18. Dodawanie rowerów przez administratora

1. Dialog w `AdminPanelScreen` — formularz (nazwa, opis, kategoria, cena, sklep).
2. File picker (`GetContent()`) — wybór zdjęcia z urządzenia.
3. `ImageUtils.copyToAppStorage` — kopia do `filesDir/bike_images/`.
4. `AppViewModel.addBike` → `BikeRepository.addBike` → `BikeDao.insert`.
5. Rower natychmiast widoczny w HomeScreen i ShopProfileScreen.

## 19. Konfiguracja Gradle

```text
settings.gradle.kts
build.gradle.kts
app/build.gradle.kts
gradle/libs.versions.toml
gradle.properties
```

Istotne wersje (`libs.versions.toml`):

| Biblioteka | Wersja |
|-----------|--------|
| Android Gradle Plugin | 9.1.0 |
| Kotlin | 2.2.10 |
| KSP | 2.3.2 |
| Room | 2.8.4 |
| Coil | 3.1.0 |
| Navigation Compose | 2.9.0 |
| Activity Compose | 1.10.1 |
| Compose BOM | 2026.02.01 |

`gradle.properties` zawiera `android.disallowKotlinSourceSets=false` — flaga kompatybilności AGP/Kotlin/KSP.

## 20. Aktualne ograniczenia

- Brak zewnętrznego backendu AWS.
- Brak trwałego zapamiętywania sesji po restarcie aplikacji.
- Baza używa `fallbackToDestructiveMigration` — przy zmianie schematu dane są kasowane.
- SHA-256 spełnia wymaganie projektu; w produkcji lepszy byłby bcrypt/scrypt/Argon2 z solą.
