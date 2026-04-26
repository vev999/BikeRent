package com.example.bikerent.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.bikerent.ui.theme.Green800
import com.example.bikerent.viewmodel.AppViewModel

@Composable
fun MyReviewsScreen(navController: NavController, appViewModel: AppViewModel) {
    val reviews by appViewModel.userReviews.collectAsState()

    Scaffold { padding ->
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
                        Text("Moje oceny", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            if (reviews.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Text(
                            "Nie dodałeś jeszcze żadnej opinii.",
                            modifier = Modifier.padding(16.dp),
                            color = Color(0xFF666666)
                        )
                    }
                }
            } else {
                items(reviews) { review ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                Text(review.bikeName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(review.date, fontSize = 12.sp, color = Color(0xFF888888))
                            }
                            Spacer(Modifier.height(4.dp))
                            Row {
                                repeat(5) { i ->
                                    Icon(
                                        Icons.Filled.Star, null,
                                        tint = if (i < review.rating.toInt()) Color(0xFFFFC107) else Color(0xFFE0E0E0),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(review.comment, color = Color(0xFF444444), fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}
