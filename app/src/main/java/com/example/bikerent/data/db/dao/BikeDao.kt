package com.example.bikerent.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bikerent.data.db.entity.BikeEntity

@Dao
interface BikeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(bikes: List<BikeEntity>)

    @Query("SELECT * FROM bikes")
    suspend fun getAll(): List<BikeEntity>

    @Query("SELECT * FROM bikes WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): BikeEntity?

    @Query("SELECT COUNT(*) FROM bikes")
    suspend fun count(): Int
}
