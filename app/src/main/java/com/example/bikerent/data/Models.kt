package com.example.bikerent.data

data class Review(
    val id: String,
    val bikeId: String,
    val bikeName: String,
    val userId: Long,
    val userName: String,
    val rating: Float,
    val comment: String,
    val date: String
)

data class Bike(
    val id: String,
    val name: String,
    val price: Int,
    val rating: Float,
    val image: String,
    val images: List<String>,
    val description: String,
    val available: Boolean,
    val shopId: String,
    val category: String
)

data class Shop(
    val id: String,
    val name: String,
    val description: String,
    val location: String,
    val rating: Float,
    val image: String,
    val bikeIds: List<String>
)

data class ActiveRental(
    val id: String,
    val bikeId: String,
    val bikeName: String,
    val shopName: String,
    val startTime: String,
    val endTime: String,
    val returnLocation: String
)

data class RentalHistory(
    val id: String,
    val bikeName: String,
    val shopName: String,
    val date: String,
    val duration: String,
    val cost: Int
)

data class SeedUser(
    val id: Long,
    val name: String,
    val email: String,
    val passwordHash: String
)
