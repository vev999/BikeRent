package com.example.bikerent.data.repository.impl

import com.example.bikerent.data.ActiveRental
import com.example.bikerent.data.RentalHistory
import com.example.bikerent.data.db.dao.ActiveRentalDao
import com.example.bikerent.data.db.dao.RentalHistoryDao
import com.example.bikerent.data.db.entity.ActiveRentalEntity
import com.example.bikerent.data.db.entity.RentalHistoryEntity
import com.example.bikerent.data.repository.RentalRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RentalRepositoryImpl(
    private val activeRentalDao: ActiveRentalDao,
    private val rentalHistoryDao: RentalHistoryDao
) : RentalRepository {

    override suspend fun getActiveRentals(userId: Long): List<ActiveRental> =
        activeRentalDao.getAllForUser(userId).map { it.toDomain() }

    override suspend fun getRentalHistory(userId: Long): List<RentalHistory> =
        rentalHistoryDao.getAllForUser(userId).map { it.toDomain() }

    override suspend fun addRental(rental: ActiveRental, userId: Long) {
        activeRentalDao.insert(
            ActiveRentalEntity(
                id = rental.id,
                bikeId = rental.bikeId,
                bikeName = rental.bikeName,
                shopName = rental.shopName,
                startTime = rental.startTime,
                endTime = rental.endTime,
                returnLocation = rental.returnLocation,
                userId = userId
            )
        )
    }

    override suspend fun returnBike(rentalId: String, bikePrice: Int, userId: Long) {
        val rental = activeRentalDao.getAllForUser(userId).find { it.id == rentalId } ?: return
        activeRentalDao.deleteById(rentalId)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        rentalHistoryDao.insert(
            RentalHistoryEntity(
                id = "h_${System.currentTimeMillis()}",
                bikeName = rental.bikeName,
                shopName = rental.shopName,
                date = today,
                duration = "4 godziny",
                cost = bikePrice * 4,
                userId = userId
            )
        )
    }

    private fun ActiveRentalEntity.toDomain() = ActiveRental(
        id = id, bikeId = bikeId, bikeName = bikeName,
        shopName = shopName, startTime = startTime,
        endTime = endTime, returnLocation = returnLocation
    )

    private fun RentalHistoryEntity.toDomain() = RentalHistory(
        id = id, bikeName = bikeName, shopName = shopName,
        date = date, duration = duration, cost = cost
    )
}
