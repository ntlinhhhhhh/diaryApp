package com.diary.moonpage.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.diary.moonpage.presentation.screens.auth.AuthViewModel
import com.diary.moonpage.presentation.screens.calendar.CalendarRoute
import com.diary.moonpage.presentation.screens.calendar.DailyLogRoute
import com.diary.moonpage.presentation.screens.calendar.FilterScreen
import com.diary.moonpage.presentation.screens.moment.momentScreen
import com.diary.moonpage.presentation.screens.profile.*
import com.diary.moonpage.presentation.screens.store.StoreScreen
import com.diary.moonpage.presentation.screens.store.StoreViewModel
import com.diary.moonpage.presentation.screens.store.ThemeDetailScreen

fun NavGraphBuilder.mainNavGraph(
    navController: NavController,
    authViewModel: AuthViewModel,
    screenWrapper: @Composable (String, @Composable () -> Unit) -> Unit
) {
    navigation(
        startDestination = Screen.Calendar.route,
        route = "main_graph"
    ) {
        composable(Screen.Calendar.route) { backStackEntry ->
            val logSavedMessage = backStackEntry.savedStateHandle.get<String>("log_saved_message")

            screenWrapper(Screen.Calendar.route) {
                CalendarRoute(
                    logSavedMessage = logSavedMessage,
                    onMessageShown = {
                        backStackEntry.savedStateHandle.remove<String>("log_saved_message")
                    },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateToDailyLog = { date ->
                        navController.navigate("daily_log_screen/$date")
                    },
                    onNavigateToThemeCalendar = { navController.navigate(Screen.ThemeCalendar.route) }
                )
            }
        }

        composable(Screen.Filter.route) {
            screenWrapper(Screen.Filter.route) {
                FilterScreen(
                    onDismiss = { navController.popBackStack() },
                    onSeeResults = { navController.popBackStack() }
                )
            }
        }

        composable(Screen.DailyLog.route) { backStackEntry ->
            val dateStr = backStackEntry.arguments?.getString("date") ?: ""
            screenWrapper(Screen.DailyLog.route) {
                DailyLogRoute(
                    dateString = dateStr,
                    onNavigateBack = { navController.popBackStack() },
                    onDone = { msg ->
                        navController.previousBackStackEntry?.savedStateHandle?.set("log_saved_message", msg)
                        navController.popBackStack() 
                    }
                )
            }
        }

        composable(Screen.Stats.route) {
            screenWrapper(Screen.Stats.route) {
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

        momentScreen(navController, screenWrapper)

        composable(Screen.Store.route) { backStackEntry ->
            val storeViewModel: StoreViewModel = hiltViewModel(backStackEntry)
            screenWrapper(Screen.Store.route) {
                StoreScreen(
                    viewModel = storeViewModel,
                    onNavigateToDetail = { navController.navigate(Screen.ThemeDetail.route) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        composable(Screen.ThemeDetail.route) {
            val storeEntry = remember(it) {
                navController.getBackStackEntry(Screen.Store.route)
            }
            val storeViewModel: StoreViewModel = hiltViewModel(storeEntry)

            screenWrapper(Screen.ThemeDetail.route) {
                ThemeDetailScreen(
                    viewModel = storeViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        composable(Screen.Profile.route) {
            screenWrapper(Screen.Profile.route) {
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
            screenWrapper(Screen.Account.route) {
                AccountScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onLogoutClick = {
                        authViewModel.logout()
                        navController.navigate("auth_graph") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToChangeAvatar = { navController.navigate(Screen.Photos.route) }
                )
            }
        }

        composable(Screen.Photos.route) {
            screenWrapper(Screen.Photos.route) {
                ChangeProfilePictureScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onApply = { navController.popBackStack() }
                )
            }
        }

        composable(Screen.Gallery.route) {
            screenWrapper(Screen.Gallery.route) {
                GalleryScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        composable(Screen.ThemeCalendar.route) {
            screenWrapper(Screen.ThemeCalendar.route) {
                ThemeCalendarScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
