package com.example.bikerent.data

object DataSource {

    val bikes = listOf(
        Bike(
            id = "1",
            name = "Urban City Bike",
            price = 15,
            rating = 4.8f,
            image = "https://images.unsplash.com/photo-1760588774918-769cb07ab9c8?w=800",
            images = listOf(
                "https://images.unsplash.com/photo-1760588774918-769cb07ab9c8?w=800",
                "https://images.unsplash.com/photo-1692668696811-90976b749459?w=800"
            ),
            description = "Idealny rower do codziennych dojazdów po mieście. Lekki, wygodny i niezawodny.",
            available = true,
            shopId = "1",
            category = "Miejski",
            reviews = emptyList()
        ),
        Bike(
            id = "2",
            name = "Mountain Explorer",
            price = 25,
            rating = 4.9f,
            image = "https://images.unsplash.com/photo-1571660973831-70d6fc86c1d6?w=800",
            images = listOf("https://images.unsplash.com/photo-1571660973831-70d6fc86c1d6?w=800"),
            description = "Rower górski dla prawdziwych poszukiwaczy przygód. Wytrzymała konstrukcja.",
            available = true,
            shopId = "1",
            category = "Górski",
            reviews = emptyList()
        ),
        Bike(
            id = "3",
            name = "E-Bike Pro",
            price = 35,
            rating = 4.7f,
            image = "https://images.unsplash.com/photo-1692668696811-90976b749459?w=800",
            images = listOf("https://images.unsplash.com/photo-1692668696811-90976b749459?w=800"),
            description = "Elektryczny rower z najnowszą technologią. Zasięg do 80 km.",
            available = true,
            shopId = "2",
            category = "Elektryczny",
            reviews = emptyList()
        ),
        Bike(
            id = "4",
            name = "Racing Speed",
            price = 30,
            rating = 4.6f,
            image = "https://images.unsplash.com/photo-1525996596318-edf2b6b64e60?w=800",
            images = listOf("https://images.unsplash.com/photo-1525996596318-edf2b6b64e60?w=800"),
            description = "Szosowy rower wyścigowy. Lekkość i szybkość w jednym.",
            available = false,
            shopId = "2",
            category = "Szosowy",
            reviews = emptyList()
        ),
        Bike(
            id = "5",
            name = "Beach Cruiser",
            price = 18,
            rating = 4.5f,
            image = "https://images.unsplash.com/photo-1618520826503-12a0dcaa549e?w=800",
            images = listOf("https://images.unsplash.com/photo-1618520826503-12a0dcaa549e?w=800"),
            description = "Komfortowy rower cruiser na spokojne przejażdżki.",
            available = true,
            shopId = "1",
            category = "Cruiser",
            reviews = emptyList()
        ),
        Bike(
            id = "6",
            name = "Hybrid Commuter",
            price = 20,
            rating = 4.8f,
            image = "https://images.unsplash.com/photo-1759047990878-b5a1e95f81fd?w=800",
            images = listOf("https://images.unsplash.com/photo-1759047990878-b5a1e95f81fd?w=800"),
            description = "Uniwersalny rower hybrydowy na każdą drogę.",
            available = true,
            shopId = "2",
            category = "Hybrydowy",
            reviews = emptyList()
        )
    )

    val shops = listOf(
        Shop(
            id = "1",
            name = "BikeHub Centrum",
            description = "Najlepsza wypożyczalnia rowerów w centrum miasta. Oferujemy szeroki wybór rowerów na każdą okazję.",
            location = "ul. Główna 15, Warszawa",
            rating = 4.9f,
            image = "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=800",
            bikeIds = listOf("1", "2", "5")
        ),
        Shop(
            id = "2",
            name = "EcoBike Station",
            description = "Wypożyczalnia specjalizująca się w rowerach elektrycznych i ekologicznych rozwiązaniach mobilności.",
            location = "ul. Zielona 42, Warszawa",
            rating = 4.7f,
            image = "https://images.unsplash.com/photo-1571902943202-507ec2618e8f?w=800",
            bikeIds = listOf("3", "4", "6")
        )
    )

    val seededAdminUsers = listOf(
        SeedUser(
            id = 0L,
            name = "Administrator",
            email = "admin@bikerent.local",
            passwordHash = "3eb3fe66b31e3b4d10fa70b5cad49c7112294af6ae4e476a1c405155d45aa121"
        )
    )

    val adminPanelUsers = emptyList<AdminUser>()

    val adminComments = emptyList<AdminComment>()
}
