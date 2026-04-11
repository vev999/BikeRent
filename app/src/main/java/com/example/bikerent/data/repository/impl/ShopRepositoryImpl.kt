package com.example.bikerent.data.repository.impl

import com.example.bikerent.data.Shop
import com.example.bikerent.data.db.dao.ShopDao
import com.example.bikerent.data.db.entity.ShopEntity
import com.example.bikerent.data.repository.ShopRepository

class ShopRepositoryImpl(private val dao: ShopDao) : ShopRepository {

    override suspend fun getAll(): List<Shop> =
        dao.getAll().map { it.toDomain() }

    override suspend fun findById(id: String): Shop? =
        dao.findById(id)?.toDomain()

    private fun ShopEntity.toDomain() = Shop(
        id = id,
        name = name,
        description = description,
        location = location,
        rating = rating,
        image = image,
        bikeIds = bikeIds
    )
}
