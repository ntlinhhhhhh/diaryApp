package com.diary.moonpage.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.diary.moonpage.presentation.screens.auth.ForgotPasswordScreen
import com.diary.moonpage.presentation.screens.auth.LandingScreen
import com.diary.moonpage.presentation.screens.auth.LoadingScreen
import com.diary.moonpage.presentation.screens.auth.LoginScreen
import com.diary.moonpage.presentation.screens.auth.RegisterScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "loading") {

        composable("loading") {
            LoadingScreen(
                onFinished = {
                    navController.navigate("landing") {
                        popUpTo("loading") { inclusive = true }
                    }
                }
            )
        }

        composable("landing") {
            LandingScreen(
                onNavigateToLogin = {
                    navController.navigate("login")
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onRegisterSuccess = {
                }
            )
        }

        composable("login") {
            LoginScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToRegister = {
                    navController.navigate("register") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToForgotPassword = {
                    navController.navigate("forgot password") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onLoginSuccess = {
                }
            )
        }

        composable("forgot password") {
            ForgotPasswordScreen (
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("forgot password") { inclusive = true }
                    }
                },
                onNavigateToReset = {
                    navController.navigate("reset password") {
                        popUpTo("forgot password") { inclusive = true }
                    }
                }
            )
        }
    }
}
