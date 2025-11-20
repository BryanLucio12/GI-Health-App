@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.gihealth.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

// ---------------------------------------------------------
// Simple VM + data model
// ---------------------------------------------------------

class CalendarViewModel : ViewModel()

/**
 * Per-day food data.
 * Hook this up to your Room DB / repository later.
 */
data class FoodDay(
    val breakfast: String? = null,
    val lunch: String? = null,
    val dinner: String? = null
)

// ---------------------------------------------------------
// Compact calendar widget used on Analytics screen
// ---------------------------------------------------------

@Composable
fun MoodCalendarWidget(
    vm: CalendarViewModel,
    onOpen: () -> Unit
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val today = LocalDate.now()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Month header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "◀",
                    modifier = Modifier
                        .clickable { currentMonth = currentMonth.minusMonths(1) },
                    fontSize = 20.sp
                )
                Text(
                    text = monthTitle(currentMonth),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "▶",
                    modifier = Modifier
                        .clickable { currentMonth = currentMonth.plusMonths(1) },
                    fontSize = 20.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            // Weekday labels
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach {
                    Text(
                        text = it,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Calendar grid
            val cells = remember(currentMonth) { daysOfMonthGrid(currentMonth) }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                cells.chunked(7).forEach { week ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        week.forEach { date ->
                            if (date == null) {
                                // empty cell, but still takes 1/7 of the width
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                )
                            } else {
                                DayCell(
                                    date = date,
                                    isSelected = date == today,
                                    onClick = { onOpen() },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------
// Full calendar screen
// ---------------------------------------------------------

@Composable
fun FullCalendarScreen(
    onClose: () -> Unit,
    vm: CalendarViewModel,
    // You can plug your real data here later:
    getFoodForDate: (LocalDate) -> FoodDay? = { null },
    getJournalForDate: (LocalDate) -> String? = { null },
    getSymptomsForDate: (LocalDate) -> String? = { null }
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    val today = LocalDate.now()
    val headerFormatter =
        DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.getDefault())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Calendar") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Month + calendar section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // Month header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "◀",
                                modifier = Modifier
                                    .clickable {
                                        currentMonth = currentMonth.minusMonths(1)
                                    },
                                fontSize = 20.sp
                            )
                            Text(
                                text = monthTitle(currentMonth),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "▶",
                                modifier = Modifier
                                    .clickable {
                                        currentMonth = currentMonth.plusMonths(1)
                                    },
                                fontSize = 20.sp
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        // Weekday labels
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach {
                                Text(
                                    text = it,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        // Calendar grid
                        val cells = remember(currentMonth) { daysOfMonthGrid(currentMonth) }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            cells.chunked(7).forEach { week ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    week.forEach { date ->
                                        if (date == null) {
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                            )
                                        } else {
                                            DayCell(
                                                date = date,
                                                isSelected = date == selectedDate,
                                                onClick = { selectedDate = date },
                                                modifier = Modifier.size(40.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom details – scrolls with the rest of the screen
            item {
                CalendarDayDetailsSection(
                    selectedDate = selectedDate,
                    headerText = selectedDate.format(headerFormatter).uppercase(),
                    foodSummary = remember(selectedDate) {
                        // Build multi-line text from FoodDay
                        getFoodForDate(selectedDate)?.let { food ->
                            buildString {
                                food.breakfast?.takeIf { it.isNotBlank() }?.let {
                                    appendLine("Breakfast: $it")
                                }
                                food.lunch?.takeIf { it.isNotBlank() }?.let {
                                    appendLine("Lunch: $it")
                                }
                                food.dinner?.takeIf { it.isNotBlank() }?.let {
                                    append("Dinner: $it")
                                }
                            }.ifBlank { null }  // null = treated as "no food"
                        }
                    },
                    journalSummary = getJournalForDate(selectedDate),
                    symptomSummary = getSymptomsForDate(selectedDate)
                )
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

// ---------------------------------------------------------
// Shared pieces
// ---------------------------------------------------------

@Composable
private fun DayCell(
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val outlineColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)
    }

    val textColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = outlineColor,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            fontSize = 14.sp,
            color = textColor
        )
    }
}

private fun monthTitle(month: YearMonth): String =
    month.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } +
            " " + month.year

private fun daysOfMonthGrid(month: YearMonth): List<LocalDate?> {
    val first = month.atDay(1)
    val daysInMonth = month.lengthOfMonth()

    // java.time: Monday = 1 … Sunday = 7
    val offset = first.dayOfWeek.value % 7 // Sunday(7) -> 0

    val cells = MutableList<LocalDate?>(42) { null }
    for (i in 0 until daysInMonth) {
        cells[offset + i] = first.plusDays(i.toLong())
    }
    return cells
}

// ---------------------------------------------------------
// Detail section (Food / Journal / Symptoms)
// ---------------------------------------------------------

@Composable
private fun CalendarDayDetailsSection(
    selectedDate: LocalDate,
    headerText: String,
    foodSummary: String?,
    journalSummary: String?,
    symptomSummary: String?
) {
    var expanded by remember { mutableStateOf(true) }

    Column(
        Modifier
            .fillMaxWidth()
    ) {
        // Header with expand / collapse icon
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = headerText,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropUp,
                contentDescription = if (expanded) "Collapse" else "Expand"
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DayDetailCard(
                    title = "Food",
                    text = foodSummary,
                    emptyText = "No food entry"
                )

                DayDetailCard(
                    title = "Journal",
                    text = journalSummary,
                    emptyText = "No journal entry"
                )

                DayDetailCard(
                    title = "Symptoms",
                    text = symptomSummary,
                    emptyText = "No symptoms logged"
                )
            }
        }
    }
}

@Composable
private fun DayDetailCard(
    title: String,
    text: String?,
    emptyText: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = text ?: emptyText,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
