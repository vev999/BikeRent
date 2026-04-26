package com.example.bikerent.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bikerent.navigation.Screen
import com.example.bikerent.ui.components.BottomNavBar
import com.example.bikerent.ui.theme.Green800
import com.example.bikerent.ui.theme.Red700
import com.example.bikerent.viewmodel.AppViewModel
import com.example.bikerent.viewmodel.AuthViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    appViewModel: AppViewModel
) {
    val userName = authViewModel.currentUserName
    val userEmail = authViewModel.currentUserEmail
    val activeRentals by appViewModel.activeRentals.collectAsState()
    val rentalHistory by appViewModel.rentalHistory.collectAsState()
    val totalRentals = activeRentals.size + rentalHistory.size

    val isAdmin = authViewModel.isAdmin
    val menuItems = buildList {
        add(Triple(Icons.Filled.Settings, "Ustawienia konta", Screen.Settings.route))
        add(Triple(Icons.Filled.History, "Historia wypożyczeń", Screen.Rentals.route))
        add(Triple(Icons.Filled.Star, "Moje oceny", Screen.MyReviews.route))
        if (isAdmin) add(Triple(Icons.Filled.AdminPanelSettings, "Panel administracyjny", Screen.Admin.route))
    }

    Scaffold(bottomBar = { BottomNavBar(navController) }) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 16.dp)) {
            item {
                Surface(color = Green800, shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)) {
                    Column(modifier = Modifier.padding(16.dp, 40.dp, 16.dp, 40.dp)) {
                        Text("Profil", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Card(shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(6.dp),
                        modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(shape = CircleShape, color = Green800, modifier = Modifier.size(96.dp)) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    Text(userName.firstOrNull()?.toString() ?: "?",
                                        color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            Text(userName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text(userEmail, color = Color(0xFF666666), fontSize = 14.sp)
                            Spacer(Modifier.height(16.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                    Text(totalRentals.toString(), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Green800)
                                    Text("Wypożyczenia", fontSize = 12.sp, color = Color(0xFF666666))
                                }
                                VerticalDivider(modifier = Modifier.height(40.dp))
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                    Text("4.8", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Green800)
                                    Text("Ocena", fontSize = 12.sp, color = Color(0xFF666666))
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))

                    Card(shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)) {
                        Column {
                            menuItems.forEachIndexed { index, (icon, label, route) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .clickable { navController.navigate(route) }
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(icon, null, tint = Green800)
                                    Spacer(Modifier.width(12.dp))
                                    Text(label, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                                    Icon(Icons.Filled.ChevronRight, null, tint = Color(0xFFBBBBBB))
                                }
                                if (index < menuItems.lastIndex) HorizontalDivider()
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = {
                            authViewModel.logout()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Red700),
                        border = BorderStroke(1.dp, Red700)
                    ) {
                        Icon(Icons.Filled.Logout, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Wyloguj się", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
