package com.example.bikerent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bikerent.data.ActiveRental
import com.example.bikerent.data.Bike
import com.example.bikerent.data.RentalHistory
import com.example.bikerent.data.Shop
import com.example.bikerent.data.repository.BikeRepository
import com.example.bikerent.data.repository.RentalRepository
import com.example.bikerent.data.repository.ShopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AppViewModel(
    private val bikeRepository: BikeRepository,
    private val shopRepository: ShopRepository,
    private val rentalRepository: RentalRepository
) : ViewModel() {

    private val _bikes = MutableStateFlow<List<Bike>>(emptyList())
    val bikes: StateFlow<List<Bike>> = _bikes.asStateFlow()

    private val _shops = MutableStateFlow<List<Shop>>(emptyList())
    val shops: StateFlow<List<Shop>> = _shops.asStateFlow()

    private val _activeRentals = MutableStateFlow<List<ActiveRental>>(emptyList())
    val activeRentals: StateFlow<List<ActiveRental>> = _activeRentals.asStateFlow()

    private val _rentalHistory = MutableStateFlow<List<RentalHistory>>(emptyList())
    val rentalHistory: StateFlow<List<RentalHistory>> = _rentalHistory.asStateFlow()

    private var currentUserId: Long = -1L

    fun initForUser(userId: Long) {
        currentUserId = userId
        viewModelScope.launch {
            _bikes.value = bikeRepository.getAll()
            _shops.value = shopRepository.getAll()
            refreshRentals()
        }
    }

    private suspend fun refreshRentals() {
        if (currentUserId == -1L) return
        _activeRentals.value = rentalRepository.getActiveRentals(currentUserId)
        _rentalHistory.value = rentalRepository.getRentalHistory(currentUserId)
    }

    fun rentBike(bike: Bike, shop: Shop) {
        viewModelScope.launch {
            val now = Calendar.getInstance()
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val startTime = sdf.format(now.time)
            now.add(Calendar.HOUR_OF_DAY, 4)
            val endTime = sdf.format(now.time)

            val rental = ActiveRental(
                id = "r_${System.currentTimeMillis()}",
                bikeId = bike.id,
                bikeName = bike.name,
                shopName = shop.name,
                startTime = startTime,
                endTime = endTime,
                returnLocation = shop.location
            )
            rentalRepository.addRental(rental, currentUserId)
            refreshRentals()
        }
    }

    fun returnBike(rental: ActiveRental) {
        viewModelScope.launch {
            val bikePrice = _bikes.value.find { it.id == rental.bikeId }?.price ?: 0
            rentalRepository.returnBike(rental.id, bikePrice, currentUserId)
            refreshRentals()
        }
    }
}

class AppViewModelFactory(
    private val bikeRepository: BikeRepository,
    private val shopRepository: ShopRepository,
    private val rentalRepository: RentalRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        AppViewModel(bikeRepository, shopRepository, rentalRepository) as T
}
