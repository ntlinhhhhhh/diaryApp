package com.diary.moonpage.presentation.screens.auth

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.diary.moonpage.R
import com.diary.moonpage.presentation.theme.MoonPageTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

@Composable
fun LoadingScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onFinished: (isLoggedIn: Boolean, needsOnboarding: Boolean) -> Unit
) {
    LaunchedEffect(Unit) {
        val token = viewModel.tokenFlow.first()
        val isLoggedIn = !token.isNullOrBlank()

        delay(1500)

        if (isLoggedIn) {
            val onboardingDone = viewModel.checkOnboardingForCurrentUser()
            onFinished(true, !onboardingDone)
        } else {
            onFinished(false, false)
        }
    }

    val isDarkTheme = isSystemInDarkTheme()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.background
            )
            .then(
                if (isDarkTheme) {
                    Modifier.background(
                        brush = Brush.radialGradient(
                            0.0f to Color.White.copy(alpha = 0.4f),
                            0.8f to Color.White.copy(alpha = 0f),
                            radius = 1300f
                        )
                    )
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )

            Text(
                text = "Moonpage",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Loading your feelings...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
fun LoadingScreenLightPreview() {
    MoonPageTheme {
        LoadingScreen(onFinished = { _, _ -> })
    }
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun LoadingScreenDarkPreview() {
    MoonPageTheme {
        LoadingScreen(onFinished = { _, _ -> })
    }
}
