package com.example.bikerent.data.repository

import com.example.bikerent.data.db.entity.UserEntity

interface UserRepository {
    suspend fun register(email: String, name: String, passwordHash: String): Result<UserEntity>
    suspend fun login(email: String, passwordHash: String): UserEntity?
    suspend fun findByEmail(email: String): UserEntity?
    suspend fun findById(id: Long): UserEntity?
    suspend fun updateUser(id: Long, name: String, email: String)
}
