package com.diary.moonpage.presentation.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomSheetHeader(title: String, onClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 16.dp, start = 24.dp, end = 24.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.Center)
        )
        IconButton(
            onClick = onClose,
            modifier = Modifier.align(Alignment.CenterEnd).size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close, 
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun GenderBottomSheetContent(
    currentGender: String,
    onGenderSelected: (String) -> Unit,
    onClose: () -> Unit
) {
    val options = listOf("Female", "Male", "Other")
    var selectedOption by remember { mutableStateOf(currentGender) }
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp)
    ) {
        BottomSheetHeader(title = "Gender", onClose = onClose)

        options.forEach { text ->
            val isSelected = text == selectedOption
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent)
                    .clickable { selectedOption = text }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = text,
                    color = if (isSelected) colorScheme.onSurface else colorScheme.onSurface.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
                
                // Custom Radio Button
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(
                            color = if (isSelected) colorScheme.primary else colorScheme.onSurface.copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                        .padding(2.dp)
                        .background(
                            color = colorScheme.surface,
                            shape = CircleShape
                        )
                        .padding(if (isSelected) 3.dp else 0.dp)
                        .background(
                            color = if (isSelected) colorScheme.primary else Color.Transparent,
                            shape = CircleShape
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onGenderSelected(selectedOption)
                onClose()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary
            ),
            shape = RoundedCornerShape(20.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Text("Done", color = colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun BirthdayBottomSheetContent(onClose: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BottomSheetHeader(title = "Birthday", onClose = onClose)

        // Simulated Wheel Picker
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            WheelColumn(listOf("March", "April", "May", "June", "July"), "April")
            WheelColumn(listOf("4", "5", "6", "7", "8"), "6")
            WheelColumn(listOf("2004", "2005", "2006", "2007", "2008"), "2005")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onClose,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary
            ),
            shape = RoundedCornerShape(20.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Text("Done", color = colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun WheelColumn(items: List<String>, selected: String) {
    val colorScheme = MaterialTheme.colorScheme
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        items.forEach { item ->
            val isSelected = item == selected
            Text(
                text = item,
                style = if (isSelected) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
                color = if (isSelected) colorScheme.onSurface else colorScheme.onSurface.copy(alpha = 0.2f),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}
