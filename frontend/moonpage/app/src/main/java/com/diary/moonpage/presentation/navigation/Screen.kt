package com.diary.moonpage.presentation.navigation

sealed class Screen (val route: String) {
    // Auth Screens
    object Loading: Screen("loading_screen")
    object Landing: Screen("landing_screen")
    object Login: Screen("login_screen")
    object Register: Screen("register_screen")
    object ForgotPassword: Screen("forgot_password_screen")
    object ResetPassword: Screen("reset_password_screen")
    object VerifyOtp: Screen("verify_otp_screen")

    // Main App Screens (Bottom Nav)
    object Calendar: Screen("calendar_screen")
    object Stats: Screen("stats_screen")
    object Camera: Screen("camera_screen")
    object Store: Screen("store_screen")
    object Profile: Screen("profile_screen")

    // Profile Sub-screens
    object Account: Screen("account_screen")
    object Settings: Screen("settings_screen")
    object Notifications: Screen("notifications_screen")
    object Photos: Screen("photos_screen")
    object Gallery: Screen("gallery_screen")
    object ThemeCalendar: Screen("theme_calendar_screen")
    object Widgets: Screen("widgets_screen")
    object InviteFriend: Screen("invite_friend_screen")

    // Calendar Sub-screens
    object Filter: Screen("filter_screen")
    object DailyLog: Screen("daily_log_screen/{date}")

    // Moment Sub-screens
    object MomentDetail: Screen("moment_detail_screen/{momentId}")

    // Store Sub-screens
    object ThemeDetail: Screen("theme_detail_screen")
}
