# BikeRent – Architektura i struktura projektu

## 1. Opis aplikacji

BikeRent to aplikacja mobilna na Androida do wypożyczania rowerów. Napisana w Kotlinie, używa Jetpack Compose do interfejsu i zachowuje architekturę MVVM z Repository Pattern.

Aplikacja działa w pełni lokalnie – dane użytkowników, rowerów, sklepów, wypożyczeń i opinii są przechowywane w lokalnej bazie Room. Zdjęcia rowerów i sklepów są zasobami bundlowanymi w APK (`drawable-nodpi`). Zdjęcia rowerów dodanych przez administratora są kopiowane do wewnętrznego magazynu aplikacji. Kod jest zorganizowany tak, żeby przyszła wymiana implementacji repozytoriów na wywołania API nie wymagała zmian w ViewModelach ani ekranach.

---

## 2. Technologie

| Technologia | Wersja | Rola |
|------------|--------|------|
| Kotlin | 2.2.10 | Główny język aplikacji |
| Jetpack Compose BOM | 2026.02.01 | Warstwa UI |
| Material 3 | — | Komponenty UI |
| Navigation Compose | 2.9.0 | Nawigacja między ekranami |
| Room | 2.8.4 | Lokalna baza SQLite |
| KSP | 2.3.2 | Generowanie kodu dla Room |
| Coil | 3.1.0 | Ładowanie obrazów (drawable, pliki lokalne) |
| Kotlin Coroutines + StateFlow | — | Asynchroniczność i reaktywny stan |
| Activity Compose | 1.10.1 | File picker (wybór zdjęć) |
| Android Gradle Plugin | 9.1.0 | Build system |

---

## 3. Architektura ogólna: MVVM + Repository Pattern

```
UI (Screens / Composables)
    ↓ obserwuje StateFlow
ViewModel (AppViewModel / AuthViewModel)
    ↓ wywołuje suspend fun
Repository (interfejs → implementacja)
    ↓ operacje na encjach
DAO (Room)
    ↓ SQL
SQLite (plik bikerent.db)
```

Ekrany nie znają Room. ViewModele nie znają szczegółów bazy. Repozytoria mapują encje Room na modele domenowe (`Entity.toDomain()`).

---

## 4. Struktura katalogów

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
│       │   │   │   ├── DataSource.kt
│       │   │   │   ├── Models.kt
│       │   │   │   ├── db/
│       │   │   │   │   ├── BikeRentDatabase.kt
│       │   │   │   │   ├── converter/Converters.kt
│       │   │   │   │   ├── dao/
│       │   │   │   │   └── entity/
│       │   │   │   ├── repository/
│       │   │   │   │   ├── *Repository.kt        (interfejsy)
│       │   │   │   │   └── impl/*RepositoryImpl.kt
│       │   │   │   └── util/
│       │   │   │       ├── HashUtils.kt
│       │   │   │       └── ImageUtils.kt
│       │   │   ├── navigation/
│       │   │   │   └── AppNavigation.kt
│       │   │   ├── ui/
│       │   │   │   ├── components/
│       │   │   │   │   ├── BottomNavBar.kt
│       │   │   │   │   ├── ScreenHeader.kt
│       │   │   │   │   └── BikeTransitionOverlay.kt
│       │   │   │   ├── screens/
│       │   │   │   │   ├── LoginScreen.kt
│       │   │   │   │   ├── HomeScreen.kt
│       │   │   │   │   ├── BikeDetailScreen.kt
│       │   │   │   │   ├── ShopProfileScreen.kt
│       │   │   │   │   ├── RentalsScreen.kt
│       │   │   │   │   ├── ProfileScreen.kt
│       │   │   │   │   ├── UserSettingsScreen.kt
│       │   │   │   │   ├── MyReviewsScreen.kt
│       │   │   │   │   ├── UserProfileScreen.kt
│       │   │   │   │   └── AdminPanelScreen.kt
│       │   │   │   └── theme/
│       │   │   │       ├── Color.kt
│       │   │   │       ├── Theme.kt
│       │   │   │       └── Type.kt
│       │   │   └── viewmodel/
│       │   │       ├── AuthViewModel.kt
│       │   │       └── AppViewModel.kt
│       │   └── res/
│       │       ├── drawable/
│       │       │   ├── ic_launcher_background.xml
│       │       │   └── ic_launcher_foreground.xml
│       │       ├── drawable-nodpi/
│       │       │   └── *.jpg  (zdjęcia rowerów i sklepów)
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
└── README.md
```

---

## 5. Punkt wejścia aplikacji

### `MainActivity.kt`
Jedyna aktywność (single-activity architecture). Ustawia Jetpack Compose jako UI, aplikuje `BikeRentTheme`, wywołuje `AppNavigation()`.

### `BikeRentApp.kt`
Rozszerza `Application` – uruchamia się przed `MainActivity`. Tworzy singleton bazy danych i wszystkie repozytoria (lazy). ViewModele dostają repozytoria przez fabryki `ViewModelProvider.Factory` używające `LocalContext.current.applicationContext as BikeRentApp`.

---

## 6. Nawigacja

### `navigation/AppNavigation.kt`

**`sealed class Screen`** – lista tras:

| Obiekt | Trasa | Ekran |
|--------|-------|-------|
| `Login` | `login` | Logowanie / rejestracja |
| `Home` | `home` | Lista rowerów |
| `BikeDetail` | `bike/{bikeId}` | Szczegóły roweru |
| `ShopProfile` | `shop/{shopId}` | Profil sklepu |
| `Rentals` | `rentals` | Aktywne i historia wypożyczeń |
| `Profile` | `profile` | Profil użytkownika |
| `Settings` | `settings` | Ustawienia konta |
| `Admin` | `admin` | Panel administracyjny |
| `MyReviews` | `my_reviews` | Moje recenzje |
| `UserProfile` | `user_profile/{userId}` | Profil innego użytkownika |

**`@Composable fun AppNavigation()`** – tworzy `NavHostController`, instancje obu ViewModeli, podpina każdy ekran do trasy. Nasłuchuje `authState` – po zalogowaniu wywołuje `appViewModel.initForUser(userId, name)`.

`NavHost` jest opakowany w `Box`. Nad nim renderowany jest `BikeTransitionOverlay` – overlay widoczny przez 500 ms przy każdej zmianie ekranu.

---

## 7. ViewModele

### `AuthViewModel.kt`

Odpowiada za logowanie i rejestrację.

**Stan (`authState: StateFlow<AuthState>`):**
- `Idle` – brak akcji
- `Loading` – trwa zapytanie
- `Success(userId, name, email)` – zalogowano
- `Error(message)` – błąd

**Kluczowe metody:**
- `login(email, password)` – hashuje hasło SHA-256, szuka użytkownika w bazie, ładuje `currentAvatarUri`
- `register(name, email, password, confirmPassword)` – waliduje pola, hashuje, tworzy konto
- `logout()` – resetuje stan do `Idle`, czyści dane sesji
- `updateUserData(name, email)` – zmiana danych profilu
- `updateAvatarUri(uri)` – zapisuje URI awatara do Room i aktualizuje `currentAvatarUri`
- `isAdmin` – sprawdza czy e-mail jest na liście adminów z `DataSource`

---

### `AppViewModel.kt`

Główny ViewModel aplikacji. Przechowuje cały stan UI po zalogowaniu.

**StateFlow'y:**

| Pole | Typ | Zawartość |
|------|-----|-----------|
| `bikes` | `List<Bike>` | Wszystkie rowery |
| `shops` | `List<Shop>` | Wszystkie sklepy |
| `activeRentals` | `List<ActiveRental>` | Aktywne wypożyczenia zalogowanego użytkownika |
| `rentalHistory` | `List<RentalHistory>` | Historia wypożyczeń |
| `currentBikeReviews` | `List<Review>` | Recenzje aktualnie przeglądanego roweru |
| `userReviews` | `List<Review>` | Recenzje zalogowanego użytkownika |
| `allReviews` | `List<Review>` | Wszystkie recenzje (dla admina) |
| `viewedUserReviews` | `List<Review>` | Recenzje przeglądanego profilu użytkownika |

**Kluczowe metody:**
- `initForUser(userId, userName)` – wołane po zalogowaniu, ładuje dane z bazy
- `rentBike(bike, shop)` – tworzy `ActiveRental` i zapisuje
- `returnBike(rental)` – usuwa z `active_rentals`, oblicza czas i koszt, wpisuje do `rental_history`
- `addBike(...)` – admin dodaje nowy rower
- `addReview(bikeId, bikeName, rating, comment)` – dodaje recenzję i aktualizuje ocenę roweru
- `deleteReview(review)` – admin usuwa recenzję, przelicza ocenę roweru
- `loadBikeReviews(bikeId)` – ładuje recenzje dla wybranego roweru
- `loadAllReviews()` – ładuje wszystkie recenzje (panel admina)
- `loadReviewsForUser(userId)` – ładuje recenzje dla przeglądanego profilu użytkownika

---

## 8. Ekrany UI

### `LoginScreen.kt`
Formularz logowania i rejestracji (zakładki). Zielony gradient jako tło, biała karta na środku. Obserwuje `authState` – po `Success` nawiguje do `Home`. Walidacja po stronie ViewModelu.

### `HomeScreen.kt`
Lista rowerów z `appViewModel.bikes`. Wyszukiwanie po nazwie i filtrowanie po kategorii. Kliknięcie → `BikeDetail`. Zawiera `BottomNavBar`.

### `BikeDetailScreen.kt`
- Karuzela zdjęć (`HorizontalPager` + wskaźnik strony)
- Przycisk „Dzwonek" → `MediaPlayer.create(context, R.raw.bike_bell)`
- Sekcja „Film promocyjny" → `VideoView` z `R.raw.bike_video`; film startuje wstrzymany
- Przycisk „Sklep" → `ShopProfile`
- Przycisk „Wypożycz" → `appViewModel.rentBike(...)`
- Lista recenzji (`currentBikeReviews`) z dynamiczną średnią + formularz dodawania recenzji (ukryty po wystawieniu)
- Kliknięcie na recenzję → `UserProfile` autora

### `ShopProfileScreen.kt`
Informacje o sklepie (nazwa, opis, lokalizacja). Ocena sklepu obliczana dynamicznie jako średnia ocen rowerów sklepu z co najmniej jedną recenzją. Lista rowerów należących do sklepu.

### `RentalsScreen.kt`
Dwie sekcje: aktywne wypożyczenia (z licznikiem czasu) i historia. Przycisk „Zwróć" → `appViewModel.returnBike(...)`. Używa `ScreenHeader`.

### `ProfileScreen.kt`
Awatar (AsyncImage lub zielone kółko z inicjałem), dane użytkownika, statystyki (liczba wypożyczeń, średnia wystawionych ocen). Menu nawigacyjne. Dla admina – pozycja „Panel administracyjny". Przycisk wylogowania.

### `UserSettingsScreen.kt`
Awatar z możliwością zmiany (file picker). Edycja imienia i e-maila. Zdjęcie kopiowane do `filesDir/bike_images/`. Używa `ScreenHeader`.

### `UserProfileScreen.kt`
Profil innego użytkownika (dostępny po kliknięciu na recenzję). Wyświetla awatar (inicjał), imię, liczbę recenzji, średnią ocenę oraz listę wystawionych recenzji. Używa `ScreenHeader` z przyciskiem wstecz.

### `MyReviewsScreen.kt`
Lista recenzji wystawionych przez zalogowanego użytkownika. Używa `ScreenHeader`.

### `AdminPanelScreen.kt`
Widoczny tylko dla adminów. Karty statystyk (rowery, sklepy, opinie). Pełna lista wszystkich rowerów z możliwością dodawania nowych (formularz: nazwa, opis, cena, kategoria, sklep, zdjęcie z file pickera). Moderacja opinii z możliwością usuwania.

---

## 9. Komponenty UI

### `BottomNavBar.kt`
Dolny pasek nawigacyjny z 4 pozycjami: Home, Wypożyczenia, Profil, Ustawienia. Podświetla aktywny ekran. Używa `navigate()` z `popUpTo` żeby nie stackować ekranów.

### `ScreenHeader.kt`
Wspólny zielony nagłówek (Profile, Rentals, UserSettings, MyReviews, UserProfile). Zapewnia jednolitą wysokość. Ekrany bez przycisku wstecz dostają `Spacer(52.dp)` zamiast `IconButton`.

### `BikeTransitionOverlay.kt`
Semi-transparentny biały overlay (alpha 0.96) wyświetlany 500 ms przy każdej zmianie ekranu. `AnimatedVisibility` z `fadeIn(80ms)` / `fadeOut(180ms)`. Zawiera `Icons.Filled.DirectionsBike` (80 dp, zielony) ze `SpinningWheelsOverlay` (Canvas: 6 szprych na każdym kole, obrót co 700 ms) i napisem „BikeRent".

### `ui/theme/`
- `Color.kt` – kolory: `Green800 = #2E7D32`, `Green100`, `Red700`, `Blue700`, itd.
- `Theme.kt` – `BikeRentTheme` (Material 3)
- `Type.kt` – typografia

---

## 10. Warstwa danych

### `data/Models.kt`
Czyste klasy domenowe (bez zależności od Room):
- `Bike` – rower (id, name, price, rating, image, images, description, available, shopId, category)
- `Shop` – sklep (id, name, description, location, rating, image, bikeIds)
- `ActiveRental` – aktywne wypożyczenie
- `RentalHistory` – zakończone wypożyczenie
- `Review` – recenzja (id, bikeId, bikeName, userId, userName, rating, comment, date)
- `SeedUser` – predefiniowany użytkownik do seedowania

### `data/DataSource.kt`
Singleton z danymi startowymi:
- `bikes` – 12 rowerów z referencjami do lokalnych zasobów drawable
- `shops` – 4 sklepy z referencjami do lokalnych zasobów drawable
- `seededAdminUsers` – konto admina z hashem hasła
- `seededRegularUsers` – Anna Kowalska i Paweł Nowak z hashami haseł

### `data/util/HashUtils.kt`
`sha256(input: String): String` – hashuje hasło SHA-256 (`java.security.MessageDigest`).

### `data/util/ImageUtils.kt`
- `copyToAppStorage(context, uri)` – kopiuje plik z urządzenia do `filesDir/bike_images/`
- `imageModel(context, path)` – zwraca odpowiedni typ dla Coil:
  - ścieżka `/...` → `File(path)` (awatar użytkownika)
  - `android.resource://...` → `@DrawableRes Int` (lokalne zasoby drawable)
  - inne → `String` (zewnętrzny URL)

---

## 11. Baza danych Room

Szczegółowy opis tabel, kolumn, danych startowych i DAO: **[BAZA_DANYCH.md](BAZA_DANYCH.md)**

```text
data/db/
├── BikeRentDatabase.kt       ← version=4, fallbackToDestructiveMigration, SeedCallback
├── converter/Converters.kt   ← List<String> ↔ String (separator "|")
├── dao/
│   ├── UserDao.kt
│   ├── BikeDao.kt
│   ├── ShopDao.kt
│   ├── ActiveRentalDao.kt
│   ├── RentalHistoryDao.kt
│   └── ReviewDao.kt
└── entity/
    ├── UserEntity.kt
    ├── BikeEntity.kt
    ├── ShopEntity.kt
    ├── ActiveRentalEntity.kt
    ├── RentalHistoryEntity.kt
    └── ReviewEntity.kt
```

---

## 12. Repozytoria

```text
data/repository/
├── UserRepository.kt
├── BikeRepository.kt
├── ShopRepository.kt
├── RentalRepository.kt
├── ReviewRepository.kt
└── impl/
    ├── UserRepositoryImpl.kt
    ├── BikeRepositoryImpl.kt
    ├── ShopRepositoryImpl.kt
    ├── RentalRepositoryImpl.kt
    └── ReviewRepositoryImpl.kt
```

| Interfejs | Odpowiada za |
|-----------|-------------|
| `UserRepository` | logowanie, rejestracja, aktualizacja danych i awatara |
| `BikeRepository` | CRUD rowerów, aktualizacja oceny |
| `ShopRepository` | pobieranie sklepów |
| `RentalRepository` | wypożyczanie, zwrot (oblicza czas/koszt), historia |
| `ReviewRepository` | dodawanie/usuwanie recenzji, sprawdzanie duplikatów |

Każda implementacja zawiera metodę `Entity.toDomain()` – konwertuje encję Room na model domenowy.

**`RentalRepositoryImpl.returnBike(...)` – logika:**
1. Pobiera aktywny wynajem
2. Usuwa z `active_rentals`
3. Oblicza czas trwania (minuty → czytelny tekst)
4. Oblicza koszt: `cena_roweru × ceil(godziny)` (min. 1 godzina)
5. Wstawia rekord do `rental_history`

---

## 13. Autoryzacja

```
LoginScreen → AuthViewModel.login → HashUtils.sha256 → UserRepository.login
    → UserDao.findByEmailAndPassword → Room → currentAvatarUri = user.avatarUri
```

```
LoginScreen → AuthViewModel.register → HashUtils.sha256 → UserRepository.register
    → UserDao.insert → Room
```

Hasła przechowywane wyłącznie jako SHA-256 w kolumnie `passwordHash`.

Administrator: konto seedowane z `DataSource` (`admin@bikerent.local`). `isAdmin` porównuje e-mail z listą adminów.

Sesja: trzymana w pamięci `AuthViewModel`. Po restarcie aplikacji wymagane ponowne logowanie.

---

## 14. Awatar użytkownika

```
UserSettingsScreen → launcher GetContent() → URI zdjęcia
    → ImageUtils.copyToAppStorage → filesDir/bike_images/nazwa.jpg
    → authViewModel.updateAvatarUri(ścieżka)
    → UserRepository.updateAvatarUri → UserDao.updateAvatarUri → users.avatarUri
```

Wyświetlanie (ProfileScreen, UserSettingsScreen):
- `currentAvatarUri != null` → `AsyncImage(File(uri))` (Coil, kółko 96 dp)
- null → zielone kółko z inicjałem imienia

---

## 15. Multimedia

- `bike_bell.mp3` (~19 KB) — `MediaPlayer.create(context, R.raw.bike_bell)`, odtwarzany po kliknięciu „Dzwonek" w `BikeDetailScreen`, zwalniany przez `setOnCompletionListener { release() }`.
- `bike_video.mp4` (~846 KB) — `VideoView` + `MediaController` w `BikeDetailScreen`, film startuje wstrzymany.

---

## 16. Ikonka aplikacji

- `ic_launcher_background.xml` – białe tło (`#FFFFFF`)
- `ic_launcher_foreground.xml` – zielony kolarz (`#2E7D32`), ścieżki z `Icons.Filled.DirectionsBike`. `fillType="evenOdd"` tworzy pierścienie kół. Skalowanie `3×` w `<group>` z `translateX/Y=18` (safe zone 18–90 w 108×108 viewport).
- `ic_launcher.xml` i `ic_launcher_round.xml` – adaptive icons.

---

## 17. Przepływy danych (przykłady)

### Wypożyczenie roweru
```
Użytkownik klika "Wypożycz" w BikeDetailScreen
    ↓
appViewModel.rentBike(bike, shop)
    ↓
RentalRepository.addRental(rental, userId)
    ↓
ActiveRentalDao.insert(ActiveRentalEntity)
    ↓
Room zapisuje do SQLite (tabela active_rentals)
    ↓
appViewModel.refreshRentals() → _activeRentals.value = ...
    ↓
RentalsScreen obserwuje activeRentals: StateFlow → recomposition UI
```

### Dodanie recenzji
```
BikeDetailScreen → appViewModel.addReview(bikeId, bikeName, rating, comment)
    ↓
ReviewRepository.addReview → ReviewDao.insert
    ↓
pobierz wszystkie opinie roweru → oblicz średnią → BikeDao.updateRating
    ↓
_currentBikeReviews i _bikes odświeżone → UI przeładowane
```

---

## 18. Konfiguracja Gradle

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

`gradle.properties` zawiera `android.disallowKotlinSourceSets=false` – flaga kompatybilności AGP/Kotlin/KSP.

---

## 19. Pytania na obronę

**Q: Dlaczego MVVM?**
A: Oddziela logikę biznesową od UI. ViewModel przeżywa rotację ekranu. UI tylko obserwuje StateFlow.

**Q: Co to Repository Pattern?**
A: Warstwa abstrakcji między ViewModel a źródłem danych. ViewModel nie wie skąd dane pochodzą. Łatwa podmiana implementacji (np. Room → REST API).

**Q: Jak działa Room?**
A: ORM od Google. `@Entity` = tabela, `@Dao` = zapytania SQL jako interfejs, `@Database` = punkt dostępu. KSP generuje implementacje w czasie kompilacji.

**Q: Dlaczego hashujemy hasła?**
A: SHA-256 w `HashUtils` – gdyby ktoś odczytał plik `bikerent.db`, nie zobaczy haseł w postaci jawnej.

**Q: Co to `sealed class AuthState`?**
A: Ograniczony zestaw możliwych stanów – kompilator wymusza obsługę wszystkich przypadków w `when`. Bezpieczniejsze niż enum, bo może nieść dane (np. `Error(message)`).

**Q: `StateFlow` vs `LiveData`?**
A: `StateFlow` to Kotlin Coroutines, idiomatyczny z Compose (`collectAsState()`). `LiveData` to starsze API Androida.

**Q: Jak działa seedowanie bazy?**
A: `SeedCallback` w `BikeRentDatabase`. `onCreate` – pełne seedowanie (użytkownicy, rowery, sklepy, opinie). `onOpen` – tylko użytkownicy i opinie (`IGNORE`), żeby nie nadpisywać zaktualizowanych ocen rowerów.

**Q: Co to animacja przejść?**
A: `BikeTransitionOverlay` – `AnimatedVisibility` nad `NavHost`. Przy każdej zmianie `currentBackStackEntry` overlay pojawia się na 500 ms. Rysuje `Icons.Filled.DirectionsBike` ze spinning Canvas na kołach.

**Q: Skąd awatar użytkownika?**
A: Wybierany przez `ActivityResultContracts.GetContent()`, kopiowany do `filesDir/`, URI zapisywane do kolumny `avatarUri` w tabeli `users`. Ładowany przez Coil jako `File(path)`.

**Q: Jak ładowane są zdjęcia rowerów?**
A: Zdjęcia seedowanych rowerów i sklepów są bundlowane jako zasoby drawable (`res/drawable-nodpi/*.jpg`). `ImageUtils.imageModel` rozwiązuje nazwę zasobu do `@DrawableRes Int` przez `getIdentifier()`. Zdjęcia rowerów dodanych przez admina są kopiowane do `filesDir/` i ładowane jako `File`.
