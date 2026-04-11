package com.example.bikerent.data.repository.impl

import com.example.bikerent.data.DataSource
import com.example.bikerent.data.db.dao.UserDao
import com.example.bikerent.data.db.entity.UserEntity
import com.example.bikerent.data.repository.UserRepository

class UserRepositoryImpl(private val dao: UserDao) : UserRepository {

    override suspend fun register(
        email: String,
        name: String,
        passwordHash: String
    ): Result<UserEntity> {
        return try {
            val existing = dao.findByEmail(email)
            if (existing != null) {
                Result.failure(Exception("Użytkownik z tym e-mailem już istnieje"))
            } else {
                val id = dao.insert(UserEntity(email = email, name = name, passwordHash = passwordHash))
                val created = dao.findById(id)!!
                Result.success(created)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, passwordHash: String): UserEntity? {
        ensureSeededAdminUsers()
        return dao.findByEmailAndPassword(email, passwordHash)
    }

    override suspend fun findByEmail(email: String): UserEntity? {
        ensureSeededAdminUsers()
        return dao.findByEmail(email)
    }

    override suspend fun findById(id: Long): UserEntity? =
        dao.findById(id)

    override suspend fun updateUser(id: Long, name: String, email: String) =
        dao.updateNameAndEmail(id, name, email)

    private suspend fun ensureSeededAdminUsers() {
        dao.insertAll(DataSource.seededAdminUsers.map { user ->
            UserEntity(
                id = user.id,
                email = user.email,
                name = user.name,
                passwordHash = user.passwordHash
            )
        })
    }
}
