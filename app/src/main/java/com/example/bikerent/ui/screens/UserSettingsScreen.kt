package com.example.bikerent.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bikerent.ui.components.BottomNavBar
import com.example.bikerent.ui.theme.Green100
import com.example.bikerent.ui.theme.Green800
import com.example.bikerent.viewmodel.AppViewModel
import com.example.bikerent.viewmodel.AuthViewModel

@Composable
fun UserSettingsScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    appViewModel: AppViewModel
) {
    var editMode by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf(authViewModel.currentUserName) }
    var userEmail by remember { mutableStateOf(authViewModel.currentUserEmail) }

    val activeRentals by appViewModel.activeRentals.collectAsState()
    val rentalHistory by appViewModel.rentalHistory.collectAsState()

    Scaffold(bottomBar = { BottomNavBar(navController) }) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 16.dp)) {
            item {
                Surface(color = Green800, shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)) {
                    Row(
                        modifier = Modifier.padding(start = 4.dp, top = 40.dp, end = 16.dp, bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Wróć", tint = Color.White)
                        }
                        Text("Ustawienia konta", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    // Avatar card
                    Card(shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(3.dp),
                        modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(shape = CircleShape, color = Green800, modifier = Modifier.size(80.dp)) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    Text(userName.firstOrNull()?.toString() ?: "?",
                                        color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            IconButton(onClick = {
                                if (editMode) {
                                    authViewModel.updateUserData(userName, userEmail)
                                }
                                editMode = !editMode
                            }) {
                                Surface(color = if (editMode) Green800 else Green100, shape = CircleShape) {
                                    Icon(
                                        if (editMode) Icons.Filled.Save else Icons.Filled.Edit,
                                        if (editMode) "Zapisz" else "Edytuj",
                                        tint = if (editMode) Color.White else Green800,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Personal info
                    Card(shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Dane osobowe", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(12.dp))
                            OutlinedTextField(value = userName, onValueChange = { if (editMode) userName = it },
                                label = { Text("Nazwa użytkownika") }, leadingIcon = { Icon(Icons.Filled.Person, null) },
                                enabled = editMode, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                            Spacer(Modifier.height(10.dp))
                            OutlinedTextField(value = userEmail, onValueChange = { if (editMode) userEmail = it },
                                label = { Text("Adres e-mail") }, leadingIcon = { Icon(Icons.Filled.Mail, null) },
                                enabled = editMode, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                            Spacer(Modifier.height(10.dp))
                            OutlinedTextField(value = "••••••••", onValueChange = {},
                                label = { Text("Hasło") }, leadingIcon = { Icon(Icons.Filled.Lock, null) },
                                enabled = false, visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                            if (editMode) {
                                Spacer(Modifier.height(12.dp))
                                Button(onClick = { authViewModel.updateUserData(userName, userEmail); editMode = false },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Green800)) {
                                    Text("Zapisz zmiany", fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Rental history
                    Card(shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Historia wypożyczeń", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            if (rentalHistory.isEmpty()) {
                                Text("Brak historii", color = Color(0xFF666666))
                            } else {
                                rentalHistory.forEachIndexed { i, rental ->
                                    Column {
                                        Text(rental.bikeName, fontWeight = FontWeight.SemiBold)
                                        Text("${rental.shopName} • ${rental.date} • ${rental.duration} • ${rental.cost} zł",
                                            color = Color(0xFF666666), fontSize = 13.sp)
                                    }
                                    if (i < rentalHistory.lastIndex) HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Active rentals
                    Card(shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Aktualne wypożyczenia", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            if (activeRentals.isEmpty()) {
                                Text("Brak aktywnych wypożyczeń", color = Color(0xFF666666))
                            } else {
                                activeRentals.forEach { rental ->
                                    Surface(color = Green100, shape = RoundedCornerShape(12.dp)) {
                                        Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                                            Text(rental.bikeName, fontWeight = FontWeight.SemiBold)
                                            Text(rental.shopName, color = Color(0xFF666666), fontSize = 13.sp)
                                            Text("od ${rental.startTime}", color = Color(0xFF666666), fontSize = 13.sp)
                                            Text("Zwrot: ${rental.returnLocation}", color = Color(0xFF666666), fontSize = 13.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
