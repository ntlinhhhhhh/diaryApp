package com.diary.moonpage.presentation.components.bars

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diary.moonpage.presentation.theme.*

@Composable
fun MoonBottomNavBar(
    selectedRoute: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    // Nền của thanh Nav: Trắng ở ban ngày, Xám nổi ở ban đêm
    val navBgColor = MaterialTheme.colorScheme.surface

    // Nền của nút Camera ở giữa: Màu nền ngoài cùng để tạo cảm giác "khoét" xuống
    val cameraBgColor = MaterialTheme.colorScheme.background

    // Màu cho Tab đang chọn (Ví dụ: Màu xanh dương đậm)
    val activeColor = MoonAccentBlue

    // Màu cho Tab không chọn (Mờ đi 40%)
    val inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            // Có thể thêm padding ngang nếu bạn muốn nó "lơ lửng" (Floating Nav Bar)
            // .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp), spotColor = Color.Black.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        color = navBgColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 1. Nút Home
            NavBarItem(
                icon = Icons.Outlined.Home,
                label = "Home",
                isSelected = selectedRoute == "home",
                activeColor = activeColor,
                inactiveColor = inactiveColor,
                onClick = { onItemSelected("home") }
            )

            // 2. Nút Thống kê (Stats)
            NavBarItem(
                icon = Icons.Outlined.BarChart,
                label = "Stats",
                isSelected = selectedRoute == "stats",
                activeColor = activeColor,
                inactiveColor = inactiveColor,
                onClick = { onItemSelected("stats") }
            )

            // 3. Nút Camera (Nút Trung Tâm Đặc Biệt)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(cameraBgColor)
                    .clickable { onItemSelected("camera") },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CameraAlt,
                    contentDescription = "Camera",
                    tint = if (selectedRoute == "camera") activeColor else inactiveColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            // 4. Nút Cửa hàng (Store)
            NavBarItem(
                icon = Icons.Outlined.Storefront,
                label = "Store",
                isSelected = selectedRoute == "store",
                activeColor = activeColor,
                inactiveColor = inactiveColor,
                onClick = { onItemSelected("store") }
            )

            // 5. Nút Profile
            NavBarItem(
                icon = Icons.Outlined.Person,
                label = "Profile",
                isSelected = selectedRoute == "profile",
                activeColor = activeColor,
                inactiveColor = inactiveColor,
                onClick = { onItemSelected("profile") }
            )
        }
    }
}

// Component con xử lý việc: Chỉ hiện chữ khi được chọn
@Composable
private fun NavBarItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    onClick: () -> Unit
) {
    val color = if (isSelected) activeColor else inactiveColor
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Tắt hiệu ứng sóng gợn (Ripple) để giống thiết kế
                onClick = onClick
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(28.dp)
        )

        // Nếu đang được chọn thì mới hiển thị chữ bên dưới
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
        }
    }
}

// Preview để bạn xem thử ngay trên Android Studio
@Preview(showBackground = true, backgroundColor = 0xFFFFFBF4)
@Composable
fun BottomNavPreviewLight() {
    MoonPageTheme {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.BottomCenter) {
            MoonBottomNavBar(selectedRoute = "profile", onItemSelected = {})
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF333746)
@Composable
fun BottomNavPreviewDark() {
    MoonPageTheme(darkTheme = true) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.BottomCenter) {
            MoonBottomNavBar(selectedRoute = "profile", onItemSelected = {})
        }
    }
}