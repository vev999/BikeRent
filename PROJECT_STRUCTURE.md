# BikeRent - struktura projektu

Ten dokument opisuje aktualną strukturę aplikacji BikeRent: warstwy projektu, sposób przechowywania danych, lokalny backend, bazę danych, frontend w Jetpack Compose oraz najważniejsze pliki.

## 1. Cel aplikacji

BikeRent to aplikacja mobilna na Androida do wypożyczania rowerów. Aplikacja jest napisana w Kotlinie, używa Jetpack Compose do UI i zachowuje architekturę MVVM z Repository Pattern.

Aktualnie aplikacja działa lokalnie:

- dane użytkowników, rowerów, sklepów i wypożyczeń są przechowywane w lokalnej bazie Room,
- początkowe dane rowerów, sklepów i konta administratora są seedowane z `DataSource.kt`,
- nie ma jeszcze zewnętrznego backendu AWS,
- kod jest przygotowany tak, żeby później można było wymienić implementacje repozytoriów na wywołania API.

## 2. Najważniejsze technologie

- Kotlin - główny język aplikacji.
- Jetpack Compose - warstwa interfejsu użytkownika.
- Material 3 - komponenty UI.
- Navigation Compose - nawigacja między ekranami.
- MVVM - podział na UI, ViewModel i warstwę danych.
- Repository Pattern - ekrany i ViewModele nie odwołują się bezpośrednio do Room.
- Room - lokalna baza SQLite.
- KSP - generowanie kodu dla Room.
- Kotlin Coroutines i StateFlow - obsługa stanu i operacji asynchronicznych.
- Coil - ładowanie obrazów z URL.

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
└── AdminPanelScreen.kt
```

Opis:

- `LoginScreen.kt` - ekran logowania i rejestracji. Obsługuje e-mail, hasło, potwierdzenie hasła i przycisk Google jako zaślepkę.
- `HomeScreen.kt` - ekran główny z listą rowerów.
- `BikeDetailScreen.kt` - szczegóły wybranego roweru i akcja wypożyczenia.
- `ShopProfileScreen.kt` - profil wypożyczalni/sklepu.
- `RentalsScreen.kt` - aktywne wypożyczenia i historia wypożyczeń.
- `ProfileScreen.kt` - profil użytkownika.
- `UserSettingsScreen.kt` - edycja danych użytkownika.
- `AdminPanelScreen.kt` - panel administratora, aktualnie korzysta z danych administracyjnych z `DataSource`.

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

Zdefiniowane trasy:

- `login`
- `home`
- `bike/{bikeId}`
- `shop/{shopId}`
- `rentals`
- `profile`
- `settings`
- `admin`

Aplikacja startuje od ekranu `login`. Po udanym logowaniu lub rejestracji `AuthViewModel` ustawia `AuthState.Success`, a `AppNavigation` inicjalizuje `AppViewModel` dla zalogowanego użytkownika.

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
- trzymanie aktualnej sesji użytkownika w pamięci aplikacji.

Stan autoryzacji jest opisany przez `AuthState`:

- `Idle`
- `Loading`
- `Success`
- `Error`

Hasło wpisane przez użytkownika jest hashowane przez `HashUtils.sha256`, a dopiero potem przekazywane do `UserRepository`.

### `AppViewModel.kt`

Odpowiada za główne dane aplikacji:

- listę rowerów,
- listę sklepów,
- aktywne wypożyczenia,
- historię wypożyczeń,
- wypożyczenie roweru,
- zwrot roweru.

ViewModel nie zna bezpośrednio Room ani DAO. Korzysta tylko z interfejsów repozytoriów:

- `BikeRepository`
- `ShopRepository`
- `RentalRepository`

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
```

### `Models.kt`

Zawiera modele domenowe używane przez UI i ViewModele:

- `Review`
- `Bike`
- `Shop`
- `ActiveRental`
- `RentalHistory`
- `SeedUser`
- `AdminUser`
- `AdminComment`

Modele domenowe nie są tym samym co encje Room. Repozytoria mapują encje bazy danych na modele domenowe.

### `DataSource.kt`

Zawiera tymczasowe dane startowe:

- rowery,
- sklepy,
- konto administratora.

Dane z `DataSource` są używane do seedowania bazy Room przy pierwszym utworzeniu bazy. W repozytorium nie ma seedowanych zwykłych kont użytkowników, recenzji ani komentarzy. Jest tylko seedowane konto administratora z hasłem zapisanym jako SHA-256.

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
│   ├── ShopDao.kt
│   └── UserDao.kt
└── entity/
    ├── ActiveRentalEntity.kt
    ├── BikeEntity.kt
    ├── RentalHistoryEntity.kt
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
1
```

Encje:

- `UserEntity`
- `BikeEntity`
- `ShopEntity`
- `ActiveRentalEntity`
- `RentalHistoryEntity`

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
- `rating`
- `image`
- `images`
- `description`
- `available`
- `shopId`
- `category`
- `reviews`

Listy `images` i `reviews` są zapisywane przez konwertery Room.

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

Lista `bikeIds` jest zapisywana przez konwertery Room.

#### `active_rentals`

Encja: `ActiveRentalEntity`

Pola:

- `id`
- `bikeId`
- `bikeName`
- `shopName`
- `startTime`
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
- `date`
- `duration`
- `cost`
- `userId`

Relacja:

- `userId` jest kluczem obcym do tabeli `users`,
- usunięcie użytkownika usuwa jego historię wypożyczeń przez `CASCADE`.

### Konwertery Room

Plik:

```text
data/db/converter/Converters.kt
```

Room nie zapisuje automatycznie list złożonych typów, dlatego używane są konwertery:

- `List<String>` jest zapisywane jako tekst rozdzielony znakiem `|`,
- `List<Review>` jest zapisywane jako JSON.

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

- dodawanie listy rowerów,
- pobranie wszystkich rowerów,
- pobranie roweru po id,
- zliczenie rowerów.

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

## 12. Repozytoria

Repozytoria znajdują się w:

```text
data/repository/
├── BikeRepository.kt
├── ShopRepository.kt
├── RentalRepository.kt
├── UserRepository.kt
└── impl/
    ├── BikeRepositoryImpl.kt
    ├── ShopRepositoryImpl.kt
    ├── RentalRepositoryImpl.kt
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
- pobranie roweru po id.

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

Przepływ zwrotu:

```text
RentalsScreen -> AppViewModel.returnBike -> RentalRepository.returnBike
```

Podczas zwrotu:

- aktywne wypożyczenie jest usuwane z `active_rentals`,
- tworzony jest wpis w `rental_history`,
- koszt jest liczony na podstawie ceny roweru i założonego czasu 4 godzin.

## 15. Zasoby aplikacji

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

Jest ono potrzebne m.in. do ładowania obrazów z URL przez Coil.

## 16. Konfiguracja Gradle

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
- KSP: `2.2.10-2.0.2`
- Room: `2.8.4`
- Compose BOM: `2026.02.01`

### `gradle.properties`

Zawiera ustawienia Gradle i Kotlin.

W projekcie ustawiono też:

```properties
android.disallowKotlinSourceSets=false
```

To jest flaga kompatybilności potrzebna przy obecnym połączeniu AGP/Kotlin/KSP, żeby kod generowany przez KSP był poprawnie traktowany jako źródła Kotlin.

## 17. Testy

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

## 18. Aktualne ograniczenia

- Brak zewnętrznego backendu AWS.
- Brak trwałego zapamiętywania sesji użytkownika po restarcie aplikacji.
- Google login jest tylko zaślepką.
- Panel administratora nie ma jeszcze kontroli dostępu po roli użytkownika.
- Listy użytkowników i komentarzy w panelu administratora są puste; nie seedujemy przykładowych danych osobowych ani komentarzy.
- Migracje Room nie są jeszcze zdefiniowane, bo baza jest w wersji `1`.
- Haszowanie SHA-256 spełnia wymaganie projektu, ale w produkcyjnej aplikacji lepszym wyborem byłby algorytm z solą i kosztem obliczeniowym, np. bcrypt, scrypt albo Argon2.
