package com.example.bikerent.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.bikerent.data.DataSource
import com.example.bikerent.data.db.converter.Converters
import com.example.bikerent.data.db.dao.ActiveRentalDao
import com.example.bikerent.data.db.dao.BikeDao
import com.example.bikerent.data.db.dao.RentalHistoryDao
import com.example.bikerent.data.db.dao.ReviewDao
import com.example.bikerent.data.db.dao.ShopDao
import com.example.bikerent.data.db.dao.UserDao
import com.example.bikerent.data.db.entity.ActiveRentalEntity
import com.example.bikerent.data.db.entity.BikeEntity
import com.example.bikerent.data.db.entity.RentalHistoryEntity
import com.example.bikerent.data.db.entity.ReviewEntity
import com.example.bikerent.data.db.entity.ShopEntity
import com.example.bikerent.data.db.entity.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        BikeEntity::class,
        ShopEntity::class,
        ActiveRentalEntity::class,
        RentalHistoryEntity::class,
        ReviewEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BikeRentDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun bikeDao(): BikeDao
    abstract fun shopDao(): ShopDao
    abstract fun activeRentalDao(): ActiveRentalDao
    abstract fun rentalHistoryDao(): RentalHistoryDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        @Volatile
        private var INSTANCE: BikeRentDatabase? = null

        fun getInstance(context: Context): BikeRentDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BikeRentDatabase::class.java,
                    "bikerent.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(SeedCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class SeedCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    seedUsers(database.userDao())
                    seedBikes(database.bikeDao())
                    seedShops(database.shopDao())
                    seedReviews(database.reviewDao(), database.userDao(), database.bikeDao())
                }
            }
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            // Bikes/shops are seeded only in onCreate to avoid REPLACE overwriting user-updated ratings.
            // Users and reviews use IGNORE strategy — safe to run on every open.
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    seedUsers(database.userDao())
                    seedReviews(database.reviewDao(), database.userDao(), database.bikeDao())
                }
            }
        }

        private suspend fun seedUsers(dao: UserDao) {
            val allSeeded = DataSource.seededAdminUsers + DataSource.seededRegularUsers
            dao.insertAll(allSeeded.map { user ->
                UserEntity(
                    id = user.id,
                    email = user.email,
                    name = user.name,
                    passwordHash = user.passwordHash
                )
            })
        }

        private suspend fun seedBikes(dao: BikeDao) {
            dao.insertAll(DataSource.bikes.map { bike ->
                BikeEntity(
                    id = bike.id, name = bike.name, price = bike.price, rating = bike.rating,
                    image = bike.image, images = bike.images, description = bike.description,
                    available = bike.available, shopId = bike.shopId, category = bike.category
                )
            })
        }

        private suspend fun seedShops(dao: ShopDao) {
            dao.insertAll(DataSource.shops.map { shop ->
                ShopEntity(
                    id = shop.id, name = shop.name, description = shop.description,
                    location = shop.location, rating = shop.rating,
                    image = shop.image, bikeIds = shop.bikeIds
                )
            })
        }

        private suspend fun seedReviews(reviewDao: ReviewDao, userDao: UserDao, bikeDao: BikeDao) {
            val anna = userDao.findByEmail("anna.k@mail.com") ?: return
            val pawel = userDao.findByEmail("pawel.n@mail.com") ?: return

            val reviews = listOf(
                ReviewEntity("seed_rev_01", "1", "Urban City Bike",
                    anna.id, anna.name, 5f,
                    "Świetny rower do codziennych dojazdów! Lekki i bardzo wygodny w obsłudze.", "10.04.2025"),
                ReviewEntity("seed_rev_02", "1", "Urban City Bike",
                    pawel.id, pawel.name, 4f,
                    "Dobry rower, choć trochę ciężki na podjazdach. Do miasta idealny.", "18.04.2025"),
                ReviewEntity("seed_rev_03", "2", "Mountain Explorer",
                    anna.id, anna.name, 5f,
                    "Amortyzacja robi robotę! Ideał na górskie trasy. Polecam wszystkim.", "05.04.2025"),
                ReviewEntity("seed_rev_04", "3", "E-Bike Pro",
                    pawel.id, pawel.name, 5f,
                    "Elektryczny wspomagacz zmienia wszystko. Do pracy dojechałem bez potu.", "22.04.2025"),
                ReviewEntity("seed_rev_05", "5", "Beach Cruiser",
                    anna.id, anna.name, 4f,
                    "Komfortowy i stylowy, idealny na spokojne przejażdżki po parku.", "30.04.2025"),
                ReviewEntity("seed_rev_06", "7", "Kids Explorer",
                    pawel.id, pawel.name, 5f,
                    "Córka była zachwycona! Kółka boczne dają dużo pewności, rama solidna.", "02.05.2025"),
                ReviewEntity("seed_rev_07", "9", "Urban Folder",
                    anna.id, anna.name, 4f,
                    "Składanie zajmuje chwilę, ale w bagażniku mieści się bez problemu.", "08.05.2025"),
                ReviewEntity("seed_rev_08", "4", "Racing Speed",
                    pawel.id, pawel.name, 3f,
                    "Szybki i zwinny, ale siodło bardzo twarde — długie trasy bywają bolesne.", "14.05.2025"),
                ReviewEntity("seed_rev_09", "11", "Gravel Master",
                    anna.id, anna.name, 4f,
                    "Sprawdza się zarówno na szutrze, jak i asfalcie. Opony spełniają obietnicę.", "20.05.2025"),
                ReviewEntity("seed_rev_10", "12", "BMX Street",
                    pawel.id, pawel.name, 5f,
                    "Solidna konstrukcja, idealny do jazdy po skateparku. Polecam!", "26.05.2025"),
            )
            reviewDao.insertAll(reviews)

            // Recalculate ratings from all reviews currently in DB (seeded + user-added)
            val bikeIds = listOf("1", "2", "3", "4", "5", "7", "9", "11", "12")
            for (bikeId in bikeIds) {
                val bikeReviews = reviewDao.getAllForBike(bikeId)
                if (bikeReviews.isNotEmpty()) {
                    val avg = bikeReviews.map { it.rating }.average().toFloat()
                    bikeDao.updateRating(bikeId, avg)
                }
            }
        }
    }
}
