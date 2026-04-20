package com.diary.moonpage.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.diary.moonpage.presentation.components.core.navigation.MoonBottomNavBar
import com.diary.moonpage.presentation.screens.auth.*
import com.diary.moonpage.presentation.screens.profile.AccountScreen
import com.diary.moonpage.presentation.screens.profile.ChangeProfilePictureScreen
import com.diary.moonpage.presentation.screens.profile.ProfileScreen
import com.diary.moonpage.presentation.screens.profile.ThemeCalendarScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val authViewModel: AuthViewModel = hiltViewModel()

    val mainAppRoutes = listOf(
        Screen.Calendar.route,
        Screen.Stats.route,
        Screen.Store.route,
        Screen.Profile.route
    )
    val showBottomBar = currentRoute in mainAppRoutes

    // Spring spec cho hiệu ứng vật lý
    val springSpec = spring<IntOffset>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )
    
    val fadeSpringSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessLow
    )

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
        NavHost(
            navController = navController,
            startDestination = Screen.Loading.route,
            modifier = Modifier.padding(paddingValues),
            // Mặc định cho toàn bộ app: Slide + Fade với Spring
            enterTransition = {
                if (initialState.destination.route in mainAppRoutes && targetState.destination.route in mainAppRoutes) {
                    // Hiệu ứng Fade Through cho Bottom Navigation
                    fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.95f, animationSpec = tween(300))
                } else {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = springSpec
                    ) + fadeIn(animationSpec = fadeSpringSpec)
                }
            },
            exitTransition = {
                if (initialState.destination.route in mainAppRoutes && targetState.destination.route in mainAppRoutes) {
                    fadeOut(animationSpec = tween(200)) + scaleOut(targetScale = 0.95f, animationSpec = tween(200))
                } else {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = springSpec
                    ) + fadeOut(animationSpec = fadeSpringSpec)
                }
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = springSpec
                ) + fadeIn(animationSpec = fadeSpringSpec)
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = springSpec
                ) + fadeOut(animationSpec = fadeSpringSpec)
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
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) }
                )
            }

            composable(Screen.Login.route) {
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
                    onLoginSuccess = {
                        navController.navigate(Screen.Calendar.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Register.route) {
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
                    }
                )
            }

            composable(Screen.ForgotPassword.route) {
                ForgotPasswordScreen(
                    viewModel = authViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToReset = { navController.navigate(Screen.VerifyOtp.route) }
                )
            }

            composable(Screen.VerifyOtp.route) {
                VerifyOtpScreen(
                    viewModel = authViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToResetPassword = { _, _ ->
                        navController.navigate(Screen.ResetPassword.route)
                    }
                )
            }

            composable(Screen.ResetPassword.route) {
                ResetPasswordScreen(
                    viewModel = authViewModel,
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Calendar.route) {
                ProfileScreen(
                    onNavigateToAccount = { navController.navigate(Screen.Account.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                    onNavigateToPhotos = { navController.navigate(Screen.Photos.route) },
                    onNavigateToThemeCalendar = { navController.navigate(Screen.ThemeCalendar.route) },
                    onNavigateToWidgets = { navController.navigate(Screen.Widgets.route) },
                    onNavigateToInviteFriend = { navController.navigate(Screen.InviteFriend.route) }
                ) 
            }

            composable(Screen.Stats.route) {
                ProfileScreen(
                    onNavigateToAccount = { navController.navigate(Screen.Account.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                    onNavigateToPhotos = { navController.navigate(Screen.Photos.route) },
                    onNavigateToThemeCalendar = { navController.navigate(Screen.ThemeCalendar.route) },
                    onNavigateToWidgets = { navController.navigate(Screen.Widgets.route) },
                    onNavigateToInviteFriend = { navController.navigate(Screen.InviteFriend.route) }
                )
            }

            composable(Screen.Store.route) {
                ProfileScreen(
                    onNavigateToAccount = { navController.navigate(Screen.Account.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                    onNavigateToPhotos = { navController.navigate(Screen.Photos.route) },
                    onNavigateToThemeCalendar = { navController.navigate(Screen.ThemeCalendar.route) },
                    onNavigateToWidgets = { navController.navigate(Screen.Widgets.route) },
                    onNavigateToInviteFriend = { navController.navigate(Screen.InviteFriend.route) }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToAccount = { navController.navigate(Screen.Account.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                    onNavigateToPhotos = { navController.navigate(Screen.Photos.route) },
                    onNavigateToThemeCalendar = { navController.navigate(Screen.ThemeCalendar.route) },
                    onNavigateToWidgets = { navController.navigate(Screen.Widgets.route) },
                    onNavigateToInviteFriend = { navController.navigate(Screen.InviteFriend.route) }
                )
            }

            composable(Screen.Account.route) {
                AccountScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onLogoutClick = {
                        navController.navigate(Screen.Landing.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToChangeAvatar = { navController.navigate(Screen.Photos.route) }
                )
            }
            
            composable(Screen.Photos.route) {
                ChangeProfilePictureScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onApply = { navController.popBackStack() }
                )
            }
            
            composable(Screen.ThemeCalendar.route) {
                ThemeCalendarScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
