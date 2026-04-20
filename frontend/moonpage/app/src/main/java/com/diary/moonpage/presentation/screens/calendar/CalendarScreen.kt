package com.diary.moonpage.presentation.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SentimentSatisfiedAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.diary.moonpage.presentation.components.calendar.*
import com.diary.moonpage.presentation.theme.MoonPageTheme
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarScreen(
    onNavigateToFilter: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    
    CalendarContent(
        currentMonthName = "Apr 2026", 
        selectedDate = selectedDate,
        onDateSelected = { selectedDate = it },
        onFilterClick = onNavigateToFilter,
        onShareClick = { /* TODO */ }
    )
}

@Composable
fun CalendarContent(
    currentMonthName: String,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onFilterClick: () -> Unit,
    onShareClick: () -> Unit
) {
    val themePrimary = MaterialTheme.colorScheme.primary
    val themeSecondary = MaterialTheme.colorScheme.secondary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CalendarTopBar(
            currentMonth = currentMonthName,
            onFilterClick = onFilterClick,
            onPrevMonth = {},
            onNextMonth = {},
            onShareClick = onShareClick
        )

        Spacer(modifier = Modifier.height(60.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Top // Changed to Top to keep it clean after the 20dp spacer
        ) {
            CalendarHeader()

            // Calendar Grid Placeholder
            val daysInMonth = (1..30).toList()
            val firstDayOffset = 3 // Wednesday for Apr 2026
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                // Empty spaces for offset
                items(firstDayOffset) {
                    DayItem(day = null, isSelected = false, moodColor = null, onClick = {})
                }
                
                items(daysInMonth) { day ->
                    val isSelected = day == 14
                    
                    // Sử dụng màu từ theme thay vì màu xanh fix cứng
                    val moodColor = when (day) {
                        6 -> themeSecondary
                        8 -> themePrimary
                        14 -> themePrimary
                        else -> null
                    }
                    
                    DayItem(
                        day = day,
                        isSelected = isSelected,
                        moodColor = moodColor,
                        onClick = { /* Handle click */ }
                    )
                }
            }
        }

        DiaryEntryPreview(
            date = "14 Tue",
            moodIcon = Icons.Rounded.SentimentSatisfiedAlt,
            moodColor = themePrimary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    MoonPageTheme(darkTheme = false) {
        CalendarScreen({}, {})
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarScreenDarkPreview() {
    MoonPageTheme(darkTheme = true) {
        CalendarScreen({}, {})
    }
}
