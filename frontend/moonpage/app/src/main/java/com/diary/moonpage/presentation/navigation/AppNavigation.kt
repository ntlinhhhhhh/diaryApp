package com.diary.moonpage.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.diary.moonpage.presentation.components.core.navigation.MoonBottomNavBar
import com.diary.moonpage.presentation.screens.auth.*
import com.diary.moonpage.presentation.screens.profile.ProfileScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Calendar.route,
        Screen.Stats.route,
        Screen.Store.route,
        Screen.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                MoonBottomNavBar(
                    selectedRoute = currentRoute ?: Screen.Calendar.route,
                    onItemSelected = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = Screen.Loading.route,
            modifier = Modifier.padding(paddingValues),
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300)) + fadeIn(tween(300))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300)) + fadeOut(tween(300))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300)) + fadeIn(tween(300))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300)) + fadeOut(tween(300))
            }
        ) {

            composable(Screen.Loading.route) {
                LoadingScreen(
                    onFinished = {
                        navController.navigate(Screen.Landing.route) {
                            popUpTo(Screen.Loading.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Landing.route) {
                LandingScreen(
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) }
                )
            }

            navigation(
                startDestination = Screen.Login.route,
                route = "auth_graph"
            ) {

                composable(Screen.Login.route) {
                    LoginScreen(
                        viewModel = hiltViewModel(),
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                        onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                        onNavigateToLoginGoogle = { /* ... */ },
                        onLoginSuccess = { token ->
                            navController.navigate(Screen.Calendar.route) {
                                popUpTo(0) { inclusive = true } // Xóa sạch lịch sử để vào Main App
                            }
                        }
                    )
                }

                composable(Screen.Register.route) {
                    RegisterScreen(
                        viewModel = hiltViewModel(), // Khởi tạo độc lập
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToLogin = { navController.popBackStack() }, // PopBackStack mượt hơn navigate
                        onNavigateToLoginGoogle = { /* ... */ },
                        onRegisterSuccess = {
                            navController.popBackStack() // Quay về màn Login
                        }
                    )
                }

                // Cụm 3 màn hình dùng chung 1 ViewModel
                composable(Screen.ForgotPassword.route) { entry ->
                    val sharedViewModel = entry.sharedViewModel<AuthViewModel>(navController)
                    ForgotPasswordScreen(
                        viewModel = sharedViewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToVerifyOtp = { navController.navigate(Screen.VerifyOtp.route) }
                    )
                }

                composable(Screen.VerifyOtp.route) { entry ->
                    val sharedViewModel = entry.sharedViewModel<AuthViewModel>(navController)
                    VerifyOtpScreen(
                        viewModel = sharedViewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToResetPassword = { _, _ -> navController.navigate(Screen.ResetPassword.route) }
                    )
                }

                composable(Screen.ResetPassword.route) { entry ->
                    val sharedViewModel = entry.sharedViewModel<AuthViewModel>(navController)
                    ResetPasswordScreen(
                        viewModel = sharedViewModel, // ĐÃ SỬA LỖI CÚ PHÁP
                        onNavigateToLogin = {
                            // Cập nhật pass xong, đá văng về tận Login
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    )
                }
            }

            // --- CÁC MÀN HÌNH MAIN APP HIỆN BOTTOM BAR ---
            composable(Screen.Calendar.route) { ProfileScreen() }
            composable(Screen.Stats.route) { ProfileScreen() }
            composable(Screen.Store.route) { ProfileScreen() }
            composable(Screen.Profile.route) { ProfileScreen() }
        }
    }
}

// Hàm mở rộng (Extension function) giúp chia sẻ ViewModel giữa các màn hình trong cùng 1 Graph
@Composable
inline fun <reified T : androidx.lifecycle.ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavController
): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentEntry)
}