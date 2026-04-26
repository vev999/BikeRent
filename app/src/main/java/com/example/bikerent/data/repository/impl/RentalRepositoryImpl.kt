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

        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val now = Date()
        val returnTime = sdf.format(now)
        val startDate = try { sdf.parse(rental.startTime) } catch (e: Exception) { null }
        val durationMinutes = if (startDate != null) (now.time - startDate.time) / 60000L else 0L

        val durationText = when {
            durationMinutes < 1 -> "mniej niż 1 min"
            durationMinutes < 60 -> "$durationMinutes min"
            else -> {
                val h = durationMinutes / 60
                val m = durationMinutes % 60
                if (m == 0L) "$h h" else "$h h $m min"
            }
        }
        val billedHours = maxOf(1, ((durationMinutes + 59) / 60).toInt())

        rentalHistoryDao.insert(
            RentalHistoryEntity(
                id = "h_${System.currentTimeMillis()}",
                bikeName = rental.bikeName,
                shopName = rental.shopName,
                date = "${rental.startTime} – $returnTime",
                duration = durationText,
                cost = bikePrice * billedHours,
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
