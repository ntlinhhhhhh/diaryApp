package com.diary.moonpage.presentation.components.profile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import java.util.*

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
fun UsernameBottomSheetContent(
    currentUsername: String,
    onUsernameChange: (String) -> Unit,
    onClose: () -> Unit
) {
    var text by remember { mutableStateOf(currentUsername) }
    val maxLength = 20
    val colorScheme = MaterialTheme.colorScheme
    val isChanged = text != currentUsername && text.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding()
            .padding(bottom = 24.dp)
    ) {
        BottomSheetHeader(title = "Change Username", onClose = onClose)

        OutlinedTextField(
            value = text,
            onValueChange = { if (it.length <= maxLength) text = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorScheme.primary,
                unfocusedBorderColor = colorScheme.onSurface.copy(alpha = 0.1f),
            ),
            singleLine = true
        )

        Text(
            text = "${text.length}/$maxLength",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodySmall,
            color = colorScheme.onSurface.copy(alpha = 0.4f),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (isChanged) {
                    onUsernameChange(text)
                    onClose()
                }
            },
            enabled = isChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary,
                disabledContainerColor = colorScheme.primary.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(20.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Text("Change", color = if (isChanged) colorScheme.onPrimary else colorScheme.onPrimary.copy(alpha = 0.6f), fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
    val isChanged = !selectedOption.equals(currentGender, ignoreCase = true)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp)
    ) {
        BottomSheetHeader(title = "Gender", onClose = onClose)

        options.forEach { text ->
            val isSelected = text.equals(selectedOption, ignoreCase = true)
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
                
                RadioButton(
                    selected = isSelected,
                    onClick = { selectedOption = text },
                    colors = RadioButtonDefaults.colors(selectedColor = colorScheme.primary)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (isChanged) {
                    onGenderSelected(selectedOption)
                    onClose()
                }
            },
            enabled = isChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary,
                disabledContainerColor = colorScheme.primary.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(20.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Text("Change", color = if (isChanged) colorScheme.onPrimary else colorScheme.onPrimary.copy(alpha = 0.6f), fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun BirthdayBottomSheetContent(
    currentBirthday: String,
    onBirthdaySelected: (String) -> Unit,
    onClose: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    
    // Parse current birthday to initialize wheel pickers
    val initialDay: String
    val initialMonth: String
    val initialYear: String
    
    val parts = currentBirthday.split("/", "-")
    if (parts.size == 3) {
        // Handle DD/MM/YYYY or YYYY-MM-DD
        if (parts[0].length == 4) { // YYYY-MM-DD
            initialYear = parts[0]
            initialMonth = parts[1]
            initialDay = parts[2]
        } else { // DD/MM/YYYY
            initialDay = parts[0]
            initialMonth = parts[1]
            initialYear = parts[2]
        }
    } else {
        initialDay = "01"
        initialMonth = "01"
        initialYear = "2000"
    }

    val years = (1950..2024).map { it.toString() }
    val months = (1..12).map { it.toString().padStart(2, '0') }
    val days = (1..31).map { it.toString().padStart(2, '0') }

    var selectedYear by remember { mutableStateOf(initialYear) }
    var selectedMonth by remember { mutableStateOf(initialMonth) }
    var selectedDay by remember { mutableStateOf(initialDay) }

    val formattedNewDate = "$selectedDay/$selectedMonth/$selectedYear"
    val isChanged = formattedNewDate != currentBirthday && 
                    formattedNewDate != currentBirthday.replace("-", "/")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BottomSheetHeader(title = "Birthday", onClose = onClose)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            WheelPicker(items = days, initialValue = selectedDay, onItemSelected = { selectedDay = it }, modifier = Modifier.weight(1f))
            WheelPicker(items = months, initialValue = selectedMonth, onItemSelected = { selectedMonth = it }, modifier = Modifier.weight(1f))
            WheelPicker(items = years, initialValue = selectedYear, onItemSelected = { selectedYear = it }, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (isChanged) {
                    onBirthdaySelected(formattedNewDate)
                    onClose()
                }
            },
            enabled = isChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary,
                disabledContainerColor = colorScheme.primary.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(20.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Text("Change", color = if (isChanged) colorScheme.onPrimary else colorScheme.onPrimary.copy(alpha = 0.6f), fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WheelPicker(
    items: List<String>,
    initialValue: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val itemHeight = 40.dp
    val visibleItemsCount = 5
    val startIndex = items.indexOf(initialValue).coerceAtLeast(0)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = startIndex)
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    
    LaunchedEffect(listState.firstVisibleItemIndex) {
        val centerIndex = listState.firstVisibleItemIndex
        if (centerIndex in items.indices) {
            onItemSelected(items[centerIndex])
        }
    }

    Box(
        modifier = modifier
            .height(itemHeight * visibleItemsCount)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
        )

        LazyColumn(
            state = listState,
            flingBehavior = snapFlingBehavior,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = itemHeight * 2),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(items) { index, item ->
                val isSelected = listState.firstVisibleItemIndex == index
                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        style = if (isSelected) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
