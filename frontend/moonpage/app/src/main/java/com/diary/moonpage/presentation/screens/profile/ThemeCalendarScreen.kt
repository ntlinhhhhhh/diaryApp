package com.diary.moonpage.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diary.moonpage.presentation.theme.MoonPageTheme

// ---- Data model for theme packs ----

data class BeanThemePack(
    val id: String,
    val name: String,
    val description: String,
    val accentColor: Color,
    val secondaryColor: Color,
    val icon: ImageVector,
    val isLocked: Boolean = false,
    val previewMoods: List<Color> = emptyList()
)

private val beanThemes = listOf(
    BeanThemePack(
        id = "basic",
        name = "Basic Bean",
        description = "The classic round bean — simple and expressive.",
        accentColor = Color(0xFF4CAF50),
        secondaryColor = Color(0xFFE8F5E9),
        icon = Icons.Rounded.Circle,
        previewMoods = listOf(
            Color(0xFFFFEB3B), Color(0xFFAED581), Color(0xFF66BB6A),
            Color(0xFF78909C), Color(0xFF546E7A)
        )
    ),
    BeanThemePack(
        id = "heart",
        name = "Heart Beans",
        description = "Spread love with heart-shaped mood beans.",
        accentColor = Color(0xFFE91E63),
        secondaryColor = Color(0xFFFCE4EC),
        icon = Icons.Rounded.Favorite,
        isLocked = false,
        previewMoods = listOf(
            Color(0xFFF48FB1), Color(0xFFEC407A), Color(0xFFE91E63),
            Color(0xFFAD1457), Color(0xFF880E4F)
        )
    ),
    BeanThemePack(
        id = "puppy",
        name = "Puppy Bean",
        description = "Adorable puppy faces for every mood of the day.",
        accentColor = Color(0xFFFF9800),
        secondaryColor = Color(0xFFFFF3E0),
        icon = Icons.Rounded.Pets,
        isLocked = true,
        previewMoods = listOf(
            Color(0xFFFFCC80), Color(0xFFFFB74D), Color(0xFFFF9800),
            Color(0xFFF57C00), Color(0xFFE65100)
        )
    ),
    BeanThemePack(
        id = "matcha",
        name = "Daily Matcha Set",
        description = "Earthy green tones for your mindful journaling ritual.",
        accentColor = Color(0xFF8BC34A),
        secondaryColor = Color(0xFFF1F8E9),
        icon = Icons.Rounded.SportsBar,
        isLocked = true,
        previewMoods = listOf(
            Color(0xFFDCEDC8), Color(0xFFAED581), Color(0xFF8BC34A),
            Color(0xFF558B2F), Color(0xFF33691E)
        )
    )
)

// ---- Screen ----

@Composable
fun ThemeCalendarScreen(
    onNavigateBack: () -> Unit
) {
    var selectedThemeId by remember { mutableStateOf("basic") }

    ThemePickerContent(
        themes = beanThemes,
        selectedThemeId = selectedThemeId,
        onThemeSelected = { selectedThemeId = it },
        onApply = {
            // TODO: persist theme choice via ViewModel / DataStore
            onNavigateBack()
        },
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemePickerContent(
    themes: List<BeanThemePack>,
    selectedThemeId: String,
    onThemeSelected: (String) -> Unit,
    onApply: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val selectedTheme = themes.find { it.id == selectedThemeId } ?: themes.first()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Bean Themes",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                shadowElevation = 12.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Currently selected theme preview row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(selectedTheme.secondaryColor)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(selectedTheme.accentColor.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = selectedTheme.icon,
                                contentDescription = null,
                                tint = selectedTheme.accentColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = selectedTheme.name,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Selected",
                                color = selectedTheme.accentColor,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onApply,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = selectedTheme.accentColor
                        )
                    ) {
                        Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Apply Theme",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Text(
                    text = "Choose your bean style",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            items(themes) { theme ->
                BeanThemeCard(
                    theme = theme,
                    isSelected = theme.id == selectedThemeId,
                    onClick = {
                        if (!theme.isLocked) onThemeSelected(theme.id)
                    }
                )
            }
        }
    }
}

@Composable
fun BeanThemeCard(
    theme: BeanThemePack,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) theme.accentColor else Color.Transparent
    val borderWidth = if (isSelected) 2.dp else 0.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(borderWidth, borderColor, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                theme.secondaryColor
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Theme icon in a colored circle
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(theme.accentColor.copy(alpha = 0.3f), theme.accentColor.copy(alpha = 0.1f))
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = theme.icon,
                        contentDescription = null,
                        tint = theme.accentColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = theme.name,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (theme.isLocked) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Rounded.Lock,
                                contentDescription = "Locked",
                                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text = theme.description,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp
                    )
                }

                // Selected indicator
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(theme.accentColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mood color preview row (5 beans)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val moodLabels = listOf("😄", "🙂", "😐", "😔", "😴")
                theme.previewMoods.forEachIndexed { index, color ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .then(
                                    if (isSelected)
                                        Modifier.border(1.5.dp, theme.accentColor.copy(alpha = 0.4f), CircleShape)
                                    else Modifier
                                )
                                .background(color, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = moodLabels.getOrElse(index) { "•" },
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            if (theme.isLocked) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(theme.accentColor.copy(alpha = 0.1f))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Rounded.Lock,
                        contentDescription = null,
                        tint = theme.accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Visit the Store to unlock",
                        color = theme.accentColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ThemeCalendarPreview() {
    MoonPageTheme {
        ThemeCalendarScreen { }
    }
}
