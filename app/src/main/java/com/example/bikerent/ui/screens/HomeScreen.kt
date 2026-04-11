package com.example.bikerent.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.bikerent.data.Bike
import com.example.bikerent.navigation.Screen
import com.example.bikerent.ui.components.BottomNavBar
import com.example.bikerent.ui.theme.Green100
import com.example.bikerent.ui.theme.Green800
import com.example.bikerent.ui.theme.Red100
import com.example.bikerent.ui.theme.Red800
import com.example.bikerent.viewmodel.AppViewModel

@Composable
fun HomeScreen(navController: NavController, appViewModel: AppViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    val allBikes by appViewModel.bikes.collectAsState()

    val filteredBikes = allBikes.filter { bike ->
        bike.name.contains(searchQuery, ignoreCase = true) ||
                bike.category.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(bottomBar = { BottomNavBar(navController) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                Surface(
                    color = Green800,
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp, 40.dp, 16.dp, 16.dp)) {
                        Text("Znajdź swój rower", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Szukaj roweru...", color = Color.Gray) },
                            leadingIcon = { Icon(Icons.Filled.Search, null, tint = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            singleLine = true
                        )
                    }
                }
            }

            item {
                Spacer(Modifier.height(16.dp))
                Text("Polecane rowery", fontSize = 18.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(12.dp))
            }

            if (allBikes.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Green800)
                    }
                }
            } else {
                items(filteredBikes) { bike ->
                    BikeCard(bike = bike, onClick = { navController.navigate(Screen.BikeDetail.createRoute(bike.id)) })
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun BikeCard(bike: Bike, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            AsyncImage(
                model = bike.image, contentDescription = bike.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(200.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text(bike.name, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    AvailabilityChip(bike.available)
                }
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(bike.rating.toString(), fontSize = 13.sp, color = Color(0xFF666666))
                    Spacer(Modifier.width(8.dp))
                    Text("•", color = Color(0xFF666666))
                    Spacer(Modifier.width(8.dp))
                    Text(bike.category, fontSize = 13.sp, color = Color(0xFF666666))
                }
                Spacer(Modifier.height(8.dp))
                Text("${bike.price} zł / dzień", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Green800)
            }
        }
    }
}

@Composable
fun AvailabilityChip(available: Boolean) {
    Surface(
        color = if (available) Green100 else Red100,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            if (available) "Dostępny" else "Niedostępny",
            color = if (available) Green800 else Red800,
            fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
