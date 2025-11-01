package com.example.gihealth.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.*
import java.time.format.TextStyle
import java.util.Locale

//DUMMY VALUES WILL BE REMOVED LATER
class AnalyticsViewModel : ViewModel() {
    private val today = LocalDate.now()
    private val startOfWeek = today.with(DayOfWeek.MONDAY)

    // Base values
    private val baseComfort = (4..8).random()
    private val baseWeight = (150..180).random()

    // Digestive comfort mock data
    val digestiveData: Map<String, Map<LocalDate, Int>> = mapOf(
        "Today" to mapOf(today to baseComfort + (-1..1).random()),
        "Yesterday" to mapOf(today.minusDays(1) to baseComfort + (-1..1).random()),
        "This Week" to (0..6).associate {
            startOfWeek.plusDays(it.toLong()) to (baseComfort + (-2..2).random()).coerceIn(1, 10)
        },
        "Last Week" to (0..6).associate {
            startOfWeek.minusWeeks(1).plusDays(it.toLong()) to (baseComfort + (-2..2).random()).coerceIn(1, 10)
        },
        "This Month" to (0 until today.lengthOfMonth()).associate {
            today.withDayOfMonth(it + 1) to (baseComfort + (-2..2).random()).coerceIn(1, 10)
        }
    )

    val weightData: Map<String, Map<LocalDate, Int>> = run {
        val weekWeights = generateSequence(baseWeight) {
            (it + (-2..2).random()).coerceIn(120, 220)
        }.take(7).toList()

        val monthWeights = generateSequence(baseWeight) {
            (it + (-1..1).random()).coerceIn(120, 220)
        }.take(today.lengthOfMonth()).toList()

        mapOf(
            "Today" to mapOf(today to baseWeight + (-1..1).random()),
            "Yesterday" to mapOf(today.minusDays(1) to baseWeight + (-2..2).random()),
            "This Week" to (0..6).associate { startOfWeek.plusDays(it.toLong()) to weekWeights[it] },
            "Last Week" to (0..6).associate {
                startOfWeek.minusWeeks(1).plusDays(it.toLong()) to (baseWeight + (-5..5).random())
            },
            "This Month" to (0 until today.lengthOfMonth()).associate {
                today.withDayOfMonth(it + 1) to monthWeights[it]
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onOpenCalendar: () -> Unit,
    vm: CalendarViewModel,
    analyticsVM: AnalyticsViewModel = viewModel()
) {
    var expanded by remember { mutableStateOf(false) }
    var typeOfRange by remember { mutableStateOf("This Week") }

    val overviewOptions = listOf("Today", "Yesterday", "This Week", "Last Week", "This Month")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Analytics", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text("Overview", fontSize = 16.sp, color = Color.DarkGray)
                Spacer(Modifier.height(12.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = typeOfRange,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Range") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(0.7f),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Gray
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        overviewOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    typeOfRange = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }

        item { DigestiveComfortCard(typeOfRange, analyticsVM) }

        item { WeightTrackerCard(typeOfRange, analyticsVM) }

        item { RecentTrendsCard() }

        item { MoodCalendarWidget(vm = vm, onOpen = onOpenCalendar) }

        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
fun DigestiveComfortCard(typeOfRange: String, analyticsVM: AnalyticsViewModel) {
    val today = LocalDate.now()
    val mockData = analyticsVM.digestiveData[typeOfRange] ?: emptyMap()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Digestive Comfort (1–10)", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            if (typeOfRange == "Today" || typeOfRange == "Yesterday") {
                val day = if (typeOfRange == "Today") today else today.minusDays(1)
                val value = mockData[day] ?: 5
                val color = when (value) {
                    in 1..3 -> Color(0xFFD32F2F)
                    in 4..6 -> Color(0xFFFFC107)
                    else -> Color(0xFF388E3C)
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Text(
                        text = value.toString(),
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (typeOfRange == "Today")
                            "Your digestive comfort today"
                        else
                            "Your digestive comfort yesterday",
                        color = Color.DarkGray,
                        fontSize = 16.sp
                    )
                }
            } else {
                DigestiveComfortGraph(
                    data = mockData,
                    typeOfRange = typeOfRange,
                    compactView = false
                )
            }
        }
    }
}

@Composable
fun DigestiveComfortGraph(
    data: Map<LocalDate, Int>,
    typeOfRange: String,
    compactView: Boolean
) {
    val today = LocalDate.now()
    val startOfWeek = today.with(DayOfWeek.MONDAY)
    val daysToShow = when (typeOfRange) {
        "This Week" -> (0..6).map { startOfWeek.plusDays(it.toLong()) }
        "Last Week" -> (0..6).map { startOfWeek.minusWeeks(1).plusDays(it.toLong()) }
        "This Month" -> (0 until today.lengthOfMonth()).map { today.withDayOfMonth(it + 1) }
        else -> emptyList()
    }
    val dayLabels = daysToShow.map {
        when (typeOfRange) {
            "This Month" -> it.dayOfMonth.toString()
            else -> it.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (compactView) 220.dp else 400.dp)
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        val maxY = 10f
        val minY = 0f
        val rangeY = maxY - minY
        val chartWidth = size.width
        val chartHeight = size.height * 0.85f
        val spacingX = if (daysToShow.size > 1) chartWidth / (daysToShow.size - 1) else chartWidth

        // Horizontal grid lines
        for (i in 1..10) {
            val y = chartHeight - (i / rangeY * chartHeight)
            drawLine(Color.LightGray.copy(alpha = 0.3f), Offset(0f, y), Offset(chartWidth, y), 1f)
            drawContext.canvas.nativeCanvas.drawText(
                i.toString(),
                -32f,
                y + 6f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = 24f
                    textAlign = android.graphics.Paint.Align.LEFT
                }
            )
        }

        // Vertical grid lines
        val verticalInterval = if (typeOfRange == "This Month") 3 else 1
        for (i in daysToShow.indices step verticalInterval) {
            val x = i * spacingX
            drawLine(Color.LightGray.copy(alpha = 0.25f), Offset(x, 0f), Offset(x, chartHeight), 1f)
        }

        // Graph line
        val validDates = daysToShow.filter { it <= today && data.containsKey(it) }
        if (validDates.size > 1) {
            for (i in 1 until validDates.size) {
                val startX = (daysToShow.indexOf(validDates[i - 1])) * spacingX
                val startY = chartHeight - ((data[validDates[i - 1]]!! - minY) / rangeY * chartHeight)
                val endX = (daysToShow.indexOf(validDates[i])) * spacingX
                val endY = chartHeight - ((data[validDates[i]]!! - minY) / rangeY * chartHeight)
                drawLine(Color(0xFF0F9D58), Offset(startX, startY),
                    Offset(endX, endY), 5f, StrokeCap.Round
                )
            }
        }

        // Dots + labels
        val labelInterval = if (typeOfRange == "This Month") 3 else 1
        daysToShow.forEachIndexed { i, date ->
            val x = i * spacingX
            val value = data[date]
            if (value != null && date <= today) {
                val y = chartHeight - ((value - minY) / rangeY * chartHeight)
                drawCircle(color = Color(0xFF0F9D58), radius = 7f, center = Offset(x, y))
            }
            if (i % labelInterval == 0 || i == daysToShow.lastIndex) {
                drawContext.canvas.nativeCanvas.drawText(
                    dayLabels[i],
                    x,
                    size.height - 4f,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.DKGRAY
                        textSize = 24f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
        }
    }
}

@Composable
fun WeightTrackerCard(typeOfRange: String, analyticsVM: AnalyticsViewModel) {
    val today = LocalDate.now()
    val mockData = analyticsVM.weightData[typeOfRange] ?: emptyMap()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Weight Tracker (lbs)", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            if (typeOfRange == "Today" || typeOfRange == "Yesterday") {
                val day = if (typeOfRange == "Today") today else today.minusDays(1)
                val value = mockData[day] ?: 160

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Text(
                        text = "$value",
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4285F4)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (typeOfRange == "Today")
                            "Your weight today"
                        else
                            "Your weight yesterday",
                        color = Color.DarkGray,
                        fontSize = 16.sp
                    )
                }
            } else {
                WeightGraph(mockData, typeOfRange)
            }
        }
    }
}

@Composable
fun WeightGraph(data: Map<LocalDate, Int>, typeOfRange: String) {
    val today = LocalDate.now()
    val startOfWeek = today.with(DayOfWeek.MONDAY)
    val daysToShow = when (typeOfRange) {
        "This Week" -> (0..6).map { startOfWeek.plusDays(it.toLong()) }
        "Last Week" -> (0..6).map { startOfWeek.minusWeeks(1).plusDays(it.toLong()) }
        "This Month" -> (0 until today.lengthOfMonth()).map { today.withDayOfMonth(it + 1) }
        else -> emptyList()
    }
    val dayLabels = daysToShow.map {
        when (typeOfRange) {
            "This Month" -> it.dayOfMonth.toString()
            else -> it.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        val minY = 120f
        val maxY = 200f
        val rangeY = maxY - minY
        val chartWidth = size.width
        val chartHeight = size.height * 0.85f
        val spacingX = if (daysToShow.size > 1) chartWidth / (daysToShow.size - 1) else chartWidth

        // Horizontal grid
        for (i in 0..8) {
            val yValue = minY + i * (rangeY / 8)
            val y = chartHeight - ((yValue - minY) / rangeY * chartHeight)
            drawLine(Color.LightGray.copy(alpha = 0.3f), Offset(0f, y), Offset(chartWidth, y), 1f)
            drawContext.canvas.nativeCanvas.drawText(
                "${yValue.toInt()}",
                -40f,
                y + 6f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = 24f
                    textAlign = android.graphics.Paint.Align.LEFT
                }
            )
        }

        // Vertical grid
        val verticalInterval = if (typeOfRange == "This Month") 3 else 1
        for (i in daysToShow.indices step verticalInterval) {
            val x = i * spacingX
            drawLine(Color.LightGray.copy(alpha = 0.25f), Offset(x, 0f), Offset(x, chartHeight), 1f)
        }

        // Graph line
        val validDates = daysToShow.filter { it <= today && data.containsKey(it) }
        if (validDates.size > 1) {
            for (i in 1 until validDates.size) {
                val startX = (daysToShow.indexOf(validDates[i - 1])) * spacingX
                val startY = chartHeight - ((data[validDates[i - 1]]!! - minY) / rangeY * chartHeight)
                val endX = (daysToShow.indexOf(validDates[i])) * spacingX
                val endY = chartHeight - ((data[validDates[i]]!! - minY) / rangeY * chartHeight)
                drawLine(Color(0xFF4285F4), Offset(startX, startY), Offset(endX, endY), 5f, StrokeCap.Round)
            }
        }

        // Dots + labels
        val labelInterval = if (typeOfRange == "This Month") 3 else 1
        daysToShow.forEachIndexed { i, date ->
            val x = i * spacingX
            val value = data[date]
            if (value != null && date <= today) {
                val y = chartHeight - ((value - minY) / rangeY * chartHeight)
                drawCircle(color = Color(0xFF4285F4), radius = 7f, center = Offset(x, y))
            }
            if (i % labelInterval == 0 || i == daysToShow.lastIndex) {
                drawContext.canvas.nativeCanvas.drawText(
                    dayLabels[i],
                    x,
                    size.height - 4f,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.DKGRAY
                        textSize = 24f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
        }
    }
}


@Composable
fun RecentTrendsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Recent Trends:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            Text("• Digestive comfort improved after reducing dairy intake.")
            Text("• Stable weight trend across the week.")
            Text("• Slight fluctuation observed after late-night meals.")
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("5 Improved", color = Color(0xFF388E3C), fontWeight = FontWeight.Bold)
                Text(" | 2 Stable | ")
                Text("1 Higher", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
            }
        }
    }
}
