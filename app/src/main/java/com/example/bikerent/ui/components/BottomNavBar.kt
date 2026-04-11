package com.example.bikerent.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.bikerent.navigation.Screen
import com.example.bikerent.ui.theme.Green800

data class NavItem(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val screen: Screen)

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        NavItem("Home", Icons.Filled.Home, Screen.Home),
        NavItem("Wypożyczenia", Icons.Filled.DirectionsBike, Screen.Rentals),
        NavItem("Profil", Icons.Filled.Person, Screen.Profile),
        NavItem("Ustawienia", Icons.Filled.Settings, Screen.Settings),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(containerColor = Color.White) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.screen.route,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Green800,
                    selectedTextColor = Green800,
                    indicatorColor = Color(0xFFE8F5E9),
                    unselectedIconColor = Color(0xFF666666),
                    unselectedTextColor = Color(0xFF666666)
                )
            )
        }
    }
}
