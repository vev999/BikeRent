package com.example.bikerent.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bikerent.ui.components.ScreenHeader
import com.example.bikerent.ui.theme.Green800
import com.example.bikerent.viewmodel.AppViewModel
import kotlin.math.roundToInt

@Composable
fun UserProfileScreen(
    navController: NavController,
    userId: Long,
    appViewModel: AppViewModel
) {
    val reviews by appViewModel.viewedUserReviews.collectAsState()

    LaunchedEffect(userId) {
        appViewModel.loadReviewsForUser(userId)
    }

    val userName = reviews.firstOrNull()?.userName ?: "Użytkownik"
    val avgRating = if (reviews.isEmpty()) null
    else (reviews.map { it.rating }.average() * 10).roundToInt() / 10f

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                ScreenHeader(title = "Profil użytkownika", onBack = { navController.popBackStack() })
                Spacer(Modifier.height(16.dp))
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(shape = CircleShape, color = Green800, modifier = Modifier.size(80.dp)) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Text(
                                    userName.firstOrNull()?.toString() ?: "?",
                                    color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(userName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        if (avgRating != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    "Średnia wystawionych ocen: $avgRating",
                                    color = Color(0xFF666666), fontSize = 14.sp
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "${reviews.size} ${pluralOpinie(reviews.size)}",
                            color = Color(0xFF888888), fontSize = 13.sp
                        )
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
                            "Ten użytkownik nie dodał jeszcze żadnej opinii.",
                            modifier = Modifier.padding(16.dp),
                            color = Color(0xFF666666)
                        )
                    }
                }
            } else {
                item {
                    Text(
                        "Opinie użytkownika",
                        fontSize = 16.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                }
                items(reviews) { review ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    review.bikeName,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(review.date, fontSize = 12.sp, color = Color(0xFF888888))
                            }
                            Spacer(Modifier.height(6.dp))
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

private fun pluralOpinie(n: Int): String = when {
    n == 1 -> "opinia"
    n in 2..4 -> "opinie"
    else -> "opinii"
}
