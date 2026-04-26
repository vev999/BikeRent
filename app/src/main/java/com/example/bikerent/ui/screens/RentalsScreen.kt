package com.example.bikerent.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bikerent.data.ActiveRental
import com.example.bikerent.data.RentalHistory
import com.example.bikerent.ui.components.BottomNavBar
import com.example.bikerent.ui.theme.Green800
import com.example.bikerent.ui.theme.Green900
import com.example.bikerent.viewmodel.AppViewModel

@Composable
fun RentalsScreen(navController: NavController, appViewModel: AppViewModel) {
    val activeRentals by appViewModel.activeRentals.collectAsState()
    val rentalHistory by appViewModel.rentalHistory.collectAsState()

    Scaffold(bottomBar = { BottomNavBar(navController) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                Surface(color = Green800, shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)) {
                    Row(
                        modifier = Modifier.padding(start = 4.dp, top = 40.dp, end = 16.dp, bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Wróć", tint = Color.White)
                        }
                        Text("Moje Wypożyczenia", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            item {
                Text("Aktywne wypożyczenia", fontSize = 17.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(10.dp))
            }

            if (activeRentals.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Text("Brak aktywnych wypożyczeń", modifier = Modifier.padding(16.dp), color = Color(0xFF666666))
                    }
                }
            } else {
                items(activeRentals) { rental ->
                    ActiveRentalCard(rental = rental, onReturn = { appViewModel.returnBike(rental) })
                    Spacer(Modifier.height(10.dp))
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                Text("Historia wypożyczeń", fontSize = 17.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(10.dp))
            }

            if (rentalHistory.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Text("Brak historii wypożyczeń", modifier = Modifier.padding(16.dp), color = Color(0xFF666666))
                    }
                }
            } else {
                item {
                    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)) {
                        Column {
                            rentalHistory.forEachIndexed { index, item ->
                                RentalHistoryItem(item)
                                if (index < rentalHistory.lastIndex) HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActiveRentalCard(rental: ActiveRental, onReturn: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp),
        border = BorderStroke(2.dp, Green800)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text(rental.bikeName, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                Surface(color = Green800, shape = RoundedCornerShape(8.dp)) {
                    Text("Aktywne", color = Color.White, fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(rental.shopName, color = Color(0xFF666666))
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.AccessTime, null, tint = Green800, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("od ${rental.startTime}", color = Color(0xFF666666), fontSize = 14.sp)
            }
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.LocationOn, null, tint = Green800, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(rental.returnLocation, color = Color(0xFF666666), fontSize = 14.sp)
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = onReturn, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green900)) {
                Text("Zwróć rower", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun RentalHistoryItem(rental: RentalHistory) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(rental.bikeName, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        Text(rental.shopName, color = Color(0xFF666666), fontSize = 13.sp)
        Text("${rental.date} • ${rental.duration}", color = Color(0xFF666666), fontSize = 13.sp)
        Spacer(Modifier.height(4.dp))
        Text("Koszt: ${rental.cost} zł", color = Green800, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}
