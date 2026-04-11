package com.example.bikerent.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.bikerent.navigation.Screen
import com.example.bikerent.ui.theme.Green800
import com.example.bikerent.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopProfileScreen(navController: NavController, shopId: String, appViewModel: AppViewModel) {
    val shops by appViewModel.shops.collectAsState()
    val bikes by appViewModel.bikes.collectAsState()
    val shop = shops.find { it.id == shopId } ?: return
    val shopBikes = bikes.filter { it.id in shop.bikeIds }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil sklepu", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Wróć")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            item {
                AsyncImage(
                    model = shop.image, contentDescription = shop.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(250.dp)
                )
            }
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Card(shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp),
                        modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(shop.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(shop.rating.toString(), color = Color(0xFF666666))
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(Icons.Filled.LocationOn, null, tint = Green800, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(shop.location, color = Color(0xFF666666))
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(shop.description, color = Color(0xFF666666), lineHeight = 22.sp)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("Dostępne rowery", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                }
            }
            items(shopBikes) { bike ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                        .clickable { navController.navigate(Screen.BikeDetail.createRoute(bike.id)) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(modifier = Modifier.height(120.dp)) {
                        AsyncImage(
                            model = bike.image, contentDescription = bike.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.width(120.dp)
                                .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                        )
                        Column(modifier = Modifier.padding(12.dp).fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                    Text(bike.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                    AvailabilityChip(bike.available)
                                }
                                Spacer(Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(2.dp))
                                    Text(bike.rating.toString(), fontSize = 12.sp, color = Color(0xFF666666))
                                }
                            }
                            Text("${bike.price} zł / dzień", fontWeight = FontWeight.Bold, color = Green800)
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}
