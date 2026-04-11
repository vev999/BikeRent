package com.example.bikerent.data.repository

import com.example.bikerent.data.Bike

interface BikeRepository {
    suspend fun getAll(): List<Bike>
    suspend fun findById(id: String): Bike?
}
