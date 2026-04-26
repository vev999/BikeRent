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
    version = 2,
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
            seedDatabase()
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            seedDatabase()
        }

        private fun seedDatabase() {
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    seedUsers(database.userDao())
                    seedBikes(database.bikeDao())
                    seedShops(database.shopDao())
                }
            }
        }

        private suspend fun seedUsers(dao: UserDao) {
            dao.insertAll(DataSource.seededAdminUsers.map { user ->
                UserEntity(id = user.id, name = user.name, email = user.email, passwordHash = user.passwordHash)
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
    }
}
