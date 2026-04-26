package com.example.bikerent.data.repository.impl

import com.example.bikerent.data.Bike
import com.example.bikerent.data.db.dao.BikeDao
import com.example.bikerent.data.db.entity.BikeEntity
import com.example.bikerent.data.repository.BikeRepository

class BikeRepositoryImpl(private val dao: BikeDao) : BikeRepository {

    override suspend fun getAll(): List<Bike> =
        dao.getAll().map { it.toDomain() }

    override suspend fun findById(id: String): Bike? =
        dao.findById(id)?.toDomain()

    override suspend fun updateRating(id: String, rating: Float) =
        dao.updateRating(id, rating)

    override suspend fun addBike(bike: Bike) {
        dao.insert(BikeEntity(
            id = bike.id, name = bike.name, price = bike.price, rating = bike.rating,
            image = bike.image, images = bike.images, description = bike.description,
            available = bike.available, shopId = bike.shopId, category = bike.category
        ))
    }

    private fun BikeEntity.toDomain() = Bike(
        id = id, name = name, price = price, rating = rating,
        image = image, images = images, description = description,
        available = available, shopId = shopId, category = category
    )
}
