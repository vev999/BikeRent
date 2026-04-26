package com.example.bikerent.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reviews",
    indices = [Index("bikeId"), Index("userId")]
)
data class ReviewEntity(
    @PrimaryKey val id: String,
    val bikeId: String,
    val bikeName: String,
    val userId: Long,
    val userName: String,
    val rating: Float,
    val comment: String,
    val date: String
)
