package com.example.bikerent.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.bikerent.data.db.converter.Converters

@Entity(tableName = "bikes")
@TypeConverters(Converters::class)
data class BikeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val price: Int,
    val rating: Float,
    val image: String,
    val images: List<String>,
    val description: String,
    val available: Boolean,
    val shopId: String,
    val category: String
)
