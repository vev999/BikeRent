package com.example.bikerent.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bikerent.data.db.entity.ActiveRentalEntity

@Dao
interface ActiveRentalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rental: ActiveRentalEntity)

    @Query("SELECT * FROM active_rentals WHERE userId = :userId ORDER BY startTime DESC")
    suspend fun getAllForUser(userId: Long): List<ActiveRentalEntity>

    @Query("DELETE FROM active_rentals WHERE id = :rentalId")
    suspend fun deleteById(rentalId: String)
}
