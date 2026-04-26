package com.example.bikerent.data.repository.impl

import com.example.bikerent.data.Review
import com.example.bikerent.data.db.dao.ReviewDao
import com.example.bikerent.data.db.entity.ReviewEntity
import com.example.bikerent.data.repository.ReviewRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReviewRepositoryImpl(private val dao: ReviewDao) : ReviewRepository {

    override suspend fun getReviewsForBike(bikeId: String): List<Review> =
        dao.getAllForBike(bikeId).map { it.toDomain() }

    override suspend fun getReviewsForUser(userId: Long): List<Review> =
        dao.getAllForUser(userId).map { it.toDomain() }

    override suspend fun getAllReviews(): List<Review> =
        dao.getAll().map { it.toDomain() }

    override suspend fun addReview(
        bikeId: String, bikeName: String, userId: Long,
        userName: String, rating: Float, comment: String
    ) {
        val date = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
        dao.insert(
            ReviewEntity(
                id = "rev_${System.currentTimeMillis()}",
                bikeId = bikeId,
                bikeName = bikeName,
                userId = userId,
                userName = userName,
                rating = rating,
                comment = comment,
                date = date
            )
        )
    }

    override suspend fun deleteReview(id: String) = dao.deleteById(id)

    override suspend fun hasUserReviewedBike(userId: Long, bikeId: String): Boolean =
        dao.findByUserAndBike(userId, bikeId) != null

    private fun ReviewEntity.toDomain() = Review(
        id = id, bikeId = bikeId, bikeName = bikeName, userId = userId,
        userName = userName, rating = rating, comment = comment, date = date
    )
}
