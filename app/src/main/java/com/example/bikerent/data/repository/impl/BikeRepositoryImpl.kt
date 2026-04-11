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

    private fun BikeEntity.toDomain() = Bike(
        id = id,
        name = name,
        price = price,
        rating = rating,
        image = image,
        images = images,
        description = description,
        available = available,
        shopId = shopId,
        category = category,
        reviews = reviews
    )
}
