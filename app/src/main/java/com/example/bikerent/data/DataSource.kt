package com.example.bikerent.data

object DataSource {

    private const val RES = "android.resource://com.example.bikerent/drawable/"

    val bikes = listOf(
        Bike(
            id = "1", name = "Urban City Bike", price = 15, rating = 0f,
            image = "${RES}bike_city_1",
            images = listOf("${RES}bike_city_1", "${RES}bike_city_2"),
            description = "Idealny rower do codziennych dojazdów po mieście. Lekki, wygodny i niezawodny.",
            available = true, shopId = "1", category = "Miejski"
        ),
        Bike(
            id = "2", name = "Mountain Explorer", price = 25, rating = 0f,
            image = "${RES}bike_mountain_1",
            images = listOf("${RES}bike_mountain_1", "${RES}bike_mountain_2"),
            description = "Rower górski dla prawdziwych poszukiwaczy przygód. Wytrzymała rama aluminiowa i amortyzacja.",
            available = true, shopId = "1", category = "Górski"
        ),
        Bike(
            id = "3", name = "E-Bike Pro", price = 35, rating = 0f,
            image = "${RES}bike_ebike_1",
            images = listOf("${RES}bike_ebike_1", "${RES}bike_ebike_2"),
            description = "Elektryczny rower z najnowszą technologią. Zasięg do 80 km na jednym ładowaniu.",
            available = true, shopId = "2", category = "Elektryczny"
        ),
        Bike(
            id = "4", name = "Racing Speed", price = 30, rating = 0f,
            image = "${RES}bike_road_1",
            images = listOf("${RES}bike_road_1"),
            description = "Szosowy rower wyścigowy. Lekkość i szybkość w jednym — karbon i aerodynamika.",
            available = true, shopId = "2", category = "Szosowy"
        ),
        Bike(
            id = "5", name = "Beach Cruiser", price = 18, rating = 0f,
            image = "${RES}bike_cruiser_1",
            images = listOf("${RES}bike_cruiser_1", "${RES}bike_cruiser_2"),
            description = "Komfortowy rower cruiser na spokojne przejażdżki. Szeroka kierownica i miękkie siodło.",
            available = true, shopId = "1", category = "Cruiser"
        ),
        Bike(
            id = "6", name = "Hybrid Commuter", price = 20, rating = 0f,
            image = "${RES}bike_hybrid_1",
            images = listOf("${RES}bike_hybrid_1"),
            description = "Uniwersalny rower hybrydowy na każdą drogę. Sprawdzi się w mieście i na szutrze.",
            available = true, shopId = "2", category = "Hybrydowy"
        ),
        Bike(
            id = "7", name = "Kids Explorer", price = 10, rating = 0f,
            image = "${RES}bike_kids_1",
            images = listOf("${RES}bike_kids_1", "${RES}bike_kids_2"),
            description = "Bezpieczny rower dla dzieci w wieku 5–10 lat. Stabilne kółka boczne w zestawie.",
            available = true, shopId = "3", category = "Dziecięcy"
        ),
        Bike(
            id = "8", name = "Tandem Adventure", price = 40, rating = 0f,
            image = "${RES}bike_tandem_1",
            images = listOf("${RES}bike_tandem_1"),
            description = "Rower tandem dla dwóch osób. Idealna propozycja na romantyczną wycieczkę.",
            available = true, shopId = "3", category = "Tandem"
        ),
        Bike(
            id = "9", name = "Urban Folder", price = 22, rating = 0f,
            image = "${RES}bike_folder_1",
            images = listOf("${RES}bike_folder_1", "${RES}bike_folder_2"),
            description = "Składany rower miejski. Zmieści się w bagażniku auta lub przedziale metra.",
            available = true, shopId = "3", category = "Składany"
        ),
        Bike(
            id = "10", name = "Cargo Express", price = 28, rating = 0f,
            image = "${RES}bike_cargo_1",
            images = listOf("${RES}bike_cargo_1"),
            description = "Rower cargo z dużym bagażnikiem przednim. Uniesie do 80 kg ładunku.",
            available = true, shopId = "4", category = "Cargo"
        ),
        Bike(
            id = "11", name = "Gravel Master", price = 32, rating = 0f,
            image = "${RES}bike_gravel_1",
            images = listOf("${RES}bike_gravel_1", "${RES}bike_gravel_2"),
            description = "Rower gravel na utwardzone i gruntowe drogi. Szersze opony i komfortowa geometria.",
            available = true, shopId = "4", category = "Gravel"
        ),
        Bike(
            id = "12", name = "BMX Street", price = 20, rating = 0f,
            image = "${RES}bike_bmx_1",
            images = listOf("${RES}bike_bmx_1"),
            description = "Wytrzymały rower BMX do jazdy ulicznej i parkowej. Solidna stalowa rama.",
            available = true, shopId = "4", category = "BMX"
        )
    )

    val shops = listOf(
        Shop(
            id = "1", name = "BikeHub Centrum",
            description = "Najlepsza wypożyczalnia rowerów w centrum miasta. Oferujemy szeroki wybór rowerów na każdą okazję. Otwarte 7 dni w tygodniu.",
            location = "ul. Główna 15, Warszawa", rating = 4.9f,
            image = "${RES}shop_1",
            bikeIds = listOf("1", "2", "5")
        ),
        Shop(
            id = "2", name = "EcoBike Station",
            description = "Wypożyczalnia specjalizująca się w rowerach elektrycznych i ekologicznych rozwiązaniach mobilności miejskiej.",
            location = "ul. Zielona 42, Warszawa", rating = 4.7f,
            image = "${RES}shop_2",
            bikeIds = listOf("3", "4", "6")
        ),
        Shop(
            id = "3", name = "VeloCity Praga",
            description = "Klimatyczna wypożyczalnia na Pradze z bogatą ofertą rowerów rodzinnych. Idealna baza wypadowa nad Wisłę.",
            location = "ul. Targowa 7, Warszawa", rating = 4.8f,
            image = "${RES}shop_3",
            bikeIds = listOf("7", "8", "9")
        ),
        Shop(
            id = "4", name = "GreenWheels Mokotów",
            description = "Nowoczesna wypożyczalnia na Mokotowie. Specjalizujemy się w rowerach cargo i gravel dla aktywnych.",
            location = "ul. Puławska 100, Warszawa", rating = 4.6f,
            image = "${RES}shop_4",
            bikeIds = listOf("10", "11", "12")
        )
    )

    // Admin-only seed — email is used by AuthViewModel.isAdmin
    val seededAdminUsers = listOf(
        SeedUser(
            id = 0L, name = "Administrator", email = "admin@bikerent.local",
            passwordHash = "3eb3fe66b31e3b4d10fa70b5cad49c7112294af6ae4e476a1c405155d45aa121"
        )
    )

    // Regular users seeded for demo (not admins)
    // Anna: hasło "rower2024", Paweł: hasło "haslo123"
    val seededRegularUsers = listOf(
        SeedUser(
            id = 0L, name = "Anna Kowalska", email = "anna.k@mail.com",
            passwordHash = "34a81ca3f394c9ef5d55fca1ed7c6356416daea88a381f3563234d6d29c9a426"
        ),
        SeedUser(
            id = 0L, name = "Paweł Nowak", email = "pawel.n@mail.com",
            passwordHash = "a15f8ae07675bfb96e084bfb4f52fb2c22091061aae86e0eb76a55f4e52dd74e"
        )
    )
}
