package com.diary.moonpage.presentation.navigation

sealed class Screen (val route: String) {
    object Loading: Screen("loading_Screen")
    object Landing: Screen("landing_Screen")
    object Login: Screen("login_Screen")
    object Register: Screen("register_Screen")
    object ForgotPassword: Screen("forgotPassword_Screen")
    object ResetPassword: Screen("resetPassword_Screen")

    object Home: Screen("home_Screen")
}