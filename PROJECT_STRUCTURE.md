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
│       │       ├── mipmap-*/
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
└── PROJECT_STRUCTURE.md
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

- `LoginScreen.kt` - ekran logowania i rejestracji. Obsługuje e-mail, hasło, potwierdzenie hasła i przycisk Google jako zaślepkę.
- `HomeScreen.kt` - ekran główny z listą rowerów i wyszukiwarką.
- `BikeDetailScreen.kt` - szczegóły wybranego roweru, karuzela zdjęć, lista opinii z dynamicznie obliczaną średnią oceną, formularz dodawania opinii, akcja wypożyczenia.
- `ShopProfileScreen.kt` - profil wypożyczalni/sklepu z listą dostępnych rowerów.
- `RentalsScreen.kt` - aktywne wypożyczenia z licznikiem czasu i historia wypożyczeń.
- `ProfileScreen.kt` - profil użytkownika z avatarem i statystykami. Dostęp do panelu administratora tylko dla konta admina.
- `UserSettingsScreen.kt` - edycja danych konta użytkownika.
- `MyReviewsScreen.kt` - lista wszystkich opinii wystawionych przez zalogowanego użytkownika.
- `AdminPanelScreen.kt` - panel administratora: dodawanie rowerów (formularz z file pickerem) i moderacja opinii z możliwością usuwania.

### Komponenty

```text
ui/components/BottomNavBar.kt
```

Zawiera dolną nawigację między głównymi ekranami.

### Motyw

```text
ui/theme/
├── Color.kt
├── Theme.kt
└── Type.kt
```

Zawiera kolory, typografię i konfigurację motywu Compose.

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

Aplikacja startuje od ekranu `login`. Po udanym logowaniu lub rejestracji `AuthViewModel` ustawia `AuthState.Success`, a `AppNavigation` inicjalizuje `AppViewModel` dla zalogowanego użytkownika z jego `userId` i `name`. Wszystkie ekrany drugorzędne mają strzałkę wstecz powracającą do poprzedniego ekranu.

## 7. ViewModel

ViewModele znajdują się w:

```text
app/src/main/java/com/example/bikerent/viewmodel/
├── AuthViewModel.kt
└── AppViewModel.kt
```

### `AuthViewModel.kt`

Odpowiada za:

- logowanie,
- rejestrację,
- wylogowanie,
- aktualizację danych użytkownika,
- trzymanie aktualnej sesji użytkownika w pamięci aplikacji,
- sprawdzanie czy zalogowany użytkownik jest administratorem (`isAdmin`).

Stan autoryzacji jest opisany przez `AuthState`:

- `Idle`
- `Loading`
- `Success`
- `Error`

Hasło wpisane przez użytkownika jest hashowane przez `HashUtils.sha256`, a dopiero potem przekazywane do `UserRepository`.

Właściwość `isAdmin` porównuje e-mail zalogowanego użytkownika z listą `DataSource.seededAdminUsers`. Dostęp do panelu administratora mają tylko konta z góry zdefiniowane w `DataSource`.

### `AppViewModel.kt`

Odpowiada za główne dane aplikacji:

- listę rowerów (`_bikes: StateFlow<List<Bike>>`),
- listę sklepów (`_shops: StateFlow<List<Shop>>`),
- aktywne wypożyczenia (`_activeRentals: StateFlow<List<ActiveRental>>`),
- historię wypożyczeń (`_rentalHistory: StateFlow<List<RentalHistory>>`),
- opinie dla wybranego roweru (`currentBikeReviews: StateFlow<List<Review>>`),
- opinie zalogowanego użytkownika (`userReviews: StateFlow<List<Review>>`),
- wszystkie opinie - do panelu admina (`allReviews: StateFlow<List<Review>>`),
- wypożyczenie roweru,
- zwrot roweru,
- dodawanie opinii z automatyczną aktualizacją średniej oceny,
- usuwanie opinii z automatyczną aktualizacją średniej oceny,
- dodawanie nowego roweru przez administratora.

ViewModel nie zna bezpośrednio Room ani DAO. Korzysta tylko z interfejsów repozytoriów:

- `BikeRepository`
- `ShopRepository`
- `RentalRepository`
- `ReviewRepository`

## 8. Backend

Aktualnie nie ma zewnętrznego backendu sieciowego. Rolę backendu pełni lokalna warstwa danych:

```text
Room Database -> DAO -> RepositoryImpl -> Repository interface -> ViewModel -> UI
```

Dzięki Repository Pattern przyszła integracja z AWS może wyglądać tak:

- zostają interfejsy repozytoriów,
- implementacje `*RepositoryImpl` można zastąpić implementacjami używającymi API Gateway/Lambda/DynamoDB,
- ViewModele i ekrany nie muszą znać szczegółów, czy dane pochodzą z Room, czy z API.

Planowana integracja AWS nie jest jeszcze zaimplementowana.

## 9. Warstwa danych

Warstwa danych znajduje się w:

```text
app/src/main/java/com/example/bikerent/data/
```

Najważniejsze pliki:

```text
data/
├── DataSource.kt
├── Models.kt
├── db/
├── repository/
└── util/
    └── ImageUtils.kt
```

### `Models.kt`

Zawiera modele domenowe używane przez UI i ViewModele:

- `Review` - opinia użytkownika o rowerze: `id, bikeId, bikeName, userId, userName, rating, comment, date`
- `Bike` - rower
- `Shop` - sklep/wypożyczalnia
- `ActiveRental` - aktywne wypożyczenie
- `RentalHistory` - wpis w historii wypożyczeń
- `SeedUser` - dane startowe konta administratora

Modele domenowe nie są tym samym co encje Room. Repozytoria mapują encje bazy danych na modele domenowe.

### `DataSource.kt`

Zawiera dane startowe:

- rowery (6 sztuk, 2 sklepy),
- sklepy (2 sztuki),
- konto administratora (`seededAdminUsers`).

Dane z `DataSource` są używane do seedowania bazy Room przy pierwszym uruchomieniu. Nie seedujemy zwykłych kont użytkowników, opinii ani historii wypożyczeń.

### `util/ImageUtils.kt`

Klasa pomocnicza do obsługi zdjęć:

- `copyToAppStorage(context, uri)` - kopiuje plik wybrany przez użytkownika z urządzenia do `filesDir/bike_images/`, zwraca bezwzględną ścieżkę do kopii.
- `imageModel(path)` - zwraca `File(path)` dla lokalnych ścieżek (zaczynających się od `/`) albo `String` dla URL-i. Coil 3 obsługuje oba typy jako model obrazu.

Zdjęcia seedowanych rowerów są pobierane z URL-i Unsplash (wymaga połączenia z internetem). Zdjęcia rowerów dodanych przez administratora są kopiowane lokalnie i ładowane z pliku bez dostępu do sieci.

## 10. Baza danych Room

Baza znajduje się w:

```text
app/src/main/java/com/example/bikerent/data/db/
```

Pliki:

```text
db/
├── BikeRentDatabase.kt
├── converter/Converters.kt
├── dao/
│   ├── ActiveRentalDao.kt
│   ├── BikeDao.kt
│   ├── RentalHistoryDao.kt
│   ├── ReviewDao.kt
│   ├── ShopDao.kt
│   └── UserDao.kt
└── entity/
    ├── ActiveRentalEntity.kt
    ├── BikeEntity.kt
    ├── RentalHistoryEntity.kt
    ├── ReviewEntity.kt
    ├── ShopEntity.kt
    └── UserEntity.kt
```

### `BikeRentDatabase.kt`

Główna klasa bazy danych Room.

Nazwa lokalnej bazy:

```text
bikerent.db
```

Wersja bazy:

```text
2
```

Baza używa `.fallbackToDestructiveMigration()` — przy zmianie wersji schematu Room usuwa starą bazę i tworzy nową. Przy pierwszym uruchomieniu po aktualizacji z wersji 1 konieczne jest ponowne logowanie.

Encje:

- `UserEntity`
- `BikeEntity`
- `ShopEntity`
- `ActiveRentalEntity`
- `RentalHistoryEntity`
- `ReviewEntity`

Przy pierwszym utworzeniu bazy wykonywany jest `SeedCallback`, który dodaje dane z `DataSource`:

- konto administratora,
- rowery,
- sklepy.

### Tabele

#### `users`

Encja: `UserEntity`

Pola:

- `id` - `Long`, klucz główny, auto-generowany dla nowych użytkowników,
- `email` - unikalny e-mail,
- `name` - imię i nazwisko,
- `passwordHash` - hash hasła SHA-256.

Indeks:

- unikalny indeks na `email`.

#### `bikes`

Encja: `BikeEntity`

Pola:

- `id`
- `name`
- `price`
- `rating` - dynamicznie aktualizowana średnia ocen z tabeli `reviews`
- `image` - ścieżka lub URL głównego zdjęcia
- `images` - lista ścieżek lub URL-i (karuzela)
- `description`
- `available`
- `shopId`
- `category`

Lista `images` jest zapisywana przez konwerter Room jako tekst rozdzielony znakiem `|`.

#### `shops`

Encja: `ShopEntity`

Pola:

- `id`
- `name`
- `description`
- `location`
- `rating`
- `image`
- `bikeIds`

Lista `bikeIds` jest zapisywana przez konwerter Room.

#### `active_rentals`

Encja: `ActiveRentalEntity`

Pola:

- `id`
- `bikeId`
- `bikeName`
- `shopName`
- `startTime` - data i czas wypożyczenia w formacie `dd.MM.yyyy HH:mm`
- `endTime`
- `returnLocation`
- `userId`

Relacja:

- `userId` jest kluczem obcym do tabeli `users`,
- usunięcie użytkownika usuwa jego aktywne wypożyczenia przez `CASCADE`.

#### `rental_history`

Encja: `RentalHistoryEntity`

Pola:

- `id`
- `bikeName`
- `shopName`
- `date` - zakres dat w formacie `dd.MM.yyyy HH:mm – dd.MM.yyyy HH:mm`
- `duration` - rzeczywisty czas trwania np. `"2 h 15 min"`
- `cost` - koszt obliczony na podstawie rzeczywistego czasu (zaokrąglony w górę do pełnych godzin)
- `userId`

Relacja:

- `userId` jest kluczem obcym do tabeli `users`,
- usunięcie użytkownika usuwa jego historię wypożyczeń przez `CASCADE`.

#### `reviews`

Encja: `ReviewEntity`

Pola:

- `id` - string generowany jako `"rev_${System.currentTimeMillis()}"`
- `bikeId` - klucz obcy do tabeli `bikes`
- `bikeName` - nazwa roweru (denormalizacja dla wygody wyświetlania)
- `userId` - identyfikator użytkownika
- `userName` - imię użytkownika
- `rating` - liczba gwiazdek (1–5)
- `comment` - treść opinii
- `date` - data wystawienia w formacie `dd.MM.yyyy`

Indeksy:

- indeks na `bikeId` (dla wydajnego pobierania opinii roweru),
- indeks na `userId` (dla profilu użytkownika).

Jeden użytkownik może wystawić jedną opinię danemu rowerowi (sprawdzane przez `ReviewDao.findByUserAndBike`).

### Konwertery Room

Plik:

```text
data/db/converter/Converters.kt
```

Room nie zapisuje automatycznie list złożonych typów, dlatego używane są konwertery:

- `List<String>` jest zapisywane jako tekst rozdzielony znakiem `|`.

## 11. DAO

DAO są warstwą bezpośredniego dostępu do Room.

### `UserDao.kt`

Obsługuje:

- dodawanie użytkownika,
- seedowanie konta administratora,
- wyszukiwanie po e-mailu,
- wyszukiwanie po e-mailu i hashu hasła,
- wyszukiwanie po id,
- aktualizację imienia i e-maila.

### `BikeDao.kt`

Obsługuje:

- dodawanie listy rowerów (seed),
- dodawanie pojedynczego roweru,
- pobranie wszystkich rowerów,
- pobranie roweru po id,
- zliczenie rowerów,
- aktualizację oceny roweru (`updateRating`).

### `ShopDao.kt`

Obsługuje:

- dodawanie listy sklepów,
- pobranie wszystkich sklepów,
- pobranie sklepu po id.

### `ActiveRentalDao.kt`

Obsługuje:

- dodanie aktywnego wypożyczenia,
- pobranie aktywnych wypożyczeń użytkownika,
- usunięcie aktywnego wypożyczenia po id.

### `RentalHistoryDao.kt`

Obsługuje:

- dodanie wpisu historii,
- pobranie historii wypożyczeń użytkownika.

### `ReviewDao.kt`

Obsługuje:

- dodanie opinii,
- pobranie wszystkich opinii dla danego roweru,
- pobranie wszystkich opinii danego użytkownika,
- pobranie wszystkich opinii (dla panelu admina),
- usunięcie opinii po id,
- sprawdzenie czy użytkownik już ocenił dany rower (`findByUserAndBike`).

## 12. Repozytoria

Repozytoria znajdują się w:

```text
data/repository/
├── BikeRepository.kt
├── ShopRepository.kt
├── RentalRepository.kt
├── ReviewRepository.kt
├── UserRepository.kt
└── impl/
    ├── BikeRepositoryImpl.kt
    ├── ShopRepositoryImpl.kt
    ├── RentalRepositoryImpl.kt
    ├── ReviewRepositoryImpl.kt
    └── UserRepositoryImpl.kt
```

Interfejsy repozytoriów są kontraktem dla ViewModeli. Implementacje `impl` korzystają aktualnie z DAO Room.

### `UserRepository`

Obsługuje:

- rejestrację,
- logowanie,
- wyszukiwanie użytkownika,
- aktualizację danych użytkownika.

### `BikeRepository`

Obsługuje:

- pobranie wszystkich rowerów,
- pobranie roweru po id,
- aktualizację oceny roweru,
- dodanie nowego roweru.

### `ShopRepository`

Obsługuje:

- pobranie wszystkich sklepów,
- pobranie sklepu po id.

### `RentalRepository`

Obsługuje:

- aktywne wypożyczenia,
- historię wypożyczeń,
- dodanie wypożyczenia,
- zwrot roweru i przeniesienie wpisu do historii.

### `ReviewRepository`

Obsługuje:

- pobranie opinii dla roweru,
- pobranie opinii użytkownika,
- pobranie wszystkich opinii,
- dodanie opinii,
- usunięcie opinii,
- sprawdzenie czy użytkownik już ocenił rower.

## 13. Autoryzacja

Autoryzacja działa lokalnie w Room.

Przepływ rejestracji:

```text
LoginScreen -> AuthViewModel.register -> HashUtils.sha256 -> UserRepository.register -> UserDao.insert -> Room
```

Przepływ logowania:

```text
LoginScreen -> AuthViewModel.login -> HashUtils.sha256 -> UserRepository.login -> UserDao.findByEmailAndPassword -> Room
```

Hasła:

- użytkownik wpisuje hasło w formularzu,
- aplikacja hashuje je przez SHA-256,
- w tabeli `users` przechowywany jest tylko `passwordHash`,
- logowanie porównuje e-mail i hash hasła.

Administrator:

- konto administratora jest seedowane z `DataSource.kt` (e-mail: `admin@bikerent.local`, hasło jako SHA-256),
- `AuthViewModel.isAdmin` sprawdza, czy e-mail zalogowanego użytkownika należy do `DataSource.seededAdminUsers`,
- tylko administrator widzi opcję "Panel administracyjny" w menu profilu.

Google login:

- przycisk jest widoczny,
- jest nieaktywny,
- tekst informuje, że funkcja będzie dostępna później.

Sesja:

- aktualnie sesja jest trzymana w pamięci `AuthViewModel`,
- po restarcie aplikacji użytkownik musi zalogować się ponownie,
- nie ma jeszcze trwałego tokenu ani pamiętania sesji.

## 14. Wypożyczanie i zwrot roweru

Przepływ wypożyczenia:

```text
BikeDetailScreen -> AppViewModel.rentBike -> RentalRepository.addRental -> ActiveRentalDao.insert -> active_rentals
```

Podczas wypożyczenia:

- rower jest oznaczany jako niedostępny,
- rejestrowany jest dokładny czas wypożyczenia (`startTime` w formacie `dd.MM.yyyy HH:mm`).

Przepływ zwrotu:

```text
RentalsScreen -> AppViewModel.returnBike -> RentalRepository.returnBike
```

Podczas zwrotu:

- aktywne wypożyczenie jest usuwane z `active_rentals`,
- tworzony jest wpis w `rental_history`,
- czas trwania jest obliczany jako różnica między aktualnym czasem a `startTime`,
- koszt jest liczony na podstawie ceny roweru i rzeczywistego czasu (zaokrąglonego w górę do pełnych godzin, minimum 1 godzina).

## 15. Opinie i oceny rowerów

Przepływ dodawania opinii:

```text
BikeDetailScreen -> AppViewModel.addReview -> ReviewRepository.addReview -> ReviewDao.insert -> reviews
```

Po dodaniu opinii:

- pobierane są wszystkie opinie danego roweru,
- liczona jest nowa średnia ocena,
- wynik jest zapisywany przez `BikeRepository.updateRating` do tabeli `bikes`.

Każdy zalogowany użytkownik może wystawić jedną opinię danemu rowerowi. Po wystawieniu formularz znika z ekranu szczegółów roweru.

Przepływ usuwania opinii (administrator):

```text
AdminPanelScreen -> AppViewModel.deleteReview -> ReviewRepository.deleteReview -> ReviewDao.deleteById
```

Po usunięciu administrator widzi aktualną listę pozostałych opinii, a średnia ocena roweru jest przeliczana automatycznie.

Użytkownik może przeglądać swoje opinie w ekranie `MyReviewsScreen` (profil → "Moje oceny").

## 16. Dodawanie rowerów przez administratora

Administrator może dodać nowy rower z poziomu panelu administratora:

1. Otwiera formularz (Dialog w `AdminPanelScreen`).
2. Wypełnia pola: nazwa, opis, kategoria (lista rozwijana), cena, sklep (lista rozwijana z bazy Room).
3. Wybiera zdjęcie z urządzenia przez file picker (`ActivityResultContracts.GetContent()`).
4. Wybrany plik jest kopiowany do `filesDir/bike_images/` w tle (`Dispatchers.IO`).
5. Po zatwierdzeniu `AppViewModel.addBike` zapisuje nowy rower do bazy Room i odświeża listę rowerów.

Dodany rower jest widoczny natychmiast na ekranie głównym i w profilu wybranego sklepu. Zdjęcie jest ładowane lokalnie przez Coil bez dostępu do sieci.

## 17. Zasoby aplikacji

Zasoby Androida znajdują się w:

```text
app/src/main/res/
```

Najważniejsze katalogi:

- `drawable/` - zasoby rysunkowe,
- `mipmap-*/` - ikony aplikacji,
- `values/` - `strings.xml`, `colors.xml`, `themes.xml`,
- `xml/` - reguły backupu i ekstrakcji danych.

Aplikacja ma włączone uprawnienie:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

Jest ono potrzebne do ładowania zdjęć seedowanych rowerów z URL-i Unsplash. Zdjęcia rowerów dodanych przez administratora są ładowane lokalnie bez tego uprawnienia.

## 18. Konfiguracja Gradle

Najważniejsze pliki:

```text
settings.gradle.kts
build.gradle.kts
app/build.gradle.kts
gradle/libs.versions.toml
gradle.properties
```

### `gradle/libs.versions.toml`

Centralne miejsce wersji zależności i pluginów.

Istotne wersje:

- Android Gradle Plugin: `9.1.0`
- Kotlin: `2.2.10`
- KSP: `2.3.2`
- Room: `2.8.4`
- Coil: `3.1.0`
- Navigation Compose: `2.9.0`
- Activity Compose: `1.10.1`
- Compose BOM: `2026.02.01`

### `gradle.properties`

Zawiera ustawienia Gradle i Kotlin.

W projekcie ustawiono też:

```properties
android.disallowKotlinSourceSets=false
```

To jest flaga kompatybilności potrzebna przy obecnym połączeniu AGP/Kotlin/KSP, żeby kod generowany przez KSP był poprawnie traktowany jako źródła Kotlin.

## 19. Testy

Testy jednostkowe:

```text
app/src/test/
```

Testy instrumentalne:

```text
app/src/androidTest/
```

Aktualnie są tam podstawowe przykładowe testy wygenerowane przez szablon projektu.

Przydatne komendy:

```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
```

## 20. Aktualne ograniczenia

- Brak zewnętrznego backendu AWS.
- Brak trwałego zapamiętywania sesji użytkownika po restarcie aplikacji.
- Google login jest tylko zaślepką.
- Baza używa `fallbackToDestructiveMigration` zamiast zdefiniowanych migracji — przy zmianie schematu dane są kasowane.
- Haszowanie SHA-256 spełnia wymaganie projektu, ale w produkcyjnej aplikacji lepszym wyborem byłby algorytm z solą i kosztem obliczeniowym, np. bcrypt, scrypt albo Argon2.
