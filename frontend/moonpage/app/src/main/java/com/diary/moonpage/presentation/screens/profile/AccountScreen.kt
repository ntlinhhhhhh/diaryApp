package com.diary.moonpage.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
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
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onLogoutClick: () -> Unit,
    onNavigateToChangeAvatar: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    
    val coroutineScope = rememberCoroutineScope()
    var currentBottomSheet by remember { mutableStateOf(BottomSheetType.NONE) }

    val user = uiState.user

    // Fetch latest profile data when screen is launched
    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    val hideBottomSheet = {
        coroutineScope.launch {
            sheetState.hide()
        }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                currentBottomSheet = BottomSheetType.NONE
            }
        }
    }

    // Listen for events (like showing snackbar)
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when(event) {
                is ProfileUiEvent.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is ProfileUiEvent.UpdateSuccess -> {
                    // Success logic if needed
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            AccountScreenContent(
                username = user?.name ?: "",
                gender = user?.gender ?: "Not specified",
                birthday = user?.birthday ?: "Not specified",
                userIdFull = user?.id ?: "",
                email = user?.email ?: "",
                avatarUrl = user?.avatarUrl,
                onNavigateBack = onNavigateBack,
                onLogoutClick = onLogoutClick,
                onBirthdayClick = { currentBottomSheet = BottomSheetType.BIRTHDAY },
                onGenderClick = { currentBottomSheet = BottomSheetType.GENDER },
                onAvatarEditClick = onNavigateToChangeAvatar,
                onUsernameEditClick = { currentBottomSheet = BottomSheetType.USERNAME }
            )
        }
    }

    if (currentBottomSheet != BottomSheetType.NONE) {
        ModalBottomSheet(
            onDismissRequest = { currentBottomSheet = BottomSheetType.NONE },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            contentWindowInsets = { WindowInsets.ime.union(WindowInsets.navigationBars) },
            tonalElevation = 0.dp,
            scrimColor = Color.Black.copy(alpha = 0.32f),
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
            Box(modifier = Modifier.fillMaxWidth()) {
                when (currentBottomSheet) {
                    BottomSheetType.GENDER -> {
                        GenderBottomSheetContent(
                            currentGender = user?.gender ?: "Other",
                            onGenderSelected = { newGender ->
                                viewModel.updateProfile(
                                    name = user?.name ?: "",
                                    gender = newGender,
                                    birthday = user?.birthday
                                )
                                hideBottomSheet() 
                            },
                            onClose = { hideBottomSheet() }
                        )
                    }
                    BottomSheetType.BIRTHDAY -> {
                        BirthdayBottomSheetContent(
                            currentBirthday = user?.birthday ?: "01/01/2000",
                            onBirthdaySelected = { newBirthday ->
                                viewModel.updateProfile(
                                    name = user?.name ?: "",
                                    gender = user?.gender,
                                    birthday = newBirthday
                                )
                                hideBottomSheet()
                            },
                            onClose = { hideBottomSheet() }
                        )
                    }
                    BottomSheetType.USERNAME -> {
                        UsernameBottomSheetContent(
                            currentUsername = user?.name ?: "",
                            onUsernameChange = { newName ->
                                viewModel.updateProfile(
                                    name = newName,
                                    gender = user?.gender,
                                    birthday = user?.birthday
                                )
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
    userIdFull: String,
    email: String,
    avatarUrl: String?,
    onNavigateBack: () -> Unit,
    onLogoutClick: () -> Unit,
    onBirthdayClick: () -> Unit,
    onGenderClick: () -> Unit,
    onAvatarEditClick: () -> Unit,
    onUsernameEditClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val isUsernameEmpty = username.trim().isEmpty()

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

            AccountAvatar(
                onEditClick = onAvatarEditClick,
                avatarUrl = avatarUrl
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onUsernameEditClick() }
            ) {
                Text(
                    text = if (isUsernameEmpty) "Set Username" else username,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isUsernameEmpty) colorScheme.onBackground.copy(alpha = 0.5f) else colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Rounded.Edit, 
                    contentDescription = "Edit Username",
                    tint = colorScheme.onBackground.copy(alpha = 0.4f), 
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            AccountInfoRow(
                label = "User ID",
                value = userIdFull,
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
                value = email,
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
            userIdFull = "01KJPADDQZ5DSB2GYGFGX384RF",
            email = "demo@gmail.com",
            avatarUrl = null,
            onNavigateBack = {},
            onLogoutClick = {},
            onBirthdayClick = {},
            onGenderClick = {},
            onAvatarEditClick = {},
            onUsernameEditClick = {}
        )
    }
}
