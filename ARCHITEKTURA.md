# BikeRent – Mapa kodu (do kolokwium/obrony)

## Architektura ogólna: MVVM + Repository Pattern

```
UI (Screens)
    ↓ obserwuje StateFlow
ViewModel (AppViewModel / AuthViewModel)
    ↓ wywołuje suspend fun
Repository (interfejs → implementacja)
    ↓ operacje na bazie
DAO (Room)
    ↓ SQL
SQLite (Room Database)
```

---

## 1. Punkt wejścia aplikacji

### `MainActivity.kt`
- Jedyna aktywność w aplikacji (single-activity architecture).
- Ustawia Jetpack Compose jako UI (`setContent`), aplikuje motyw `BikeRentTheme`.
- Wywołuje `AppNavigation()` – od tego momentu całą nawigacją zajmuje się Compose.

### `BikeRentApp.kt`
- Rozszerza klasę `Application` – uruchamia się jako pierwsza, przed `MainActivity`.
- Tworzy **singleton bazy danych** (`BikeRentDatabase`) i wszystkie **repozytoria** (lazy – tworzone przy pierwszym użyciu).
- Dzięki temu ViewModele dostają repozytoria przez `LocalContext.current.applicationContext as BikeRentApp`.

---

## 2. Nawigacja i animacja przejść

### `navigation/AppNavigation.kt`

Zawiera dwie rzeczy:

**`sealed class Screen`** – lista wszystkich ekranów z ich trasami URL:
| Obiekt | Trasa | Ekran |
|--------|-------|-------|
| `Login` | `"login"` | Logowanie/rejestracja |
| `Home` | `"home"` | Lista rowerów |
| `BikeDetail` | `"bike/{bikeId}"` | Szczegóły roweru |
| `ShopProfile` | `"shop/{shopId}"` | Profil sklepu |
| `Rentals` | `"rentals"` | Aktywne i historia wypożyczeń |
| `Profile` | `"profile"` | Profil użytkownika |
| `Settings` | `"settings"` | Ustawienia konta |
| `Admin` | `"admin"` | Panel admina |
| `MyReviews` | `"my_reviews"` | Moje recenzje |

**`@Composable fun AppNavigation()`** – tworzy `NavHostController`, instancje obu ViewModeli, podpina każdy ekran do jego trasy i nasłuchuje `authState` – po zalogowaniu wywołuje `appViewModel.initForUser(...)`.

`NavHost` jest opakowany w `Box`. Nad nim wyświetlany jest `BikeTransitionOverlay` – biały semi-transparentny overlay z logo BikeRent, widoczny przez 500 ms przy każdej zmianie ekranu:

```kotlin
LaunchedEffect(currentEntry) {
    showTransition = true
    delay(500)
    showTransition = false
}
```

---

## 3. ViewModele (warstwa logiki)

### `viewmodel/AuthViewModel.kt`

Odpowiada za **logowanie i rejestrację**.

**Stan (`authState: StateFlow<AuthState>`):**
- `Idle` – brak akcji
- `Loading` – trwa zapytanie
- `Success(userId, name, email)` – zalogowano
- `Error(message)` – błąd

**Kluczowe metody:**
- `login(email, password)` – hashuje hasło SHA-256, szuka użytkownika w bazie, ładuje `currentAvatarUri`
- `register(name, email, password, confirmPassword)` – waliduje pola, hashuje, tworzy konto
- `logout()` – resetuje stan do `Idle`, czyści `currentAvatarUri`
- `updateUserData(name, email)` – zmiana danych profilu
- `updateAvatarUri(uri)` – zapisuje URI awatara do Room i aktualizuje `currentAvatarUri`
- `isAdmin` – sprawdza czy email jest na liście adminów z `DataSource`

**Właściwości stanu:**
- `currentAvatarUri: String?` – ścieżka do awatara zalogowanego użytkownika (null jeśli nie ustawiony)

**`AuthViewModelFactory`** – potrzebna bo ViewModel potrzebuje `UserRepository` w konstruktorze.

---

### `viewmodel/AppViewModel.kt`

Główny ViewModel aplikacji. Przechowuje **cały stan UI** po zalogowaniu.

**StateFlow'y (obserwowane przez ekrany):**
| Pole | Typ | Zawartość |
|------|-----|-----------|
| `bikes` | `List<Bike>` | Wszystkie rowery |
| `shops` | `List<Shop>` | Wszystkie sklepy |
| `activeRentals` | `List<ActiveRental>` | Aktywne wypożyczenia zalogowanego usera |
| `rentalHistory` | `List<RentalHistory>` | Historia wypożyczeń |
| `currentBikeReviews` | `List<Review>` | Recenzje aktualnie przeglądanego roweru |
| `userReviews` | `List<Review>` | Recenzje zalogowanego usera |
| `allReviews` | `List<Review>` | Wszystkie recenzje (dla admina) |

**Kluczowe metody:**
- `initForUser(userId, userName)` – wołane po zalogowaniu, ładuje dane z bazy
- `rentBike(bike, shop)` – tworzy `ActiveRental` z timestampem i zapisuje
- `returnBike(rental)` – usuwa z `active_rentals`, oblicza czas i koszt, wpisuje do `rental_history`
- `addBike(...)` – admin dodaje nowy rower
- `addReview(bikeId, bikeName, rating, comment)` – dodaje recenzję i aktualizuje ocenę roweru
- `deleteReview(review)` – admin usuwa recenzję, przelicza ocenę roweru

---

## 4. Ekrany UI (`ui/screens/`)

Każdy ekran to `@Composable fun` przyjmujący `navController` i `appViewModel` (lub `authViewModel`).

### `LoginScreen.kt`
- Formularz logowania i rejestracja (zakładki).
- Logo aplikacji: `Icons.Filled.DirectionsBike` (60 dp, zielony) + napis „BikeRent".
- Obserwuje `authState` – po `Success` nawiguje do `Home`.
- Walidacja po stronie ViewModelu.

### `HomeScreen.kt`
- Lista rowerów z `appViewModel.bikes`.
- Filtrowanie po kategorii i wyszukiwanie po nazwie.
- Kliknięcie → `BikeDetail`.
- Zawiera `BottomNavBar`.

### `BikeDetailScreen.kt`
- Karuzela zdjęć (`HorizontalPager` + wskaźnik strony).
- Przycisk „Dzwonek" → `MediaPlayer.create(context, R.raw.bike_bell)`.
- Sekcja „Film promocyjny" → `VideoView` z `R.raw.bike_video`; film startuje **wstrzymany**.
- Przycisk „Wypożycz" → `appViewModel.rentBike(...)`.
- Lista recenzji (`currentBikeReviews`) + formularz dodawania recenzji (ukryty po wystawieniu).
- Link do profilu sklepu.

### `ShopProfileScreen.kt`
- Informacje o sklepie (nazwa, opis, lokalizacja, ocena).
- Lista rowerów należących do tego sklepu.

### `RentalsScreen.kt`
- Dwie sekcje: aktywne wypożyczenia i historia.
- Używa `ScreenHeader("Moje Wypożyczenia", onBack = popBackStack)`.
- Przycisk „Zwróć" → `appViewModel.returnBike(...)`.

### `ProfileScreen.kt`
- Awatar: `AsyncImage` (96 dp, kółko) jeśli `authViewModel.currentAvatarUri != null`, inaczej zielone kółko z inicjałem.
- Wyświetla dane zalogowanego użytkownika i statystyki.
- Przycisk wylogowania → `authViewModel.logout()` → nawigacja do `Login`.
- Jeśli `authViewModel.isAdmin` → przycisk do panelu admina.
- Używa `ScreenHeader("Profil")`.

### `UserSettingsScreen.kt`
- Awatar z możliwością zmiany: klikalne kółko (AsyncImage lub inicjał) z ikoną aparatu w rogu.
- Picker uruchamiany przez `rememberLauncherForActivityResult(ActivityResultContracts.GetContent())`.
- Wybrany plik kopiowany do `filesDir/bike_images/`, URI zapisywane do Room przez `authViewModel.updateAvatarUri(uri)`.
- Edycja imienia i emaila → `authViewModel.updateUserData(...)`.
- Używa `ScreenHeader("Ustawienia konta", onBack = navigateUp)`.

### `AdminPanelScreen.kt`
- Widoczny tylko dla adminów.
- Dodawanie nowych rowerów (formularz z wyborem sklepu, kategorii, ceny, file picker).
- Lista wszystkich recenzji z możliwością usunięcia.

### `MyReviewsScreen.kt`
- Lista recenzji napisanych przez zalogowanego użytkownika.
- Używa `ScreenHeader("Moje opinie", onBack = navigateUp)`.

---

## 5. Komponenty UI (`ui/components/`)

### `BottomNavBar.kt`
- Dolny pasek nawigacyjny z 4 pozycjami: Home, Wypożyczenia, Profil, Ustawienia.
- Podświetla aktywny ekran.
- Używa `navController.navigate(...)` z `popUpTo` żeby nie stackować ekranów.

### `ScreenHeader.kt`
- Wspólny zielony nagłówek używany przez: Profile, Rentals, UserSettings, MyReviews.
- Zapewnia jednolitą wysokość na wszystkich ekranach.
- `Row` z `padding(start=4, top=40, end=16, bottom=20)`.
- Ekrany bez przycisku wstecz dostają `Spacer(52.dp)` zamiast `IconButton`.

### `BikeTransitionOverlay.kt`
- Semi-transparentny biały overlay (alpha 0.96) wyświetlany 500 ms przy każdej zmianie ekranu.
- `AnimatedVisibility` z `fadeIn(80ms)` / `fadeOut(180ms)`.
- Zawiera `Icons.Filled.DirectionsBike` (80 dp, zielony) — to samo logo co na ekranie logowania.
- Na warstwie ikony rysowany jest `SpinningWheelsOverlay` (Canvas): 6 szprych na każdym kole, obracające się co 700 ms.
- Środki kół dopasowane do geometrii ikony: tył `(5/24 × w, 17/24 × h)`, przód `(19/24 × w, 17/24 × h)`.
- Pod ikoną napis „BikeRent" (26 sp, Bold, zielony).

### `ui/theme/`
- `Color.kt` – definicje kolorów (np. `Green800 = #2E7D32`, `Green100`)
- `Theme.kt` – `BikeRentTheme` (Material 3)
- `Type.kt` – typografia

---

## 6. Ikonka aplikacji (`res/drawable/`)

- `ic_launcher_background.xml` – białe tło (`#FFFFFF`).
- `ic_launcher_foreground.xml` – zielony kolarz (`#2E7D32`), ścieżki skopiowane dosłownie z `Icons.Filled.DirectionsBike` z biblioteki Material Icons. `fillType="evenOdd"` tworzy pierścienie kół (efekt ring). Skalowanie 3× w `<group>` z `translateX/Y=18` (safe zone 18–90 w 108×108 viewport).
- Dotyczy zarówno `ic_launcher.xml` jak i `ic_launcher_round.xml`.

---

## 7. Dane – warstwa `data/`

### `data/Models.kt`
Czyste klasy domenowe (nie znają Room ani bazy danych):
- `Bike` – rower (id, name, price, rating, image, images, description, available, shopId, category)
- `Shop` – sklep (id, name, description, location, rating, image, bikeIds)
- `ActiveRental` – aktywne wypożyczenie
- `RentalHistory` – zakończone wypożyczenie
- `Review` – recenzja (id, bikeId, bikeName, userId, userName, rating, comment, date)
- `SeedUser` – predefiniowany użytkownik/admin do seedowania bazy

### `data/DataSource.kt`
- Obiekt singleton z hardkodowanymi danymi startowymi.
- `bikes` – 12 predefiniowanych rowerów, wszystkie `available = true`.
- `shops` – 4 sklepy: BikeHub Centrum (id=1), EcoBike Station (id=2), VeloCity Praga (id=3), GreenWheels Mokotów (id=4).
- `seededAdminUsers` – konta adminów z hashami haseł, wczytywane przy starcie bazy.

---

## 8. Baza danych – Room (`data/db/`)

### `data/db/BikeRentDatabase.kt`
- Klasa `@Database` łącząca wszystkie encje i DAO.
- **Singleton** tworzony przez `getInstance(context)`.
- **Wersja bazy: `3`**, `fallbackToDestructiveMigration()` – przy zmianie wersji kasuje dane.
- `SeedCallback` – wypełnia bazę danymi z `DataSource` przy `onCreate` i `onOpen`.

### Encje (`data/db/entity/`) – mapowanie klas na tabele SQL

| Plik | Tabela | Klucz główny | Uwagi |
|------|--------|--------------|-------|
| `UserEntity` | `users` | `id: Long` (autoincrement) | + `avatarUri: String?` (v3) |
| `BikeEntity` | `bikes` | `id: String` | — |
| `ShopEntity` | `shops` | `id: String` | — |
| `ActiveRentalEntity` | `active_rentals` | `id: String` | FK → `users.id` CASCADE |
| `RentalHistoryEntity` | `rental_history` | `id: String` | FK → `users.id` CASCADE |
| `ReviewEntity` | `reviews` | `id: String` | indeksy na `bikeId`, `userId` |

### DAO (`data/db/dao/`) – interfejsy zapytań SQL

| Plik | Kluczowe metody |
|------|----------------|
| `UserDao` | `insert`, `findByEmail`, `findByEmailAndPassword`, `updateNameAndEmail`, `updateAvatarUri` |
| `BikeDao` | `insert`, `insertAll`, `getAll`, `findById`, `updateRating` |
| `ShopDao` | `insertAll`, `getAll`, `findById` |
| `ActiveRentalDao` | `insert`, `getAllForUser(userId)`, `deleteById` |
| `RentalHistoryDao` | `insert`, `getAllForUser(userId)` |
| `ReviewDao` | `insert`, `getAllForBike`, `getAllForUser`, `getAll`, `deleteById`, `findByUserAndBike` |

---

## 9. Repozytoria (`data/repository/`)

| Interfejs | Implementacja | Odpowiada za |
|-----------|--------------|--------------|
| `UserRepository` | `UserRepositoryImpl` | logowanie, rejestracja, aktualizacja danych, `updateAvatarUri` |
| `BikeRepository` | `BikeRepositoryImpl` | CRUD rowerów, aktualizacja oceny |
| `ShopRepository` | `ShopRepositoryImpl` | pobieranie sklepów |
| `RentalRepository` | `RentalRepositoryImpl` | wypożyczanie, zwrot roweru (oblicza czas/koszt), historia |
| `ReviewRepository` | `ReviewRepositoryImpl` | dodawanie/usuwanie recenzji, sprawdzanie duplikatów |

**Wzorzec:** każda implementacja zawiera prywatną metodę `Entity.toDomain()` – konwertuje encję Room na czysty model domenowy.

**`RentalRepositoryImpl.returnBike(...)` – złożona logika:**
1. Pobiera aktywny wynajem
2. Usuwa z `active_rentals`
3. Oblicza czas trwania (minuty → czytelny tekst)
4. Oblicza koszt: `cena_roweru × ceil(godziny)` (min. 1 godzina)
5. Wstawia rekord do `rental_history`

---

## 10. Narzędzia (`data/util/`)

### `HashUtils.kt`
- `sha256(input: String): String` – hashuje hasło przed zapisem/porównaniem z bazą.
- Używa `java.security.MessageDigest`.

### `ImageUtils.kt`
- `copyToAppStorage(context, uri)` – kopiuje plik z urządzenia do `filesDir/bike_images/`.
- `imageModel(path)` – zwraca `File(path)` dla lokalnych ścieżek albo `String` dla URL-i (Coil 3 obsługuje oba).

---

## Schemat przepływu danych (przykład: wypożyczenie roweru)

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

---

## Pytania, które mogą paść na obronie

**Q: Dlaczego MVVM?**
A: Oddziela logikę biznesową od UI. ViewModel przeżywa rotację ekranu (nie ginie jak Activity). UI tylko obserwuje StateFlow.

**Q: Co to Repository Pattern?**
A: Warstwa abstrakcji między ViewModel a źródłem danych. ViewModel nie wie skąd dane pochodzą (Room, API, cache). Łatwa podmiana implementacji.

**Q: Jak działa Room?**
A: Biblioteka ORM od Google. `@Entity` = tabela, `@Dao` = zapytania SQL jako interfejs, `@Database` = punkt dostępu. KSP generuje implementacje w czasie kompilacji.

**Q: Dlaczego hashujemy hasła?**
A: SHA-256 w `HashUtils` – gdyby ktoś odczytał bazę SQLite z urządzenia, nie zobaczy haseł w postaci jawnej.

**Q: Co to `sealed class AuthState`?**
A: Ograniczony zestaw możliwych stanów – kompilator wymusza obsługę wszystkich przypadków w `when`. Bezpieczniejsze niż enum bo może mieć dane (np. `Error(message)`).

**Q: Co to `StateFlow` vs `LiveData`?**
A: `StateFlow` to Kotlin Coroutines, działa z `collectAsState()` w Compose. `LiveData` to starsze API Androida, mniej idiomatyczne z Compose.

**Q: Jak działa seedowanie bazy?**
A: `SeedCallback` w `BikeRentDatabase` – przy każdym otwarciu bazy wstawia wstępne dane (rowery, sklepy, adminów) z `DataSource`. `IGNORE` conflict strategy – nie nadpisuje istniejących.

**Q: Co to animacja przejść?**
A: `BikeTransitionOverlay` – `AnimatedVisibility` nad `NavHost`. Przy każdej zmianie `currentBackStackEntry` ustawiany jest `showTransition = true` na 500 ms. Overlay rysuje `Icons.Filled.DirectionsBike` ze spinning Canvas na kołach.

**Q: Skąd awatar użytkownika?**
A: Wybierany przez `ActivityResultContracts.GetContent()`, kopiowany do `filesDir/`, URI zapisywane do kolumny `avatarUri` w tabeli `users` (DB v3). Ładowany przez Coil jako `File(uri)`.
