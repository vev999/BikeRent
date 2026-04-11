package com.example.bikerent.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.bikerent.data.db.converter.Converters

@Entity(tableName = "shops")
@TypeConverters(Converters::class)
data class ShopEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val location: String,
    val rating: Float,
    val image: String,
    val bikeIds: List<String>
)
