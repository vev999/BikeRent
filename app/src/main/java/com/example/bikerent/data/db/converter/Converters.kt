package com.example.bikerent.data.db.converter

import androidx.room.TypeConverter
import com.example.bikerent.data.Review
import org.json.JSONArray
import org.json.JSONObject

class Converters {

    @TypeConverter
    fun stringListToString(list: List<String>): String = list.joinToString("|")

    @TypeConverter
    fun stringToStringList(value: String): List<String> =
        if (value.isBlank()) emptyList() else value.split("|")

    @TypeConverter
    fun reviewListToString(reviews: List<Review>): String {
        val array = JSONArray()
        reviews.forEach { review ->
            array.put(JSONObject().apply {
                put("id", review.id)
                put("userName", review.userName)
                put("rating", review.rating.toDouble())
                put("comment", review.comment)
                put("date", review.date)
            })
        }
        return array.toString()
    }

    @TypeConverter
    fun stringToReviewList(value: String): List<Review> {
        if (value.isBlank()) return emptyList()
        return try {
            val array = JSONArray(value)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                Review(
                    id = obj.getString("id"),
                    userName = obj.getString("userName"),
                    rating = obj.getDouble("rating").toFloat(),
                    comment = obj.getString("comment"),
                    date = obj.getString("date")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
