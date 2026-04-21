package com.diary.moonpage.presentation.screens.profile

import android.content.res.Configuration
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Cake
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material.icons.rounded.Wc
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diary.moonpage.R
import com.diary.moonpage.presentation.components.profile.*
import com.diary.moonpage.presentation.theme.MoonPageTheme
import kotlinx.coroutines.launch

/**
 * BottomSheet type management
 */
enum class BottomSheetType { NONE, BIRTHDAY, GENDER, USERNAME }

/**
 * Stateful Screen for Account
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    onNavigateBack: () -> Unit,
    onLogoutClick: () -> Unit,
    onNavigateToChangeAvatar: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    
    val coroutineScope = rememberCoroutineScope()
    var currentBottomSheet by remember { mutableStateOf(BottomSheetType.NONE) }

    var username by remember { mutableStateOf("🥑") }
    var gender by remember { mutableStateOf("Female") }
    var birthday by remember { mutableStateOf("04/06/2005") }

    val hideBottomSheet = {
        coroutineScope.launch {
            sheetState.hide()
        }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                currentBottomSheet = BottomSheetType.NONE
            }
        }
    }

    AccountScreenContent(
        username = username,
        gender = gender,
        birthday = birthday,
        onNavigateBack = onNavigateBack,
        onLogoutClick = onLogoutClick,
        onBirthdayClick = { currentBottomSheet = BottomSheetType.BIRTHDAY },
        onGenderClick = { currentBottomSheet = BottomSheetType.GENDER },
        onAvatarEditClick = onNavigateToChangeAvatar,
        onUsernameEditClick = { currentBottomSheet = BottomSheetType.USERNAME }
    )

    if (currentBottomSheet != BottomSheetType.NONE) {
        ModalBottomSheet(
            onDismissRequest = { currentBottomSheet = BottomSheetType.NONE },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            // Xử lý insets ở mức Sheet để nội dung bên trong trôi mượt khi hiện bàn phím
            contentWindowInsets = { WindowInsets.ime.union(WindowInsets.navigationBars) },
            tonalElevation = 0.dp,
            scrimColor = Color.Black.copy(alpha = 0.32f), // Màu scrim nhạt hơn cho cảm giác nhanh hơn
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .width(36.dp)
                        .height(4.dp)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f), CircleShape)
                )
            }
        ) {
            // Bao bọc bởi Box để tránh nhảy padding khi Sheet đang trượt
            Box(modifier = Modifier.fillMaxWidth()) {
                when (currentBottomSheet) {
                    BottomSheetType.GENDER -> {
                        GenderBottomSheetContent(
                            currentGender = gender,
                            onGenderSelected = { 
                                gender = it
                                hideBottomSheet() 
                            },
                            onClose = { hideBottomSheet() }
                        )
                    }
                    BottomSheetType.BIRTHDAY -> {
                        BirthdayBottomSheetContent(onClose = { hideBottomSheet() })
                    }
                    BottomSheetType.USERNAME -> {
                        UsernameBottomSheetContent(
                            currentUsername = username,
                            onUsernameChange = { 
                                username = it
                                hideBottomSheet()
                            },
                            onClose = { hideBottomSheet() }
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}

/**
 * Stateless Content for Account Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreenContent(
    username: String,
    gender: String,
    birthday: String,
    onNavigateBack: () -> Unit,
    onLogoutClick: () -> Unit,
    onBirthdayClick: () -> Unit,
    onGenderClick: () -> Unit,
    onAvatarEditClick: () -> Unit,
    onUsernameEditClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Account", 
                        fontWeight = FontWeight.Bold, 
                        color = colorScheme.onBackground
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back", 
                            tint = colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            AccountAvatar(onEditClick = onAvatarEditClick)

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(username, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onUsernameEditClick, modifier = Modifier.size(20.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.Edit, 
                        contentDescription = "Edit Username",
                        tint = colorScheme.onBackground.copy(alpha = 0.4f), 
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            AccountInfoRow(
                label = "User ID",
                value = "01KJPADDQZ5DSB2GYGFGX384RF",
                actionText = "Copy",
                icon = Icons.Rounded.Person,
                isColumnValue = true,
                onClick = {}
            )

            AccountInfoRow(
                label = "Birthday",
                value = birthday,
                showArrow = true,
                icon = Icons.Rounded.Cake,
                onClick = onBirthdayClick
            )

            AccountInfoRow(
                label = "Gender",
                value = gender,
                showArrow = true,
                icon = Icons.Rounded.Wc,
                onClick = onGenderClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Login Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            AccountInfoRow(
                label = "My social account",
                value = "duonghoangg241@gmail.com",
                iconRes = R.drawable.ic_google,
                isColumnValue = true,
                onClick = {}
            )

            AccountInfoRow(
                label = "Change social account",
                value = "",
                icon = Icons.Rounded.Sync,
                showArrow = true,
                onClick = {}
            )

            Spacer(modifier = Modifier.height(40.dp))

            TextButton(onClick = onLogoutClick) {
                Text(
                    "Log out", 
                    color = colorScheme.error, 
                    fontWeight = FontWeight.Bold, 
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Preview(showBackground = true, name = "Account Screen Light")
@Composable
fun AccountScreenPreview() {
    MoonPageTheme {
        AccountScreenContent(
            username = "🥑",
            gender = "Female",
            birthday = "04/06/2005",
            onNavigateBack = {},
            onLogoutClick = {},
            onBirthdayClick = {},
            onGenderClick = {},
            onAvatarEditClick = {},
            onUsernameEditClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Account Screen Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AccountScreenDarkPreview() {
    MoonPageTheme {
        AccountScreenContent(
            username = "🥑",
            gender = "Female",
            birthday = "04/06/2005",
            onNavigateBack = {},
            onLogoutClick = {},
            onBirthdayClick = {},
            onGenderClick = {},
            onAvatarEditClick = {},
            onUsernameEditClick = {}
        )
    }
}
