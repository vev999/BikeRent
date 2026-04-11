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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bikerent.data.DataSource
import com.example.bikerent.ui.theme.Blue700
import com.example.bikerent.ui.theme.DarkBackground
import com.example.bikerent.ui.theme.Green100
import com.example.bikerent.ui.theme.Green800
import com.example.bikerent.ui.theme.Orange700
import com.example.bikerent.ui.theme.Purple700
import com.example.bikerent.ui.theme.Red100
import com.example.bikerent.ui.theme.Red700
import com.example.bikerent.ui.theme.Red800
import com.example.bikerent.viewmodel.AppViewModel

data class StatCard(val icon: ImageVector, val label: String, val value: String, val color: Color)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(navController: NavController, appViewModel: AppViewModel) {
    var showAddDialog by remember { mutableStateOf(false) }
    var newBikeName by remember { mutableStateOf("") }

    val bikes by appViewModel.bikes.collectAsState()
    val shops by appViewModel.shops.collectAsState()

    val stats = listOf(
        StatCard(Icons.Filled.DirectionsBike, "Rowery", bikes.size.toString(), Green800),
        StatCard(Icons.Filled.Store, "Sklepy", shops.size.toString(), Blue700),
        StatCard(Icons.Filled.People, "Użytkownicy", "247", Orange700),
        StatCard(Icons.Filled.TrendingUp, "Wypożyczenia", "1543", Purple700),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Panel Administracyjny", fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Zarządzaj aplikacją BikeRent", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Wróć", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp)) {
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    stats.take(2).forEach { StatCardItem(it, Modifier.weight(1f)) }
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    stats.drop(2).forEach { StatCardItem(it, Modifier.weight(1f)) }
                }
                Spacer(Modifier.height(16.dp))
            }

            // Bikes management
            item {
                Card(shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                            Text("Zarządzanie Rowerami", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Button(onClick = { showAddDialog = true }, shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Green800),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)) {
                                Icon(Icons.Filled.Add, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Dodaj", fontSize = 13.sp)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                            Text("Nazwa", fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f), fontSize = 13.sp)
                            Text("Cena", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), fontSize = 13.sp)
                            Text("Status", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f), fontSize = 13.sp)
                            Spacer(Modifier.weight(0.8f))
                        }
                        HorizontalDivider()
                        bikes.take(3).forEach { bike ->
                            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(bike.name, modifier = Modifier.weight(2f), fontSize = 13.sp)
                                Text("${bike.price} zł", modifier = Modifier.weight(1f), fontSize = 13.sp)
                                Surface(color = if (bike.available) Green100 else Red100,
                                    shape = RoundedCornerShape(6.dp), modifier = Modifier.weight(1.5f)) {
                                    Text(if (bike.available) "Dostępny" else "Niedostępny",
                                        color = if (bike.available) Green800 else Red800,
                                        fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                                }
                                Row(modifier = Modifier.weight(0.8f)) {
                                    IconButton(onClick = {}, modifier = Modifier.size(28.dp)) {
                                        Icon(Icons.Filled.Edit, null, tint = Blue700, modifier = Modifier.size(16.dp))
                                    }
                                    IconButton(onClick = {}, modifier = Modifier.size(28.dp)) {
                                        Icon(Icons.Filled.Delete, null, tint = Red700, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                            HorizontalDivider(color = Color(0xFFF5F5F5))
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // Users management
            item {
                Card(shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Zarządzanie Użytkownikami", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                            Text("Nazwa", fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f), fontSize = 13.sp)
                            Text("Wypoż.", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), fontSize = 13.sp)
                            Text("Status", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f), fontSize = 13.sp)
                            Spacer(Modifier.weight(0.5f))
                        }
                        HorizontalDivider()
                        DataSource.adminPanelUsers.forEach { user ->
                            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(2f)) {
                                    Text(user.name, fontSize = 13.sp)
                                    Text(user.email, fontSize = 11.sp, color = Color(0xFF666666))
                                }
                                Text(user.rentals.toString(), modifier = Modifier.weight(1f), fontSize = 13.sp)
                                Surface(color = if (user.status == "Aktywny") Green100 else Red100,
                                    shape = RoundedCornerShape(6.dp), modifier = Modifier.weight(1.5f)) {
                                    Text(user.status,
                                        color = if (user.status == "Aktywny") Green800 else Red800,
                                        fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                                }
                                IconButton(onClick = {}, modifier = Modifier.size(28.dp).weight(0.5f)) {
                                    Icon(Icons.Filled.Delete, null, tint = Red700, modifier = Modifier.size(16.dp))
                                }
                            }
                            HorizontalDivider(color = Color(0xFFF5F5F5))
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // Comments moderation
            item {
                Card(shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Moderacja Komentarzy", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        DataSource.adminComments.forEachIndexed { index, comment ->
                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(comment.user, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                        Text(comment.bike, color = Color(0xFF666666), fontSize = 12.sp)
                                        Text(comment.comment, fontSize = 13.sp)
                                        Text(comment.date, color = Color(0xFF666666), fontSize = 11.sp)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Surface(
                                            color = if (comment.status == "Zaakceptowany") Green100 else Color(0xFFFFF3E0),
                                            shape = RoundedCornerShape(6.dp)) {
                                            Text(comment.status,
                                                color = if (comment.status == "Zaakceptowany") Green800 else Orange700,
                                                fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                                        }
                                        IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                                            Icon(Icons.Filled.Delete, null, tint = Red700, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                            if (index < DataSource.adminComments.lastIndex) HorizontalDivider()
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Dodaj nowy rower") },
            text = {
                OutlinedTextField(value = newBikeName, onValueChange = { newBikeName = it },
                    label = { Text("Nazwa roweru") }, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp))
            },
            confirmButton = {
                Button(onClick = { showAddDialog = false; newBikeName = "" },
                    colors = ButtonDefaults.buttonColors(containerColor = Green800)) { Text("Dodaj") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Anuluj") }
            }
        )
    }
}

@Composable
private fun StatCardItem(stat: StatCard, modifier: Modifier = Modifier) {
    Card(shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp), modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Surface(color = stat.color.copy(alpha = 0.12f), shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(44.dp)) {
                Icon(stat.icon, null, tint = stat.color, modifier = Modifier.padding(10.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(stat.value, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text(stat.label, color = Color(0xFF666666), fontSize = 13.sp)
        }
    }
}
