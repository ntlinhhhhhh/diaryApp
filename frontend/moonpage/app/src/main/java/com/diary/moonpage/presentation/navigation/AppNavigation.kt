package com.diary.moonpage.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.diary.moonpage.presentation.components.core.navigation.MoonBottomNavBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.diary.moonpage.presentation.screens.auth.AuthViewModel
import com.diary.moonpage.presentation.screens.auth.ForgotPasswordScreen
import com.diary.moonpage.presentation.screens.auth.LandingScreen
import com.diary.moonpage.presentation.screens.auth.LoadingScreen
import com.diary.moonpage.presentation.screens.auth.LoginScreen
import com.diary.moonpage.presentation.screens.auth.RegisterScreen
import com.diary.moonpage.presentation.screens.auth.ResetPasswordScreen
import com.diary.moonpage.presentation.screens.auth.VerifyOtpScreen
import com.diary.moonpage.presentation.screens.profile.ProfileScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val authViewModel: AuthViewModel = hiltViewModel()


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
            // HIỆU ỨNG CHUYỂN TRANG: Slide + Fade giúp mượt và nhanh hơn
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
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
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route)
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    viewModel = authViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    },
                    onNavigateToLoginGoogle = {
                        // Gọi logic login google ở đây
                    },
                    onRegisterSuccess = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = authViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToForgotPassword = {
                        navController.navigate(Screen.ForgotPassword.route)
                    },
                    onNavigateToLoginGoogle = {
                        // login google
                    },
                    onLoginSuccess = { token ->
                        // Chuyển sang màn hình chính sau khi login
                        navController.navigate(Screen.Calendar.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Calendar.route) {
                ProfileScreen()
            }

            composable(Screen.Stats.route) {
                ProfileScreen()
            }

            composable(Screen.Store.route) {
                ProfileScreen()
            }

            composable(Screen.Profile.route) {
                ProfileScreen()
            }

            composable(Screen.ForgotPassword.route) {
                ForgotPasswordScreen(
                    viewModel = authViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToVerifyOtp = { email ->
                        navController.navigate(Screen.VerifyOtp.route)
                    }
                )
            }


            composable(Screen.VerifyOtp.route) {
                VerifyOtpScreen(
                    viewModel = authViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToResetPassword = { email, token ->
                        navController.navigate(Screen.ResetPassword.route)
                    }
                )
            }

            composable(Screen.ResetPassword.route) {
                ResetPasswordScreen(
                    onNavigateToLogin = {
//                        viewModel = authViewModel,
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
