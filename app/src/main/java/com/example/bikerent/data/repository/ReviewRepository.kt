package com.example.bikerent.data.repository

import com.example.bikerent.data.Review

interface ReviewRepository {
    suspend fun getReviewsForBike(bikeId: String): List<Review>
    suspend fun getReviewsForUser(userId: Long): List<Review>
    suspend fun getAllReviews(): List<Review>
    suspend fun addReview(bikeId: String, bikeName: String, userId: Long, userName: String, rating: Float, comment: String)
    suspend fun deleteReview(id: String)
    suspend fun hasUserReviewedBike(userId: Long, bikeId: String): Boolean
}
