package com.example.bikerent.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bikerent.data.db.entity.ReviewEntity

@Dao
interface ReviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(review: ReviewEntity)

    @Query("SELECT * FROM reviews WHERE bikeId = :bikeId ORDER BY date DESC")
    suspend fun getAllForBike(bikeId: String): List<ReviewEntity>

    @Query("SELECT * FROM reviews WHERE userId = :userId ORDER BY date DESC")
    suspend fun getAllForUser(userId: Long): List<ReviewEntity>

    @Query("SELECT * FROM reviews ORDER BY date DESC")
    suspend fun getAll(): List<ReviewEntity>

    @Query("DELETE FROM reviews WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM reviews WHERE userId = :userId AND bikeId = :bikeId LIMIT 1")
    suspend fun findByUserAndBike(userId: Long, bikeId: String): ReviewEntity?
}
