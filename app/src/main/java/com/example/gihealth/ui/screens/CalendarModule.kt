package com.example.gihealth.ui.screens


import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.clickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

// ---------- State ----------
enum class DayStatus { GOOD, BAD }

class CalendarViewModel : ViewModel() {
    val moodMap = mutableStateMapOf<LocalDate, DayStatus>()
    var lastClicked by mutableStateOf<Pair<LocalDate, DayStatus>?>(null)

    fun setStatus(date: LocalDate, status: DayStatus) {
        moodMap[date] = status
        lastClicked = date to status
    }
    fun clear(date: LocalDate) {
        moodMap.remove(date)
        if (lastClicked?.first == date) lastClicked = null
    }
}

// ---------- Widget (used on Analytics screen) ----------
@Composable
fun MoodCalendarWidget(
    vm: CalendarViewModel,
    onOpen: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(380.dp)
            .clickable { onOpen() }, // open full calendar
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        var currentMonth by remember { mutableStateOf(YearMonth.now()) }
        var paintMode by remember { mutableStateOf(DayStatus.GOOD) }

        Column(Modifier.fillMaxSize().padding(16.dp)) {

            // Month header
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) { Text("◀") }
                Text(
                    text = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) +
                            " " + currentMonth.year,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                TextButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) { Text("▶") }
            }

            Spacer(Modifier.height(8.dp))

            // Mode chips + Clear
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Good
                FilterChip(
                    selected = paintMode == DayStatus.GOOD,
                    onClick = { paintMode = DayStatus.GOOD },
                    label = { Text("Good") },
                    leadingIcon = {
                        Box(
                            Modifier
                                .size(12.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF2ECC71))
                        )
                    }
                )
                // Bad
                FilterChip(
                    selected = paintMode == DayStatus.BAD,
                    onClick = { paintMode = DayStatus.BAD },
                    label = { Text("Bad") },
                    leadingIcon = {
                        Box(
                            Modifier
                                .size(12.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFE74C3C))
                        )
                    }
                )
                Spacer(Modifier.weight(1f))
                TextButton(onClick = {
                    val start = currentMonth.atDay(1)
                    val end = currentMonth.atEndOfMonth()
                    vm.moodMap.keys.filter { it in start..end }.toList().forEach { vm.clear(it) }
                }) { Text("Clear") }
            }

            Spacer(Modifier.height(6.dp))

            // Weekdays
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("Sun","Mon","Tue","Wed","Thu","Fri","Sat").forEach {
                    Text(
                        it,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            // 7x6 grid filling remaining card height (no vertical scroll)
            val cells = remember(currentMonth) { daysOfMonthGrid(currentMonth) }
            Box(Modifier.fillMaxWidth().weight(1f)) {
                BoxWithConstraints(Modifier.fillMaxSize()) {
                    val maxW = this.maxWidth
                    val maxH = this.maxHeight

                    val cols = 7; val rows = 6
                    val h = 6.dp; val v = 6.dp
                    val cellW = (maxW - h * (cols - 1)) / cols
                    val cellH = (maxH - v * (rows - 1)) / rows
                    val cellSize = minOf(cellW, cellH)

                    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(v)) {
                        repeat(rows) { r ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(h)) {
                                repeat(cols) { c ->
                                    val i = r * cols + c
                                    val day = cells.getOrNull(i)
                                    if (day == null) {
                                        Box(Modifier.size(cellSize))
                                    } else {
                                        val status = vm.moodMap[day]
                                        DayCell(
                                            date = day,
                                            status = status,
                                            onClick = { vm.setStatus(day, paintMode) },
                                            onLongPressClear = { vm.clear(day) },
                                            modifier = Modifier.size(cellSize)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Show last selection (if any)
            vm.lastClicked?.let { (date, status) ->
                val label = if (status == DayStatus.GOOD) "Good" else "Bad"
                val color = if (status == DayStatus.GOOD) Color(0xFF2ECC71) else Color(0xFFE74C3C)
                val d = date.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
                Spacer(Modifier.height(8.dp))
                Text("Selected: $d — $label", color = color, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ---------- Full-screen calendar (with X to close) ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullCalendarScreen(
    onClose: () -> Unit,
    vm: CalendarViewModel
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var paintMode by remember { mutableStateOf(DayStatus.GOOD) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendar") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) { Text("◀") }
                Text(
                    text = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) +
                            " " + currentMonth.year,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) { Text("▶") }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = paintMode == DayStatus.GOOD, onClick = { paintMode = DayStatus.GOOD },
                    label = { Text("Good") },
                    leadingIcon = {
                        Box(
                            Modifier
                                .size(12.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF2ECC71))
                        )
                    }
                )
                FilterChip(
                    selected = paintMode == DayStatus.BAD, onClick = { paintMode = DayStatus.BAD },
                    label = { Text("Bad") },
                    leadingIcon = {
                        Box(
                            Modifier
                                .size(12.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFE74C3C))
                        )
                    }
                )
                Spacer(Modifier.weight(1f))
                TextButton(onClick = {
                    val start = currentMonth.atDay(1)
                    val end = currentMonth.atEndOfMonth()
                    vm.moodMap.keys.filter { it in start..end }.toList().forEach { vm.clear(it) }
                }) { Text("Clear month") }
            }

            Spacer(Modifier.height(6.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("Sun","Mon","Tue","Wed","Thu","Fri","Sat").forEach {
                    Text(
                        it,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            val cells = remember(currentMonth) { daysOfMonthGrid(currentMonth) }
            Box(Modifier.fillMaxWidth().weight(1f)) {
                BoxWithConstraints(Modifier.fillMaxSize()) {
                    val maxW = this.maxWidth
                    val maxH = this.maxHeight

                    val cols = 7; val rows = 6
                    val h = 6.dp; val v = 6.dp
                    val cellW = (maxW - h * (cols - 1)) / cols
                    val cellH = (maxH - v * (rows - 1)) / rows
                    val cellSize = minOf(cellW, cellH)

                    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(v)) {
                        repeat(rows) { r ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(h)) {
                                repeat(cols) { c ->
                                    val i = r * cols + c
                                    val day = cells.getOrNull(i)
                                    if (day == null) {
                                        Box(Modifier.size(cellSize))
                                    } else {
                                        val status = vm.moodMap[day]
                                        DayCell(
                                            date = day,
                                            status = status,
                                            onClick = { vm.setStatus(day, paintMode) },
                                            onLongPressClear = { vm.clear(day) },
                                            modifier = Modifier.size(cellSize)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------- Shared visuals ----------
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DayCell(
    date: LocalDate,
    status: DayStatus?,
    onClick: () -> Unit,
    onLongPressClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = when (status) {
        DayStatus.GOOD -> Color(0xFF2ECC71)
        DayStatus.BAD  -> Color(0xFFE74C3C)
        null           -> Color.Transparent
    }
    val borderColor = if (status == null) Color(0x33000000) else Color.Transparent

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .background(bg)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongPressClear
            )
            .padding(4.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (status == null) Color.Unspecified else Color.White
        )
    }
}

private fun daysOfMonthGrid(month: YearMonth): List<LocalDate?> {
    val first = month.atDay(1)
    val days = month.lengthOfMonth()
    val offset = first.dayOfWeek.value % 7 // Sunday=0
    return MutableList<LocalDate?>(42) { null }.apply {
        for (i in 0 until days) this[offset + i] = first.plusDays(i.toLong())
    }
}
