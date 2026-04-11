package com.example.bikerent.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bikerent.data.db.entity.RentalHistoryEntity

@Dao
interface RentalHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rental: RentalHistoryEntity)

    @Query("SELECT * FROM rental_history WHERE userId = :userId ORDER BY date DESC")
    suspend fun getAllForUser(userId: Long): List<RentalHistoryEntity>
}
