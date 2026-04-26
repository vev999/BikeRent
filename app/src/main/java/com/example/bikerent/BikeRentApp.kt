package com.example.bikerent

import android.app.Application
import com.example.bikerent.data.db.BikeRentDatabase
import com.example.bikerent.data.repository.BikeRepository
import com.example.bikerent.data.repository.RentalRepository
import com.example.bikerent.data.repository.ReviewRepository
import com.example.bikerent.data.repository.ShopRepository
import com.example.bikerent.data.repository.UserRepository
import com.example.bikerent.data.repository.impl.BikeRepositoryImpl
import com.example.bikerent.data.repository.impl.RentalRepositoryImpl
import com.example.bikerent.data.repository.impl.ReviewRepositoryImpl
import com.example.bikerent.data.repository.impl.ShopRepositoryImpl
import com.example.bikerent.data.repository.impl.UserRepositoryImpl

class BikeRentApp : Application() {

    val database by lazy { BikeRentDatabase.getInstance(this) }

    val userRepository: UserRepository by lazy {
        UserRepositoryImpl(database.userDao())
    }
    val bikeRepository: BikeRepository by lazy {
        BikeRepositoryImpl(database.bikeDao())
    }
    val shopRepository: ShopRepository by lazy {
        ShopRepositoryImpl(database.shopDao())
    }
    val rentalRepository: RentalRepository by lazy {
        RentalRepositoryImpl(database.activeRentalDao(), database.rentalHistoryDao())
    }
    val reviewRepository: ReviewRepository by lazy {
        ReviewRepositoryImpl(database.reviewDao())
    }
}
