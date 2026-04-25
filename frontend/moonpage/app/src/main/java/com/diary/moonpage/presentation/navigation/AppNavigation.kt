package com.diary.moonpage.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.diary.moonpage.presentation.components.core.navigation.MoonBottomNavBar
import com.diary.moonpage.presentation.screens.auth.*
import com.diary.moonpage.presentation.screens.calendar.CalendarScreen
import com.diary.moonpage.presentation.screens.calendar.DailyLogScreen
import com.diary.moonpage.presentation.screens.calendar.FilterScreen
import com.diary.moonpage.presentation.screens.moment.MomentCameraScreen
import com.diary.moonpage.presentation.screens.profile.*
import com.diary.moonpage.presentation.screens.store.StoreScreen
import com.diary.moonpage.presentation.screens.store.StoreViewModel
import com.diary.moonpage.presentation.screens.store.ThemeDetailScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = navBackStackEntry?.destination?.route

    val authViewModel: AuthViewModel = hiltViewModel()

    val mainAppRoutes = listOf(
        Screen.Calendar.route,
        Screen.Stats.route,
        Screen.Camera.route,
        Screen.Store.route,
        Screen.Profile.route
    )
    val showBottomBar = currentRoute in mainAppRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                MoonBottomNavBar(
                    selectedRoute = currentRoute ?: Screen.Calendar.route,
                    onItemSelected = { route ->
                        if (currentRoute != route) {
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        @Composable
        fun ScreenWrapper(route: String, content: @Composable () -> Unit) {
            val isMainRoute = route in mainAppRoutes
            Box(
                modifier = Modifier.padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = if (isMainRoute) paddingValues.calculateBottomPadding() else 0.dp,
                    start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                    end = paddingValues.calculateEndPadding(LayoutDirection.Ltr)
                )
            ) {
                content()
            }
        }

        NavHost(
            navController = navController,
            startDestination = Screen.Loading.route,
            modifier = Modifier,
            enterTransition = {
                fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300))
            }
        ) {
            composable(Screen.Loading.route) {
                ScreenWrapper(Screen.Loading.route) {
                    LoadingScreen(
                        onFinished = { isLoggedIn ->
                            val nextDestination = if (isLoggedIn) Screen.Calendar.route else Screen.Landing.route
                            navController.navigate(nextDestination) {
                                popUpTo(Screen.Loading.route) { inclusive = true }
                            }
                        }
                    )
                }
            }

            composable(Screen.Landing.route) {
                ScreenWrapper(Screen.Landing.route) {
                    LandingScreen(
                        onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                        onNavigateToRegister = { navController.navigate(Screen.Register.route) }
                    )
                }
            }

            composable(Screen.Login.route) {
                ScreenWrapper(Screen.Login.route) {
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
                            navController.navigate(Screen.Calendar.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }

            composable(Screen.Register.route) {
                ScreenWrapper(Screen.Register.route) {
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
                            navController.navigate(Screen.Calendar.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }

            composable(Screen.ForgotPassword.route) {
                ScreenWrapper(Screen.ForgotPassword.route) {
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
                ScreenWrapper(Screen.VerifyOtp.route) {
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
                ScreenWrapper(Screen.ResetPassword.route) {
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

            composable(Screen.Calendar.route) {
                ScreenWrapper(Screen.Calendar.route) {
                    CalendarScreen(
                        onNavigateToFilter = { navController.navigate(Screen.Filter.route) },
                        onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                        onNavigateToDailyLog = { dateStr -> navController.navigate("daily_log_screen/$dateStr") },
                        onNavigateToThemeCalendar = { navController.navigate(Screen.ThemeCalendar.route) }
                    )
                }
            }

            composable(Screen.Filter.route) {
                ScreenWrapper(Screen.Filter.route) {
                    FilterScreen(
                        onDismiss = { navController.popBackStack() },
                        onSeeResults = { navController.popBackStack() }
                    )
                }
            }

            composable(Screen.DailyLog.route) { backStackEntry ->
                val dateStr = backStackEntry.arguments?.getString("date") ?: ""
                ScreenWrapper(Screen.DailyLog.route) {
                    DailyLogScreen(
                        dateString = dateStr,
                        onNavigateBack = { navController.popBackStack() },
                        onDone = { navController.popBackStack() }
                    )
                }
            }

            composable(Screen.Stats.route) {
                ScreenWrapper(Screen.Stats.route) {
                    ProfileScreen(
                        onNavigateToAccount = { navController.navigate(Screen.Account.route) },
                        onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                        onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                        onNavigateToPhotos = { navController.navigate(Screen.Gallery.route) },
                        onNavigateToThemeCalendar = { navController.navigate(Screen.ThemeCalendar.route) },
                        onNavigateToWidgets = { navController.navigate(Screen.Widgets.route) },
                        onNavigateToInviteFriend = { navController.navigate(Screen.InviteFriend.route) }
                    )
                }
            }

            composable(Screen.Camera.route) {
                ScreenWrapper(Screen.Camera.route) {
                    MomentCameraScreen(
                        onNavigateToGallery = { navController.navigate(Screen.Gallery.route) },
                        onNavigateToHistory = { /* TODO */ }
                    )
                }
            }

            composable(Screen.Store.route) { backStackEntry ->
                // Scope StoreViewModel to this route to share it with ThemeDetail
                val storeViewModel: StoreViewModel = hiltViewModel(backStackEntry)
                ScreenWrapper(Screen.Store.route) {
                    StoreScreen(
                        viewModel = storeViewModel,
                        onNavigateToDetail = { navController.navigate(Screen.ThemeDetail.route) },
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }

            composable(Screen.ThemeDetail.route) {
                // Get the same StoreViewModel instance by using the Store backstack entry
                val storeEntry = remember(it) {
                    navController.getBackStackEntry(Screen.Store.route)
                }
                val storeViewModel: StoreViewModel = hiltViewModel(storeEntry)

                ScreenWrapper(Screen.ThemeDetail.route) {
                    ThemeDetailScreen(
                        viewModel = storeViewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }

            composable(Screen.Profile.route) {
                ScreenWrapper(Screen.Profile.route) {
                    ProfileScreen(
                        onNavigateToAccount = { navController.navigate(Screen.Account.route) },
                        onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                        onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                        onNavigateToPhotos = { navController.navigate(Screen.Gallery.route) },
                        onNavigateToThemeCalendar = { navController.navigate(Screen.ThemeCalendar.route) },
                        onNavigateToWidgets = { navController.navigate(Screen.Widgets.route) },
                        onNavigateToInviteFriend = { navController.navigate(Screen.InviteFriend.route) }
                    )
                }
            }

            composable(Screen.Account.route) {
                ScreenWrapper(Screen.Account.route) {
                    AccountScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onLogoutClick = {
                            authViewModel.logout()
                            navController.navigate(Screen.Landing.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onNavigateToChangeAvatar = { navController.navigate(Screen.Photos.route) }
                    )
                }
            }

            composable(Screen.Photos.route) {
                ScreenWrapper(Screen.Photos.route) {
                    ChangeProfilePictureScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onApply = { navController.popBackStack() }
                    )
                }
            }

            composable(Screen.Gallery.route) {
                ScreenWrapper(Screen.Gallery.route) {
                    GalleryScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }

            composable(Screen.ThemeCalendar.route) {
                ScreenWrapper(Screen.ThemeCalendar.route) {
                    ThemeCalendarScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
