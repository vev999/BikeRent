package com.example.bikerent.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bikerent.data.db.entity.ShopEntity

@Dao
interface ShopDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(shops: List<ShopEntity>)

    @Query("SELECT * FROM shops")
    suspend fun getAll(): List<ShopEntity>

    @Query("SELECT * FROM shops WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): ShopEntity?

    @Query("SELECT COUNT(*) FROM shops")
    suspend fun count(): Int
}
