package com.example.bikerent.data.db.converter

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun stringListToString(list: List<String>): String = list.joinToString("|")

    @TypeConverter
    fun stringToStringList(value: String): List<String> =
        if (value.isBlank()) emptyList() else value.split("|")
}
