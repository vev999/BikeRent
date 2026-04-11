package com.example.bikerent.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "rental_history",
    foreignKeys = [ForeignKey(
        entity = UserEntity::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("userId")]
)
data class RentalHistoryEntity(
    @PrimaryKey val id: String,
    val bikeName: String,
    val shopName: String,
    val date: String,
    val duration: String,
    val cost: Int,
    val userId: Long
)
