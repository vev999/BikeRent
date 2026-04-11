package com.example.bikerent.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "active_rentals",
    foreignKeys = [ForeignKey(
        entity = UserEntity::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("userId")]
)
data class ActiveRentalEntity(
    @PrimaryKey val id: String,
    val bikeId: String,
    val bikeName: String,
    val shopName: String,
    val startTime: String,
    val endTime: String,
    val returnLocation: String,
    val userId: Long
)
