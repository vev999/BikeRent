package com.example.bikerent.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bikerent.BikeRentApp
import com.example.bikerent.ui.screens.AdminPanelScreen
import com.example.bikerent.ui.screens.BikeDetailScreen
import com.example.bikerent.ui.screens.HomeScreen
import com.example.bikerent.ui.screens.LoginScreen
import com.example.bikerent.ui.screens.MyReviewsScreen
import com.example.bikerent.ui.screens.ProfileScreen
import com.example.bikerent.ui.screens.RentalsScreen
import com.example.bikerent.ui.screens.ShopProfileScreen
import com.example.bikerent.ui.screens.UserSettingsScreen
import com.example.bikerent.viewmodel.AppViewModelFactory
import com.example.bikerent.viewmodel.AuthState
import com.example.bikerent.viewmodel.AuthViewModel
import com.example.bikerent.viewmodel.AuthViewModelFactory
import com.example.bikerent.viewmodel.AppViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object BikeDetail : Screen("bike/{bikeId}") {
        fun createRoute(bikeId: String) = "bike/$bikeId"
    }
    object ShopProfile : Screen("shop/{shopId}") {
        fun createRoute(shopId: String) = "shop/$shopId"
    }
    object Rentals : Screen("rentals")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object Admin : Screen("admin")
    object MyReviews : Screen("my_reviews")
}

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()
    val app = LocalContext.current.applicationContext as BikeRentApp

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(app.userRepository)
    )
    val appViewModel: AppViewModel = viewModel(
        factory = AppViewModelFactory(app.bikeRepository, app.shopRepository, app.rentalRepository, app.reviewRepository)
    )

    val authState by authViewModel.authState.collectAsState()
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            val state = authState as AuthState.Success
            appViewModel.initForUser(state.userId, state.name)
        }
    }

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController, appViewModel = appViewModel)
        }
        composable(Screen.BikeDetail.route) { backStackEntry ->
            val bikeId = backStackEntry.arguments?.getString("bikeId") ?: return@composable
            BikeDetailScreen(navController = navController, bikeId = bikeId, appViewModel = appViewModel)
        }
        composable(Screen.ShopProfile.route) { backStackEntry ->
            val shopId = backStackEntry.arguments?.getString("shopId") ?: return@composable
            ShopProfileScreen(navController = navController, shopId = shopId, appViewModel = appViewModel)
        }
        composable(Screen.Rentals.route) {
            RentalsScreen(navController = navController, appViewModel = appViewModel)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController, authViewModel = authViewModel, appViewModel = appViewModel)
        }
        composable(Screen.Settings.route) {
            UserSettingsScreen(navController = navController, authViewModel = authViewModel, appViewModel = appViewModel)
        }
        composable(Screen.Admin.route) {
            AdminPanelScreen(navController = navController, appViewModel = appViewModel)
        }
        composable(Screen.MyReviews.route) {
            MyReviewsScreen(navController = navController, appViewModel = appViewModel)
        }
    }
}
