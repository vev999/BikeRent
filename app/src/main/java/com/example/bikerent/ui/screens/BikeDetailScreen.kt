package com.example.bikerent.ui.screens

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.example.bikerent.data.Review
import com.example.bikerent.navigation.Screen
import com.example.bikerent.ui.theme.Green100
import com.example.bikerent.ui.theme.Green800
import com.example.bikerent.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BikeDetailScreen(navController: NavController, bikeId: String, appViewModel: AppViewModel) {
    val bikes by appViewModel.bikes.collectAsState()
    val shops by appViewModel.shops.collectAsState()
    val bike = bikes.find { it.id == bikeId } ?: return
    val shop = shops.find { it.id == bike.shopId }

    var showRentDialog by remember { mutableStateOf(false) }
    var rentSuccess by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState { bike.images.size }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Szczegóły roweru", fontWeight = FontWeight.SemiBold) },
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
                Box {
                    HorizontalPager(state = pagerState) { page ->
                        AsyncImage(
                            model = bike.images[page], contentDescription = bike.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth().height(300.dp)
                        )
                    }
                    if (bike.images.size > 1) {
                        Row(
                            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            repeat(bike.images.size) { index ->
                                Box(
                                    modifier = Modifier
                                        .size(if (pagerState.currentPage == index) 10.dp else 7.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (pagerState.currentPage == index) Green800
                                            else Color.White.copy(alpha = 0.7f)
                                        )
                                )
                            }
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Card(shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(3.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                Text(bike.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                AvailabilityChip(bike.available)
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("${bike.rating} (${bike.reviews.size} opinii)", color = Color(0xFF666666))
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(bike.description, color = Color(0xFF666666), lineHeight = 22.sp)
                            Spacer(Modifier.height(12.dp))
                            Text("${bike.price} zł / dzień", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Green800)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                                            .startTone(ToneGenerator.TONE_PROP_BEEP2, 500)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Filled.VolumeUp, null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Dzwonek")
                                }
                                if (shop != null) {
                                    OutlinedButton(
                                        onClick = { navController.navigate(Screen.ShopProfile.createRoute(shop.id)) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Filled.Store, null, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Sklep")
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))

                            Button(
                                onClick = { if (bike.available && !rentSuccess) showRentDialog = true },
                                enabled = bike.available && !rentSuccess,
                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Green800)
                            ) {
                                Text(
                                    when {
                                        rentSuccess -> "Wypożyczono!"
                                        bike.available -> "Wypożycz teraz"
                                        else -> "Obecnie niedostępny"
                                    },
                                    fontSize = 16.sp, fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Card(shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(3.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Opinie użytkowników", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(12.dp))
                            if (bike.reviews.isEmpty()) {
                                Text("Brak opinii. Bądź pierwszą osobą!", color = Color(0xFF666666))
                            } else {
                                bike.reviews.forEachIndexed { index, review ->
                                    ReviewItem(review)
                                    if (index < bike.reviews.lastIndex)
                                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }

    if (showRentDialog && shop != null) {
        AlertDialog(
            onDismissRequest = { showRentDialog = false },
            title = { Text("Potwierdź wypożyczenie") },
            text = {
                Column {
                    Text("Rower: ${bike.name}")
                    Text("Sklep: ${shop.name}")
                    Text("Cena: ${bike.price} zł / dzień")
                    Spacer(Modifier.height(8.dp))
                    Surface(color = Green100, shape = RoundedCornerShape(8.dp)) {
                        Text("Wypożyczenie zostanie dodane do aktywnych.",
                            modifier = Modifier.padding(8.dp), color = Green800, fontSize = 13.sp)
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    appViewModel.rentBike(bike, shop)
                    showRentDialog = false
                    rentSuccess = true
                }, colors = ButtonDefaults.buttonColors(containerColor = Green800)) {
                    Text("Wypożycz")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRentDialog = false }) { Text("Anuluj") }
            }
        )
    }
}

@Composable
private fun ReviewItem(review: Review) {
    Row(verticalAlignment = Alignment.Top) {
        Surface(shape = CircleShape, color = Green800, modifier = Modifier.size(40.dp)) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(review.userName.first().toString(), color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(review.userName, fontWeight = FontWeight.SemiBold)
            Text(review.date, fontSize = 12.sp, color = Color(0xFF666666))
            Row {
                repeat(5) { i ->
                    Icon(Icons.Filled.Star, null,
                        tint = if (i < review.rating.toInt()) Color(0xFFFFC107) else Color(0xFFE0E0E0),
                        modifier = Modifier.size(14.dp))
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(review.comment, color = Color(0xFF666666), fontSize = 14.sp)
        }
    }
}
