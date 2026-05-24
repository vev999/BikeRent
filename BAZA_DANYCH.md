# BikeRent – Baza danych (Room / SQLite)

## Ogólne informacje

| Parametr | Wartość |
|----------|---------|
| Biblioteka | Room (Jetpack) |
| Silnik | SQLite |
| Nazwa pliku | `bikerent.db` |
| Wersja schematu | 3 |
| Migracja | `fallbackToDestructiveMigration()` – przy zmianie wersji kasuje i odtwarza bazę |
| Singleton | `BikeRentDatabase.getInstance(context)` |

---

## Tabele

### 1. `users`

Przechowuje konta użytkowników (zwykłych i adminów).

| Kolumna | Typ SQLite | Ograniczenia | Opis |
|---------|-----------|-------------|------|
| `id` | INTEGER | PRIMARY KEY AUTOINCREMENT | Identyfikator użytkownika |
| `email` | TEXT | NOT NULL, UNIQUE | Adres e-mail (unikalny – indeks) |
| `name` | TEXT | NOT NULL | Imię / nazwa wyświetlana |
| `passwordHash` | TEXT | NOT NULL | Skrót SHA-256 hasła |
| `avatarUri` | TEXT | NULL | Ścieżka do zdjęcia awatara (null = brak) — dodane w wersji 3 |

**Indeksy:** `idx_users_email` (UNIQUE) na kolumnie `email`

**Dane startowe (seed):**
```
id=0, name="Administrator", email="admin@bikerent.local"
hasło (jawne): "Admin123!" → hash SHA-256 przechowywany w bazie
```

---

### 2. `bikes`

Katalog rowerów dostępnych do wypożyczenia.

| Kolumna | Typ SQLite | Ograniczenia | Opis |
|---------|-----------|-------------|------|
| `id` | TEXT | PRIMARY KEY | Identyfikator roweru |
| `name` | TEXT | NOT NULL | Nazwa roweru |
| `price` | INTEGER | NOT NULL | Cena za godzinę (PLN) |
| `rating` | REAL | NOT NULL | Średnia ocena (0.0 – 5.0), dynamicznie aktualizowana |
| `image` | TEXT | NOT NULL | URL głównego zdjęcia |
| `images` | TEXT | NOT NULL | Lista URL zdjęć serializowana jako `url1\|url2\|...` |
| `description` | TEXT | NOT NULL | Opis roweru |
| `available` | INTEGER | NOT NULL | Dostępność: 1 = dostępny, 0 = niedostępny |
| `shopId` | TEXT | NOT NULL | ID sklepu, do którego należy rower |
| `category` | TEXT | NOT NULL | Kategoria |

> `images` to lista stringów serializowana przez `Converters` (`joinToString("|")` / `split("|")`).

**Dane startowe (seed) – 12 rowerów, wszystkie dostępne:**

| id | name | price | category | shopId |
|----|------|-------|----------|--------|
| 1 | Urban City Bike | 15 zł/h | Miejski | 1 |
| 2 | Mountain Explorer | 25 zł/h | Górski | 1 |
| 3 | E-Bike Pro | 35 zł/h | Elektryczny | 2 |
| 4 | Racing Speed | 30 zł/h | Szosowy | 2 |
| 5 | Beach Cruiser | 18 zł/h | Cruiser | 1 |
| 6 | Hybrid Commuter | 20 zł/h | Hybrydowy | 2 |
| 7 | Kids Explorer | 10 zł/h | Dziecięcy | 3 |
| 8 | Tandem Adventure | 40 zł/h | Tandem | 3 |
| 9 | Urban Folder | 22 zł/h | Składany | 3 |
| 10 | Cargo Express | 28 zł/h | Cargo | 4 |
| 11 | Gravel Master | 32 zł/h | Gravel | 4 |
| 12 | BMX Street | 20 zł/h | BMX | 4 |

---

### 3. `shops`

Sklepy / wypożyczalnie rowerów.

| Kolumna | Typ SQLite | Ograniczenia | Opis |
|---------|-----------|-------------|------|
| `id` | TEXT | PRIMARY KEY | Identyfikator sklepu |
| `name` | TEXT | NOT NULL | Nazwa sklepu |
| `description` | TEXT | NOT NULL | Opis działalności |
| `location` | TEXT | NOT NULL | Adres |
| `rating` | REAL | NOT NULL | Ocena sklepu |
| `image` | TEXT | NOT NULL | URL zdjęcia sklepu |
| `bikeIds` | TEXT | NOT NULL | Lista ID rowerów serializowana jako `id1\|id2\|...` |

**Dane startowe (seed) – 4 sklepy:**

| id | name | location | rating | rowery |
|----|------|----------|--------|--------|
| 1 | BikeHub Centrum | ul. Główna 15, Warszawa | 4.9 | 1, 2, 5 |
| 2 | EcoBike Station | ul. Zielona 42, Warszawa | 4.7 | 3, 4, 6 |
| 3 | VeloCity Praga | ul. Targowa 7, Warszawa | 4.8 | 7, 8, 9 |
| 4 | GreenWheels Mokotów | ul. Puławska 100, Warszawa | 4.6 | 10, 11, 12 |

---

### 4. `active_rentals`

Aktywne (trwające) wypożyczenia.

| Kolumna | Typ SQLite | Ograniczenia | Opis |
|---------|-----------|-------------|------|
| `id` | TEXT | PRIMARY KEY | UUID wypożyczenia |
| `bikeId` | TEXT | NOT NULL | ID wypożyczonego roweru |
| `bikeName` | TEXT | NOT NULL | Nazwa roweru (denormalizacja) |
| `shopName` | TEXT | NOT NULL | Nazwa sklepu (denormalizacja) |
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

**Logika obliczania kosztu (w `RentalRepositoryImpl.returnBike`):**
```
koszt = cena_roweru_za_godzinę × ceil(czas_trwania_w_godzinach)
minimum: 1 godzina
```

---

### 6. `reviews`

Recenzje rowerów wystawiane przez użytkowników.

| Kolumna | Typ SQLite | Ograniczenia | Opis |
|---------|-----------|-------------|------|
| `id` | TEXT | PRIMARY KEY | `"rev_${System.currentTimeMillis()}"` |
| `bikeId` | TEXT | NOT NULL | ID ocenianego roweru |
| `bikeName` | TEXT | NOT NULL | Nazwa roweru (denormalizacja) |
| `userId` | INTEGER | NOT NULL | ID autora |
| `userName` | TEXT | NOT NULL | Imię autora (denormalizacja) |
| `rating` | REAL | NOT NULL | Ocena 1–5 |
| `comment` | TEXT | NOT NULL | Treść komentarza |
| `date` | TEXT | NOT NULL | Data wystawienia (dd.MM.yyyy) |

**Brak klucza obcego** do `users` (recenzje pozostają po usunięciu konta – celowy projekt)

**Indeksy:** `idx_reviews_bikeId` na `bikeId`, `idx_reviews_userId` na `userId`

**Ograniczenie biznesowe:** jeden użytkownik może wystawić tylko jedną recenzję danemu rowerowi (sprawdzane przez `ReviewDao.findByUserAndBike`).

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

`BikeRentDatabase.SeedCallback` implementuje `RoomDatabase.Callback` i jest wywoływany przy `onCreate` oraz `onOpen`. Wstawia dane z `DataSource` używając strategii `IGNORE` (nie nadpisuje istniejących rekordów).

Kolejność seedowania:
1. `users` – konta adminów
2. `bikes` – 12 predefiniowanych rowerów
3. `shops` – 4 predefiniowane sklepy

---

## Operacje DAO – podsumowanie

| DAO | Metody |
|-----|--------|
| `UserDao` | `insert` (ABORT), `insertAll` (IGNORE), `findByEmail`, `findByEmailAndPassword`, `findById`, `updateNameAndEmail`, `updateAvatarUri` |
| `BikeDao` | `insert` (REPLACE), `insertAll` (REPLACE), `getAll`, `findById`, `count`, `updateRating` |
| `ShopDao` | `insertAll` (REPLACE), `getAll`, `findById`, `count` |
| `ActiveRentalDao` | `insert` (REPLACE), `getAllForUser(userId)`, `deleteById` |
| `RentalHistoryDao` | `insert` (REPLACE), `getAllForUser(userId)` |
| `ReviewDao` | `insert` (REPLACE), `getAllForBike`, `getAllForUser`, `getAll`, `deleteById`, `findByUserAndBike` |

---

## Historia wersji schematu

| Wersja | Zmiana |
|--------|--------|
| 1 | Wersja inicjalna: users, bikes, shops, active_rentals, rental_history |
| 2 | Dodanie tabeli `reviews` + ReviewDao |
| 3 | Dodanie kolumny `avatarUri TEXT NULL` do tabeli `users` |
