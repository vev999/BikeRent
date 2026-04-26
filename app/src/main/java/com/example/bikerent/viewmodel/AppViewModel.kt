package com.example.bikerent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bikerent.data.ActiveRental
import com.example.bikerent.data.Bike
import com.example.bikerent.data.RentalHistory
import com.example.bikerent.data.Review
import com.example.bikerent.data.Shop
import com.example.bikerent.data.repository.BikeRepository
import com.example.bikerent.data.repository.RentalRepository
import com.example.bikerent.data.repository.ReviewRepository
import com.example.bikerent.data.repository.ShopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppViewModel(
    private val bikeRepository: BikeRepository,
    private val shopRepository: ShopRepository,
    private val rentalRepository: RentalRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _bikes = MutableStateFlow<List<Bike>>(emptyList())
    val bikes: StateFlow<List<Bike>> = _bikes.asStateFlow()

    private val _shops = MutableStateFlow<List<Shop>>(emptyList())
    val shops: StateFlow<List<Shop>> = _shops.asStateFlow()

    private val _activeRentals = MutableStateFlow<List<ActiveRental>>(emptyList())
    val activeRentals: StateFlow<List<ActiveRental>> = _activeRentals.asStateFlow()

    private val _rentalHistory = MutableStateFlow<List<RentalHistory>>(emptyList())
    val rentalHistory: StateFlow<List<RentalHistory>> = _rentalHistory.asStateFlow()

    private val _currentBikeReviews = MutableStateFlow<List<Review>>(emptyList())
    val currentBikeReviews: StateFlow<List<Review>> = _currentBikeReviews.asStateFlow()

    private val _userReviews = MutableStateFlow<List<Review>>(emptyList())
    val userReviews: StateFlow<List<Review>> = _userReviews.asStateFlow()

    private val _allReviews = MutableStateFlow<List<Review>>(emptyList())
    val allReviews: StateFlow<List<Review>> = _allReviews.asStateFlow()

    private var currentUserId: Long = -1L
    private var currentUserName: String = ""

    fun initForUser(userId: Long, userName: String) {
        currentUserId = userId
        currentUserName = userName
        viewModelScope.launch {
            _bikes.value = bikeRepository.getAll()
            _shops.value = shopRepository.getAll()
            refreshRentals()
            refreshUserReviews()
        }
    }

    fun getCurrentUserId(): Long = currentUserId

    // ── Rentals ──────────────────────────────────────────────────────────────

    private suspend fun refreshRentals() {
        if (currentUserId == -1L) return
        _activeRentals.value = rentalRepository.getActiveRentals(currentUserId)
        _rentalHistory.value = rentalRepository.getRentalHistory(currentUserId)
    }

    fun rentBike(bike: Bike, shop: Shop) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val startTime = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(now))
            val rental = ActiveRental(
                id = "r_$now",
                bikeId = bike.id,
                bikeName = bike.name,
                shopName = shop.name,
                startTime = startTime,
                endTime = "",
                returnLocation = shop.location
            )
            rentalRepository.addRental(rental, currentUserId)
            refreshRentals()
        }
    }

    fun addBike(name: String, description: String, shopId: String, price: Int, category: String, imagePath: String) {
        viewModelScope.launch {
            val bikeId = "bike_${System.currentTimeMillis()}"
            bikeRepository.addBike(
                Bike(
                    id = bikeId, name = name, price = price, rating = 0f,
                    image = imagePath, images = listOf(imagePath),
                    description = description, available = true,
                    shopId = shopId, category = category
                )
            )
            _bikes.value = bikeRepository.getAll()
        }
    }

    fun returnBike(rental: ActiveRental) {
        viewModelScope.launch {
            val bikePrice = _bikes.value.find { it.id == rental.bikeId }?.price ?: 0
            rentalRepository.returnBike(rental.id, bikePrice, currentUserId)
            refreshRentals()
        }
    }

    // ── Reviews ──────────────────────────────────────────────────────────────

    fun loadBikeReviews(bikeId: String) {
        viewModelScope.launch {
            _currentBikeReviews.value = reviewRepository.getReviewsForBike(bikeId)
        }
    }

    fun addReview(bikeId: String, bikeName: String, rating: Float, comment: String) {
        if (currentUserId == -1L) return
        viewModelScope.launch {
            reviewRepository.addReview(bikeId, bikeName, currentUserId, currentUserName, rating, comment)
            refreshBikeReviews(bikeId)
            refreshUserReviews()
        }
    }

    fun deleteReview(review: Review) {
        viewModelScope.launch {
            reviewRepository.deleteReview(review.id)
            refreshBikeReviews(review.bikeId)
            refreshUserReviews()
            _allReviews.value = reviewRepository.getAllReviews()
        }
    }

    fun loadAllReviews() {
        viewModelScope.launch {
            _allReviews.value = reviewRepository.getAllReviews()
        }
    }

    private suspend fun refreshBikeReviews(bikeId: String) {
        val reviews = reviewRepository.getReviewsForBike(bikeId)
        _currentBikeReviews.value = reviews
        val newRating = if (reviews.isEmpty()) 0f
        else reviews.map { it.rating }.average().toFloat()
        bikeRepository.updateRating(bikeId, newRating)
        _bikes.value = bikeRepository.getAll()
    }

    private suspend fun refreshUserReviews() {
        if (currentUserId == -1L) return
        _userReviews.value = reviewRepository.getReviewsForUser(currentUserId)
    }
}

class AppViewModelFactory(
    private val bikeRepository: BikeRepository,
    private val shopRepository: ShopRepository,
    private val rentalRepository: RentalRepository,
    private val reviewRepository: ReviewRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        AppViewModel(bikeRepository, shopRepository, rentalRepository, reviewRepository) as T
}
