package com.diary.moonpage.presentation.navigation

sealed class Screen (val route: String) {
    object Loading: Screen("loading_Screen")
    object Landing: Screen("landing_Screen")
    object Login: Screen("login_Screen")
    object Register: Screen("register_Screen")
    object ForgotPassword: Screen("forgotPassword_Screen")
    object ResetPassword: Screen("resetPassword_Screen")
    object VerifyOtp: Screen("verifyOtp_Screen")

    object Calendar: Screen("calendar_Screen")
    object Stats: Screen("stats_Screen")
    object Store: Screen("store_Screen")
    object Profile: Screen("Profile_Screen")
}
