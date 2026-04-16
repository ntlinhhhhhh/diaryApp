package com.diary.moonpage.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.diary.moonpage.presentation.components.core.navigation.MoonBottomNavBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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
            modifier = Modifier.padding(paddingValues)
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
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    },
                    onNavigateToLoginGoogle = {
                        // todo: login with google
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
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToForgotPassword = {
                        navController.navigate(Screen.ForgotPassword.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToLoginGoogle = {
                        // todo: login with google
                    },
                    onLoginSuccess = { token ->
                        // todo: saved token to SharedPreferences

                        navController.navigate(Screen.Calendar.route) {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable(Screen.Calendar.route) {
                ProfileScreen()
            }

            composable(Screen.ForgotPassword.route) {
                ForgotPasswordScreen(
                    onNavigateToReset = {
                        navController.navigate(Screen.ResetPassword.route) {
                            popUpTo(Screen.ForgotPassword.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.ForgotPassword.route) { inclusive = true }
                        }
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.ResetPassword.route) {
                ResetPasswordScreen(
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.ResetPassword.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.VerifyOtp.route) {
                VerifyOtpScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onResendClick = {
                        // viewModel.resendOtp()
                    },
                    onVerifySubmit = { code ->
                        // todo: viewModel.verifyOtp(code)
                        // todo: handle response
                    }
                )
            }
        }
    }
}