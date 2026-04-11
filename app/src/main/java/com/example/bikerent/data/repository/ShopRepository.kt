package com.example.bikerent.data.repository

import com.example.bikerent.data.Shop

interface ShopRepository {
    suspend fun getAll(): List<Shop>
    suspend fun findById(id: String): Shop?
}
