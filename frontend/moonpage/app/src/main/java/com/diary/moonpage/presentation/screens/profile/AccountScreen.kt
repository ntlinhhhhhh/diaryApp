package com.diary.moonpage.presentation.screens.profile

import android.content.res.Configuration
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
enum class BottomSheetType { NONE, BIRTHDAY, GENDER }

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
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var currentBottomSheet by remember { mutableStateOf(BottomSheetType.NONE) }

    // Dummy state (Should be in ViewModel)
    var gender by remember { mutableStateOf("Female") }
    var birthday by remember { mutableStateOf("04/06/2005") }

    val hideBottomSheet = {
        coroutineScope.launch {
            sheetState.hide()
            currentBottomSheet = BottomSheetType.NONE
        }
    }

    AccountScreenContent(
        gender = gender,
        birthday = birthday,
        onNavigateBack = onNavigateBack,
        onLogoutClick = onLogoutClick,
        onBirthdayClick = { currentBottomSheet = BottomSheetType.BIRTHDAY },
        onGenderClick = { currentBottomSheet = BottomSheetType.GENDER },
        onAvatarEditClick = onNavigateToChangeAvatar,
        onBioEditClick = { /* Handle bio/username edit */ }
    )

    if (currentBottomSheet != BottomSheetType.NONE) {
        ModalBottomSheet(
            onDismissRequest = { currentBottomSheet = BottomSheetType.NONE },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f), CircleShape)
                )
            }
        ) {
            when (currentBottomSheet) {
                BottomSheetType.GENDER -> {
                    GenderBottomSheetContent(
                        currentGender = gender,
                        onGenderSelected = { gender = it },
                        onClose = { hideBottomSheet() }
                    )
                }
                BottomSheetType.BIRTHDAY -> {
                    BirthdayBottomSheetContent(onClose = { hideBottomSheet() })
                }
                else -> {}
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
    gender: String,
    birthday: String,
    onNavigateBack: () -> Unit,
    onLogoutClick: () -> Unit,
    onBirthdayClick: () -> Unit,
    onGenderClick: () -> Unit,
    onAvatarEditClick: () -> Unit,
    onBioEditClick: () -> Unit
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

            // Avatar Section (Extracted to component)
            AccountAvatar(onEditClick = onAvatarEditClick)

            Spacer(modifier = Modifier.height(16.dp))

            // Bio / Username Section
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🥑", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onBioEditClick, modifier = Modifier.size(20.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.Edit, 
                        contentDescription = "Edit Bio",
                        tint = colorScheme.onBackground.copy(alpha = 0.4f), 
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Information Rows
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

            // Login Info Section
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
            gender = "Female",
            birthday = "04/06/2005",
            onNavigateBack = {},
            onLogoutClick = {},
            onBirthdayClick = {},
            onGenderClick = {},
            onAvatarEditClick = {},
            onBioEditClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Account Screen Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AccountScreenDarkPreview() {
    MoonPageTheme {
        AccountScreenContent(
            gender = "Female",
            birthday = "04/06/2005",
            onNavigateBack = {},
            onLogoutClick = {},
            onBirthdayClick = {},
            onGenderClick = {},
            onAvatarEditClick = {},
            onBioEditClick = {}
        )
    }
}
