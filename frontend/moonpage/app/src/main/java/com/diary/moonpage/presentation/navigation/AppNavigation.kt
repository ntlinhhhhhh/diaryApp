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
import com.diary.moonpage.presentation.screens.auth.ResetPasswordScreen
import com.diary.moonpage.presentation.screens.auth.VerifyOtpScreen
import com.diary.moonpage.presentation.screens.profile.ProfileScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Loading.route
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

                    navController.navigate(Screen.Home.route) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            ProfileScreen()
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.ForgotPassword.route) { inclusive = true }
                    }
                },
                onNavigateToReset = {
                    navController.navigate(Screen.ResetPassword.route) {
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
