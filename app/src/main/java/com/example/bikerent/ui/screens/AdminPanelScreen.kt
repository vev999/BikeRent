package com.example.bikerent.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.bikerent.data.Review
import com.example.bikerent.data.util.ImageUtils
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val categories = listOf("Miejski", "Górski", "Elektryczny", "Szosowy", "Cruiser", "Hybrydowy")

data class StatCard(val icon: ImageVector, val label: String, val value: String, val color: Color)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(navController: NavController, appViewModel: AppViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showAddDialog by remember { mutableStateOf(false) }
    var reviewToDelete by remember { mutableStateOf<Review?>(null) }

    // Add bike form state
    var newBikeName by remember { mutableStateOf("") }
    var newBikeDescription by remember { mutableStateOf("") }
    var newBikePrice by remember { mutableStateOf("") }
    var newBikeCategory by remember { mutableStateOf(categories.first()) }
    var newBikeShopId by remember { mutableStateOf("") }
    var selectedImagePath by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var shopExpanded by remember { mutableStateOf(false) }

    val bikes by appViewModel.bikes.collectAsState()
    val shops by appViewModel.shops.collectAsState()
    val allReviews by appViewModel.allReviews.collectAsState()

    LaunchedEffect(Unit) { appViewModel.loadAllReviews() }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            scope.launch(Dispatchers.IO) {
                val path = ImageUtils.copyToAppStorage(context, it)
                withContext(Dispatchers.Main) { if (path != null) selectedImagePath = path }
            }
        }
    }

    fun resetForm() {
        newBikeName = ""; newBikeDescription = ""; newBikePrice = ""
        newBikeCategory = categories.first(); newBikeShopId = ""
        selectedImagePath = ""; selectedImageUri = null
    }

    val stats = listOf(
        StatCard(Icons.Filled.DirectionsBike, "Rowery", bikes.size.toString(), Green800),
        StatCard(Icons.Filled.Store, "Sklepy", shops.size.toString(), Blue700),
        StatCard(Icons.Filled.RateReview, "Opinie", allReviews.size.toString(), Orange700),
        StatCard(Icons.Filled.TrendingUp, "Wypożyczenia", "—", Purple700),
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
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
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
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                            Text("Zarządzanie Rowerami", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Button(
                                onClick = { showAddDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Green800),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
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
                        bikes.take(5).forEach { bike ->
                            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(bike.name, modifier = Modifier.weight(2f), fontSize = 13.sp)
                                Text("${bike.price} zł", modifier = Modifier.weight(1f), fontSize = 13.sp)
                                Surface(
                                    color = if (bike.available) Green100 else Red100,
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.weight(1.5f)
                                ) {
                                    Text(
                                        if (bike.available) "Dostępny" else "Niedostępny",
                                        color = if (bike.available) Green800 else Red800,
                                        fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
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

            // Reviews moderation
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Moderacja Opinii (${allReviews.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        if (allReviews.isEmpty()) {
                            Text("Brak opinii.", color = Color(0xFF666666))
                        } else {
                            allReviews.forEachIndexed { index, review ->
                                Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(review.userName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                        Text(review.bikeName, color = Color(0xFF666666), fontSize = 12.sp)
                                        Row {
                                            repeat(5) { i ->
                                                Icon(Icons.Filled.Star, null,
                                                    tint = if (i < review.rating.toInt()) Color(0xFFFFC107) else Color(0xFFE0E0E0),
                                                    modifier = Modifier.size(12.dp))
                                            }
                                        }
                                        Text(review.comment, fontSize = 13.sp)
                                        Text(review.date, color = Color(0xFF666666), fontSize = 11.sp)
                                    }
                                    IconButton(onClick = { reviewToDelete = review }, modifier = Modifier.size(36.dp)) {
                                        Icon(Icons.Filled.Delete, null, tint = Red700, modifier = Modifier.size(20.dp))
                                    }
                                }
                                if (index < allReviews.lastIndex) HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }

    // ── Add bike dialog ────────────────────────────────────────────────────
    if (showAddDialog) {
        Dialog(onDismissRequest = { showAddDialog = false; resetForm() }) {
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("Dodaj nowy rower", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = newBikeName, onValueChange = { newBikeName = it },
                        label = { Text("Nazwa roweru") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true
                    )
                    Spacer(Modifier.height(10.dp))

                    OutlinedTextField(
                        value = newBikeDescription, onValueChange = { newBikeDescription = it },
                        label = { Text("Opis") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), minLines = 2
                    )
                    Spacer(Modifier.height(10.dp))

                    OutlinedTextField(
                        value = newBikePrice, onValueChange = { newBikePrice = it.filter { c -> c.isDigit() } },
                        label = { Text("Cena (zł / dzień)") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true
                    )
                    Spacer(Modifier.height(10.dp))

                    // Category dropdown
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = newBikeCategory, onValueChange = {},
                            readOnly = true, label = { Text("Kategoria") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(categoryExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(), shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = { newBikeCategory = cat; categoryExpanded = false }
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))

                    // Shop dropdown
                    val selectedShopName = shops.find { it.id == newBikeShopId }?.name ?: "Wybierz sklep"
                    ExposedDropdownMenuBox(
                        expanded = shopExpanded,
                        onExpandedChange = { shopExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedShopName, onValueChange = {},
                            readOnly = true, label = { Text("Sklep") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(shopExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(), shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(expanded = shopExpanded, onDismissRequest = { shopExpanded = false }) {
                            shops.forEach { shop ->
                                DropdownMenuItem(
                                    text = { Text(shop.name) },
                                    onClick = { newBikeShopId = shop.id; shopExpanded = false }
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))

                    // Image preview
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri, contentDescription = "Podgląd zdjęcia",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth().height(140.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                        Spacer(Modifier.height(8.dp))
                    }

                    OutlinedButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.Image, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(if (selectedImageUri == null) "Wybierz zdjęcie" else "Zmień zdjęcie")
                    }
                    Spacer(Modifier.height(16.dp))

                    val isValid = newBikeName.isNotBlank()
                            && newBikeDescription.isNotBlank()
                            && newBikePrice.toIntOrNull() != null
                            && newBikeShopId.isNotEmpty()
                            && selectedImagePath.isNotEmpty()

                    Row(Modifier.fillMaxWidth(), Arrangement.End, Alignment.CenterVertically) {
                        TextButton(onClick = { showAddDialog = false; resetForm() }) { Text("Anuluj") }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                appViewModel.addBike(
                                    name = newBikeName.trim(),
                                    description = newBikeDescription.trim(),
                                    shopId = newBikeShopId,
                                    price = newBikePrice.toInt(),
                                    category = newBikeCategory,
                                    imagePath = selectedImagePath
                                )
                                showAddDialog = false
                                resetForm()
                            },
                            enabled = isValid,
                            colors = ButtonDefaults.buttonColors(containerColor = Green800)
                        ) { Text("Dodaj rower") }
                    }
                }
            }
        }
    }

    // Confirm review delete dialog
    reviewToDelete?.let { review ->
        AlertDialog(
            onDismissRequest = { reviewToDelete = null },
            title = { Text("Usuń opinię") },
            text = { Text("Czy na pewno chcesz usunąć opinię użytkownika ${review.userName}?") },
            confirmButton = {
                Button(
                    onClick = { appViewModel.deleteReview(review); reviewToDelete = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Red700)
                ) { Text("Usuń") }
            },
            dismissButton = { TextButton(onClick = { reviewToDelete = null }) { Text("Anuluj") } }
        )
    }
}

@Composable
private fun StatCardItem(stat: StatCard, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Surface(
                color = stat.color.copy(alpha = 0.12f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(44.dp)
            ) {
                Icon(stat.icon, null, tint = stat.color, modifier = Modifier.padding(10.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(stat.value, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text(stat.label, color = Color(0xFF666666), fontSize = 13.sp)
        }
    }
}
