package com.diary.moonpage.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.diary.moonpage.presentation.screens.auth.*

fun NavGraphBuilder.authNavGraph(
    navController: NavController,
    authViewModel: AuthViewModel,
    screenWrapper: @Composable (String, @Composable () -> Unit) -> Unit
) {
    navigation(
        startDestination = Screen.Landing.route,
        route = "auth_graph"
    ) {
        composable(Screen.Landing.route) {
            screenWrapper(Screen.Landing.route) {
                LandingScreen(
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) }
                )
            }
        }

        composable(Screen.Login.route) {
            screenWrapper(Screen.Login.route) {
                LoginScreen(
                    viewModel = authViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                    onNavigateToLoginGoogle = { /* TODO */ },
                    onLoginSuccess = { _ ->
                        navController.navigate("main_graph") {
                            popUpTo("auth_graph") { inclusive = true }
                        }
                    }
                )
            }
        }

        composable(Screen.Register.route) {
            screenWrapper(Screen.Register.route) {
                RegisterScreen(
                    viewModel = authViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    },
                    onNavigateToLoginGoogle = { /* TODO */ },
                    onRegisterSuccess = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    },
                    onLoginSuccess = { _ ->
                        navController.navigate("main_graph") {
                            popUpTo("auth_graph") { inclusive = true }
                        }
                    }
                )
            }
        }

        composable(Screen.ForgotPassword.route) {
            screenWrapper(Screen.ForgotPassword.route) {
                ForgotPasswordScreen(
                    viewModel = authViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToVerifyOtp = { email ->
                        navController.navigate(Screen.VerifyOtp.route)
                    }
                )
            }
        }

        composable(Screen.VerifyOtp.route) {
            screenWrapper(Screen.VerifyOtp.route) {
                VerifyOtpScreen(
                    viewModel = authViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToResetPassword = { _, _ ->
                        navController.navigate(Screen.ResetPassword.route)
                    }
                )
            }
        }

        composable(Screen.ResetPassword.route) {
            screenWrapper(Screen.ResetPassword.route) {
                ResetPasswordScreen(
                    viewModel = authViewModel,
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
