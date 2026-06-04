# BikeRent – Baza danych (Room / SQLite)

## Informacje ogólne

| Parametr | Wartość |
|----------|---------|
| Biblioteka | Room (Jetpack) |
| Silnik | SQLite |
| Nazwa pliku | `bikerent.db` |
| Wersja schematu | 4 |
| Migracja | `fallbackToDestructiveMigration()` – przy zmianie wersji kasuje i odtwarza bazę |
| Singleton | `BikeRentDatabase.getInstance(context)` |

---

## Tabele

### 1. `users`

Przechowuje konta użytkowników (zwykłych i adminów).

| Kolumna | Typ SQLite | Ograniczenia | Opis |
|---------|-----------|-------------|------|
| `id` | INTEGER | PRIMARY KEY AUTOINCREMENT | Identyfikator użytkownika |
| `email` | TEXT | NOT NULL, UNIQUE | Adres e-mail |
| `name` | TEXT | NOT NULL | Imię / nazwa wyświetlana |
| `passwordHash` | TEXT | NOT NULL | Skrót SHA-256 hasła |
| `avatarUri` | TEXT | NULL | Ścieżka do zdjęcia awatara (null = brak) |

**Indeksy:** `idx_users_email` (UNIQUE) na `email`

**Konta seedowane:**

| E-mail | Hasło (jawne) | Rola |
|--------|--------------|------|
| `admin@bikerent.local` | `Admin123!` | Administrator |
| `anna.k@mail.com` | `rower2024` | Użytkownik |
| `pawel.n@mail.com` | `haslo123` | Użytkownik |

---

### 2. `bikes`

Katalog rowerów dostępnych do wypożyczenia.

| Kolumna | Typ SQLite | Ograniczenia | Opis |
|---------|-----------|-------------|------|
| `id` | TEXT | PRIMARY KEY | Identyfikator roweru |
| `name` | TEXT | NOT NULL | Nazwa roweru |
| `price` | INTEGER | NOT NULL | Cena za dzień (PLN) |
| `rating` | REAL | NOT NULL | Średnia ocena (0.0–5.0), aktualizowana po każdej opinii |
| `image` | TEXT | NOT NULL | Nazwa zasobu drawable (główne zdjęcie) |
| `images` | TEXT | NOT NULL | Lista nazw zasobów drawable, separator `\|` |
| `description` | TEXT | NOT NULL | Opis roweru |
| `available` | INTEGER | NOT NULL | Dostępność: 1 = dostępny, 0 = niedostępny |
| `shopId` | TEXT | NOT NULL | ID sklepu, do którego należy rower |
| `category` | TEXT | NOT NULL | Kategoria roweru |

> `images` to lista stringów serializowana przez `Converters` (`joinToString("|")` / `split("|")`).

**Dane startowe – 12 rowerów, wszystkie dostępne:**

| id | name | price | category | shopId |
|----|------|-------|----------|--------|
| 1 | Urban City Bike | 15 zł/dzień | Miejski | 1 |
| 2 | Mountain Explorer | 25 zł/dzień | Górski | 1 |
| 3 | E-Bike Pro | 35 zł/dzień | Elektryczny | 2 |
| 4 | Racing Speed | 30 zł/dzień | Szosowy | 2 |
| 5 | Beach Cruiser | 18 zł/dzień | Cruiser | 1 |
| 6 | Hybrid Commuter | 20 zł/dzień | Hybrydowy | 2 |
| 7 | Kids Explorer | 10 zł/dzień | Dziecięcy | 3 |
| 8 | Tandem Adventure | 40 zł/dzień | Tandem | 3 |
| 9 | Urban Folder | 22 zł/dzień | Składany | 3 |
| 10 | Cargo Express | 28 zł/dzień | Cargo | 4 |
| 11 | Gravel Master | 32 zł/dzień | Gravel | 4 |
| 12 | BMX Street | 20 zł/dzień | BMX | 4 |

---

### 3. `shops`

Sklepy / wypożyczalnie rowerów.

| Kolumna | Typ SQLite | Ograniczenia | Opis |
|---------|-----------|-------------|------|
| `id` | TEXT | PRIMARY KEY | Identyfikator sklepu |
| `name` | TEXT | NOT NULL | Nazwa sklepu |
| `description` | TEXT | NOT NULL | Opis działalności |
| `location` | TEXT | NOT NULL | Adres |
| `rating` | REAL | NOT NULL | Wartość rezerwowa; w UI ocena jest obliczana dynamicznie jako średnia ocen rowerów sklepu z co najmniej jedną opinią |
| `image` | TEXT | NOT NULL | Nazwa zasobu drawable |
| `bikeIds` | TEXT | NOT NULL | Lista ID rowerów, separator `\|` |

**Dane startowe – 4 sklepy:**

| id | name | location | rowery |
|----|------|----------|--------|
| 1 | BikeHub Centrum | ul. Główna 15, Warszawa | 1, 2, 5 |
| 2 | EcoBike Station | ul. Zielona 42, Warszawa | 3, 4, 6 |
| 3 | VeloCity Praga | ul. Targowa 7, Warszawa | 7, 8, 9 |
| 4 | GreenWheels Mokotów | ul. Puławska 100, Warszawa | 10, 11, 12 |

---

### 4. `active_rentals`

Aktywne (trwające) wypożyczenia.

| Kolumna | Typ SQLite | Ograniczenia | Opis |
|---------|-----------|-------------|------|
| `id` | TEXT | PRIMARY KEY | UUID wypożyczenia |
| `bikeId` | TEXT | NOT NULL | ID wypożyczonego roweru |
| `bikeName` | TEXT | NOT NULL | Nazwa roweru |
| `shopName` | TEXT | NOT NULL | Nazwa sklepu |
| `startTime` | TEXT | NOT NULL | Data i godzina rozpoczęcia (dd.MM.yyyy HH:mm) |
| `endTime` | TEXT | NOT NULL | Planowany czas zwrotu |
| `returnLocation` | TEXT | NOT NULL | Miejsce zwrotu |
| `userId` | INTEGER | NOT NULL, FK → users.id CASCADE | Właściciel wypożyczenia |

**Klucz obcy:** `userId` → `users.id` z `ON DELETE CASCADE`

**Indeksy:** `idx_active_rentals_userId` na `userId`

---

### 5. `rental_history`

Historia zakończonych wypożyczeń.

| Kolumna | Typ SQLite | Ograniczenia | Opis |
|---------|-----------|-------------|------|
| `id` | TEXT | PRIMARY KEY | UUID rekordu historii |
| `bikeName` | TEXT | NOT NULL | Nazwa roweru |
| `shopName` | TEXT | NOT NULL | Nazwa sklepu |
| `date` | TEXT | NOT NULL | Zakres dat (dd.MM.yyyy HH:mm – dd.MM.yyyy HH:mm) |
| `duration` | TEXT | NOT NULL | Czas trwania (np. „2 h 15 min") |
| `cost` | INTEGER | NOT NULL | Koszt w PLN |
| `userId` | INTEGER | NOT NULL, FK → users.id CASCADE | Właściciel historii |

**Klucz obcy:** `userId` → `users.id` z `ON DELETE CASCADE`

**Indeksy:** `idx_rental_history_userId` na `userId`

**Obliczanie kosztu:** `cena_roweru_za_dzień × ceil(czas_trwania_w_godzinach)`, minimum 1 godzina.

---

### 6. `reviews`

Recenzje rowerów wystawiane przez użytkowników.

| Kolumna | Typ SQLite | Ograniczenia | Opis |
|---------|-----------|-------------|------|
| `id` | TEXT | PRIMARY KEY | Stałe ID dla seedowanych (`seed_rev_XX`); `"rev_${currentTimeMillis()}"` dla nowych |
| `bikeId` | TEXT | NOT NULL | ID ocenianego roweru |
| `bikeName` | TEXT | NOT NULL | Nazwa roweru |
| `userId` | INTEGER | NOT NULL | ID autora |
| `userName` | TEXT | NOT NULL | Imię autora |
| `rating` | REAL | NOT NULL | Ocena 1–5 |
| `comment` | TEXT | NOT NULL | Treść komentarza |
| `date` | TEXT | NOT NULL | Data wystawienia (dd.MM.yyyy) |

**Indeksy:** `idx_reviews_bikeId` na `bikeId`, `idx_reviews_userId` na `userId`

**Ograniczenie biznesowe:** jeden użytkownik = jedna opinia na rower (sprawdzane przez `findByUserAndBike`).

**Dane startowe – 10 opinii** wystawionych przez Annę i Pawła dla rowerów 1, 2, 3, 4, 5, 7, 9, 11, 12. Po seedowaniu oceny tych rowerów są przeliczane i aktualizowane w tabeli `bikes`.

---

## Diagram relacji (ERD)

```
users (PK: id)
  │
  ├──< active_rentals (FK: userId → users.id CASCADE)
  │
  └──< rental_history (FK: userId → users.id CASCADE)

bikes (PK: id)  ──── shopId ────> shops (PK: id)

reviews: bikeId → bikes, userId → users  (bez FK, tylko logicznie)
```

---

## TypeConverters

Plik: `data/db/converter/Converters.kt`

Room nie obsługuje natywnie kolekcji — `List<String>` jest serializowana do jednego pola TEXT:

| Konwersja | Logika |
|-----------|--------|
| `List<String>` → `String` | `list.joinToString("|")` |
| `String` → `List<String>` | `value.split("|")` (puste → `emptyList()`) |

Używane w: `bikes.images`, `shops.bikeIds`

---

## Seedowanie bazy (`SeedCallback`)

`SeedCallback` implementuje `RoomDatabase.Callback` i jest rejestrowany przez `addCallback()` przy budowaniu instancji bazy.

### `onCreate` – pierwsze uruchomienie (lub po rekreacji bazy)

1. Seeduje użytkowników (`IGNORE`) – admin, Anna, Paweł
2. Seeduje rowery (`REPLACE`) – 12 rowerów z `DataSource`
3. Seeduje sklepy (`REPLACE`) – 4 sklepy z `DataSource`
4. Seeduje opinie (`IGNORE`) – 10 startowych recenzji, następnie przelicza i zapisuje oceny rowerów

### `onOpen` – każde kolejne uruchomienie

1. Seeduje użytkowników (`IGNORE`) – bezpieczne, nie nadpisuje istniejących
2. Seeduje opinie (`IGNORE`) – bezpieczne, nie duplikuje (stałe ID `seed_rev_XX`), następnie przelicza oceny

Rowery i sklepy **nie są seedowane w `onOpen`**, aby nie nadpisywać ocen rowerów zaktualizowanych przez użytkowników.

---

## DAO – podsumowanie

| DAO | Metody |
|-----|--------|
| `UserDao` | `insert` (ABORT), `insertAll` (IGNORE), `findByEmail`, `findByEmailAndPassword`, `findById`, `updateNameAndEmail`, `updateAvatarUri` |
| `BikeDao` | `insert` (REPLACE), `insertAll` (REPLACE), `getAll`, `findById`, `count`, `updateRating` |
| `ShopDao` | `insertAll` (REPLACE), `getAll`, `findById`, `count` |
| `ActiveRentalDao` | `insert` (REPLACE), `getAllForUser(userId)`, `deleteById` |
| `RentalHistoryDao` | `insert` (REPLACE), `getAllForUser(userId)` |
| `ReviewDao` | `insert` (REPLACE), `insertAll` (IGNORE), `getAllForBike`, `getAllForUser`, `getAll`, `deleteById`, `findByUserAndBike` |
