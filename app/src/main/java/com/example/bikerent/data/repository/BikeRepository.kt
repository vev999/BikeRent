package com.example.bikerent.data.repository

import com.example.bikerent.data.Bike

interface BikeRepository {
    suspend fun getAll(): List<Bike>
    suspend fun findById(id: String): Bike?
    suspend fun updateRating(id: String, rating: Float)
    suspend fun addBike(bike: Bike)
}
