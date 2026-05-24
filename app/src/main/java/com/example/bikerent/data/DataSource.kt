package com.example.bikerent.data

object DataSource {

    val bikes = listOf(
        Bike(
            id = "1", name = "Urban City Bike", price = 15, rating = 0f,
            image = "https://images.unsplash.com/photo-1760588774918-769cb07ab9c8?w=800",
            images = listOf(
                "https://images.unsplash.com/photo-1760588774918-769cb07ab9c8?w=800",
                "https://images.unsplash.com/photo-1692668696811-90976b749459?w=800"
            ),
            description = "Idealny rower do codziennych dojazdów po mieście. Lekki, wygodny i niezawodny.",
            available = true, shopId = "1", category = "Miejski"
        ),
        Bike(
            id = "2", name = "Mountain Explorer", price = 25, rating = 0f,
            image = "https://images.unsplash.com/photo-1571660973831-70d6fc86c1d6?w=800",
            images = listOf(
                "https://images.unsplash.com/photo-1571660973831-70d6fc86c1d6?w=800",
                "https://images.unsplash.com/photo-1589186102699-94b04c18c9e3?w=800"
            ),
            description = "Rower górski dla prawdziwych poszukiwaczy przygód. Wytrzymała rama aluminiowa i amortyzacja.",
            available = true, shopId = "1", category = "Górski"
        ),
        Bike(
            id = "3", name = "E-Bike Pro", price = 35, rating = 0f,
            image = "https://images.unsplash.com/photo-1692668696811-90976b749459?w=800",
            images = listOf(
                "https://images.unsplash.com/photo-1692668696811-90976b749459?w=800",
                "https://images.unsplash.com/photo-1560185009-dddeb820c7b7?w=800"
            ),
            description = "Elektryczny rower z najnowszą technologią. Zasięg do 80 km na jednym ładowaniu.",
            available = true, shopId = "2", category = "Elektryczny"
        ),
        Bike(
            id = "4", name = "Racing Speed", price = 30, rating = 0f,
            image = "https://images.unsplash.com/photo-1525996596318-edf2b6b64e60?w=800",
            images = listOf("https://images.unsplash.com/photo-1525996596318-edf2b6b64e60?w=800"),
            description = "Szosowy rower wyścigowy. Lekkość i szybkość w jednym — karbon i aerodynamika.",
            available = true, shopId = "2", category = "Szosowy"
        ),
        Bike(
            id = "5", name = "Beach Cruiser", price = 18, rating = 0f,
            image = "https://images.unsplash.com/photo-1618520826503-12a0dcaa549e?w=800",
            images = listOf(
                "https://images.unsplash.com/photo-1618520826503-12a0dcaa549e?w=800",
                "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=800"
            ),
            description = "Komfortowy rower cruiser na spokojne przejażdżki. Szeroka kierownica i miękkie siodło.",
            available = true, shopId = "1", category = "Cruiser"
        ),
        Bike(
            id = "6", name = "Hybrid Commuter", price = 20, rating = 0f,
            image = "https://images.unsplash.com/photo-1759047990878-b5a1e95f81fd?w=800",
            images = listOf("https://images.unsplash.com/photo-1759047990878-b5a1e95f81fd?w=800"),
            description = "Uniwersalny rower hybrydowy na każdą drogę. Sprawdzi się w mieście i na szutrze.",
            available = true, shopId = "2", category = "Hybrydowy"
        ),
        Bike(
            id = "7", name = "Kids Explorer", price = 10, rating = 0f,
            image = "https://images.unsplash.com/photo-1526506118085-60ce8714f8c5?w=800",
            images = listOf(
                "https://images.unsplash.com/photo-1526506118085-60ce8714f8c5?w=800",
                "https://images.unsplash.com/photo-1574158622682-e40e69881006?w=800"
            ),
            description = "Bezpieczny rower dla dzieci w wieku 5–10 lat. Stabilne kółka boczne w zestawie.",
            available = true, shopId = "3", category = "Dziecięcy"
        ),
        Bike(
            id = "8", name = "Tandem Adventure", price = 40, rating = 0f,
            image = "https://images.unsplash.com/photo-1501147830916-ce44a6359892?w=800",
            images = listOf("https://images.unsplash.com/photo-1501147830916-ce44a6359892?w=800"),
            description = "Rower tandem dla dwóch osób. Idealna propozycja na romantyczną wycieczkę.",
            available = true, shopId = "3", category = "Tandem"
        ),
        Bike(
            id = "9", name = "Urban Folder", price = 22, rating = 0f,
            image = "https://images.unsplash.com/photo-1485965120184-e220f721d03e?w=800",
            images = listOf(
                "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?w=800",
                "https://images.unsplash.com/photo-1571902943202-507ec2618e8f?w=800"
            ),
            description = "Składany rower miejski. Zmieści się w bagażniku auta lub przedziale metra.",
            available = true, shopId = "3", category = "Składany"
        ),
        Bike(
            id = "10", name = "Cargo Express", price = 28, rating = 0f,
            image = "https://images.unsplash.com/photo-1571902943202-507ec2618e8f?w=800",
            images = listOf("https://images.unsplash.com/photo-1571902943202-507ec2618e8f?w=800"),
            description = "Rower cargo z dużym bagażnikiem przednim. Uniesie do 80 kg ładunku.",
            available = true, shopId = "4", category = "Cargo"
        ),
        Bike(
            id = "11", name = "Gravel Master", price = 32, rating = 0f,
            image = "https://images.unsplash.com/photo-1615571022219-eb45cf7faa9d?w=800",
            images = listOf(
                "https://images.unsplash.com/photo-1615571022219-eb45cf7faa9d?w=800",
                "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=800"
            ),
            description = "Rower gravel na utwardzone i gruntowe drogi. Szersze opony i komfortowa geometria.",
            available = true, shopId = "4", category = "Gravel"
        ),
        Bike(
            id = "12", name = "BMX Street", price = 20, rating = 0f,
            image = "https://images.unsplash.com/photo-1517649763962-0c623066013b?w=800",
            images = listOf("https://images.unsplash.com/photo-1517649763962-0c623066013b?w=800"),
            description = "Wytrzymały rower BMX do jazdy ulicznej i parkowej. Solidna stalowa rama.",
            available = true, shopId = "4", category = "BMX"
        )
    )

    val shops = listOf(
        Shop(
            id = "1", name = "BikeHub Centrum",
            description = "Najlepsza wypożyczalnia rowerów w centrum miasta. Oferujemy szeroki wybór rowerów na każdą okazję. Otwarte 7 dni w tygodniu.",
            location = "ul. Główna 15, Warszawa", rating = 4.9f,
            image = "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=800",
            bikeIds = listOf("1", "2", "5")
        ),
        Shop(
            id = "2", name = "EcoBike Station",
            description = "Wypożyczalnia specjalizująca się w rowerach elektrycznych i ekologicznych rozwiązaniach mobilności miejskiej.",
            location = "ul. Zielona 42, Warszawa", rating = 4.7f,
            image = "https://images.unsplash.com/photo-1571902943202-507ec2618e8f?w=800",
            bikeIds = listOf("3", "4", "6")
        ),
        Shop(
            id = "3", name = "VeloCity Praga",
            description = "Klimatyczna wypożyczalnia na Pradze z bogatą ofertą rowerów rodzinnych. Idealna baza wypadowa nad Wisłę.",
            location = "ul. Targowa 7, Warszawa", rating = 4.8f,
            image = "https://images.unsplash.com/photo-1517649763962-0c623066013b?w=800",
            bikeIds = listOf("7", "8", "9")
        ),
        Shop(
            id = "4", name = "GreenWheels Mokotów",
            description = "Nowoczesna wypożyczalnia na Mokotowie. Specjalizujemy się w rowerach cargo i gravel dla aktywnych.",
            location = "ul. Puławska 100, Warszawa", rating = 4.6f,
            image = "https://images.unsplash.com/photo-1501147830916-ce44a6359892?w=800",
            bikeIds = listOf("10", "11", "12")
        )
    )

    val seededAdminUsers = listOf(
        SeedUser(
            id = 0L, name = "Administrator", email = "admin@bikerent.local",
            passwordHash = "3eb3fe66b31e3b4d10fa70b5cad49c7112294af6ae4e476a1c405155d45aa121"
        )
    )
}
