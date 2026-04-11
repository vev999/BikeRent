package com.example.bikerent.data.repository

import com.example.bikerent.data.ActiveRental
import com.example.bikerent.data.RentalHistory

interface RentalRepository {
    suspend fun getActiveRentals(userId: Long): List<ActiveRental>
    suspend fun getRentalHistory(userId: Long): List<RentalHistory>
    suspend fun addRental(rental: ActiveRental, userId: Long)
    suspend fun returnBike(rentalId: String, bikePrice: Int, userId: Long)
}
