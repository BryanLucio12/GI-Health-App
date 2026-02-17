package com.example.gihealth.ui.screens

import android.app.Application
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gihealth.data.SymptomEntity
import com.example.gihealth.data.WellBeingEntity
import com.example.gihealth.data.WellBeingViewModel
import com.example.gihealth.data.UserInfoViewModel
import com.example.gihealth.utils.generatePdfReport
import java.time.*
import java.time.format.TextStyle
import java.util.Locale
import kotlin.collections.emptyList
import kotlin.math.roundToInt
import com.example.gihealth.data.TopSymptomResults
import com.example.gihealth.data.SymptomWithTrend
import com.example.gihealth.ui.viewmodel.ReportViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onOpenCalendar: () -> Unit,
    vm: CalendarViewModel,
    onGeneratePdf: () -> Unit
) {

    val context = LocalContext.current

    val reportVM: ReportViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(
            context.applicationContext as Application
        )
    )

    val symptomViewModel: SymptomViewModel = viewModel()

    val userInfoViewModel: UserInfoViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(
            context.applicationContext as Application
        )
    )
    val userInfo by userInfoViewModel.userInfo.observeAsState()

    // NEW: WellBeingViewModel for real weight data
    val wellBeingViewModel: WellBeingViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(
            context.applicationContext as Application
        )
    )

    var expanded by remember { mutableStateOf(false) }
    var typeOfRange by remember { mutableStateOf("This Week") }

    val symptoms by symptomViewModel.symptoms.collectAsState()
    val wellBeingEntries by wellBeingViewModel.entries.observeAsState(emptyList())

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

                val viewModel: SymptomViewModel = viewModel() // gets the ViewModel
                val symptomsList by viewModel.symptoms.collectAsState()
                // button to generate the pdf report
                Button(
                    onClick = {
                        reportVM.symptoms = symptomsList
                        reportVM.userInfoSnapshot = userInfo   // UserInfoEntity?

                        onGeneratePdf()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0F9D58)
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(top = 12.dp)
                ) {
                    Text("Generate PDF Report")
                }
            }
        }

        item { DigestiveComfortCard(typeOfRange = typeOfRange, symptoms = symptoms) }

        // UPDATED: pass real WellBeing entries to the weight tracker
        item { WeightTrackerCard(typeOfRange = typeOfRange, entries = wellBeingEntries) }

        item { SeverityOverTimeCard(symptoms = symptoms, range = typeOfRange) }

        item { TopSymptomsCard(topSymptoms = computeTopSymptomsWithTrend(symptoms, typeOfRange)) }

        item { RecentTrendsCard(range = typeOfRange, symptoms = symptoms, entries = wellBeingEntries) }

        item { MoodCalendarWidget(vm = vm, onOpen = onOpenCalendar) }

        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
fun SeverityOverTimeCard(
    symptoms: List<SymptomEntity>,
    range: String
) {
    val today = LocalDate.now()

    val grouped = filterSymptomsForRange(symptoms, range)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {

            Text("Symptom Severity (1–10)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))

            if (symptoms.isEmpty()) {
                Text("No symptom logs available.")
                return@Column
            }

            if (grouped.isEmpty()) {
                Text("No data available for selected range.")
                return@Column
            }

            if (range == "Today" || range == "Yesterday") {

                val date = if (range == "Today") today else today.minusDays(1)
                val dayList = grouped[date] ?: emptyList()

                val value = if (dayList.isNotEmpty())
                    dayList.map { it.severity }.average()
                else 0.0

                // Pick color
                val color = when (value.toInt()) {
                    in 1..3 -> Color(0xFFD32F2F)
                    in 4..6 -> Color(0xFFFFC107)
                    else -> Color(0xFF388E3C)
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = String.format("%.1f", value),
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (range == "Today")
                            "Average symptom severity today"
                        else
                            "Average symptom severity yesterday",
                        color = Color.DarkGray,
                        fontSize = 16.sp
                    )
                }

                return@Column
            }

            // --- Graph Mode (Week, Last Week, Month) ---
            val dailyAverages = grouped.toSortedMap().mapValues { (_, list) ->
                list.map { it.severity }.average()
            }

            SymptomSeverityGraph(dailyAverages, range)
        }
    }
}

fun filterSymptomsForRange(
    symptoms: List<SymptomEntity>,
    range: String
): Map<LocalDate, List<SymptomEntity>> {

    val today = LocalDate.now()
    val startOfWeek = today.with(DayOfWeek.MONDAY)
    val lastWeekStart = startOfWeek.minusWeeks(1)
    val lastWeekEnd = startOfWeek.minusDays(1)

    val filtered = symptoms.filter { symptom ->

        val date = Instant.ofEpochMilli(symptom.timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        when (range) {
            "Today" -> date == today
            "Yesterday" -> date == today.minusDays(1)
            "This Week" -> date in startOfWeek..today
            "Last Week" -> date in lastWeekStart..lastWeekEnd
            "This Month" -> date.month == today.month && date.year == today.year
            else -> false
        }
    }

    return filtered.groupBy {
        Instant.ofEpochMilli(it.timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }
}

fun computeDigestiveComfortForRange(
    symptoms: List<SymptomEntity>,
    range: String
): Map<LocalDate, Int> {

    val grouped = filterSymptomsForRange(symptoms, range)

    // Convert daily symptom avg severity -> daily comfort (1–10)
    return grouped.mapValues { (_, list) ->
        val avgSeverity = list.map { it.severity }.average()

        // Inverse of severity
        val comfort = 10.0 - avgSeverity
        comfort.coerceIn(1.0, 10.0).roundToInt()
    }
}


@Composable
fun SymptomSeverityGraph(
    data: Map<LocalDate, Double>,
    range: String
) {
    val today = LocalDate.now()
    val startOfWeek = today.with(DayOfWeek.MONDAY)

    // Generate full range of dates (even if empty)
    val days = when (range) {
        "Today" -> listOf(today)
        "Yesterday" -> listOf(today.minusDays(1))
        "This Week" -> (0..6).map { startOfWeek.plusDays(it.toLong()) }
        "Last Week" -> (0..6).map { startOfWeek.minusWeeks(1).plusDays(it.toLong()) }
        "This Month" -> (1..today.lengthOfMonth()).map { today.withDayOfMonth(it) }
        else -> emptyList()
    }

    // Extract severity values (null if no logs)
    val values = days.map { date -> data[date] }

    val dayLabels = days.map {
        when (range) {
            "This Month" -> it.dayOfMonth.toString()
            else -> it.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        val chartWidth = size.width
        val chartHeight = size.height * 0.80f

        val spacingX = if (days.size > 1)
            chartWidth / (days.size - 1)
        else
            chartWidth / 2f

        // Y-scale range
        val minY = 0f
        val maxY = 10f
        val rangeY = maxY - minY

        // Horizontal grid + labels
        for (i in 1..10) {
            val y = chartHeight - (i / rangeY * chartHeight)
            drawLine(
                Color.LightGray.copy(alpha = 0.25f),
                Offset(0f, y),
                Offset(chartWidth, y),
                1f
            )
            drawContext.canvas.nativeCanvas.drawText(
                i.toString(),
                -40f,
                y + 5f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.DKGRAY
                    textSize = 26f
                    textAlign = android.graphics.Paint.Align.LEFT
                }
            )
        }

        // Line + dots
        for (i in values.indices) {
            val x = if (days.size > 1) i * spacingX else chartWidth / 2f
            val value = values[i]

            if (value != null) {
                val y = chartHeight - (value / maxY * chartHeight)

                // Draw line segment from previous point
                if (i > 0 && values[i - 1] != null) {
                    val prevX = (i - 1) * spacingX
                    val prevY =
                        chartHeight - ((values[i - 1]!! / maxY) * chartHeight)

                    drawLine(
                        Color(0xFF0F9D58),
                        Offset(prevX.toFloat(), prevY.toFloat()),
                        Offset(x.toFloat(), y.toFloat()),
                        4f,
                        StrokeCap.Round
                    )
                }

                // Draw data point
                drawCircle(
                    color = Color(0xFF0F9D58),
                    radius = 7f,
                    center = Offset(x.toFloat(), y.toFloat())
                )
            }
        }

        // Draw bottom labels
        val labelInterval = if (range == "This Month") 3 else 1
        days.forEachIndexed { i, _ ->
            val x = if (days.size > 1) i * spacingX else chartWidth / 2f

            if (i % labelInterval == 0 || i == days.lastIndex) {
                drawContext.canvas.nativeCanvas.drawText(
                    dayLabels[i],
                    x,
                    size.height,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.DKGRAY
                        textSize = 28f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
        }
    }
}

@Composable
fun TopSymptomsCard(
    topSymptoms: List<SymptomWithTrend>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {

            Text("Most Common Symptoms", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            if (topSymptoms.isEmpty()) {
                Text("No symptom logs available.")
                return@Column
            } else {
                topSymptoms.forEachIndexed { index, s ->

                    // TREND ARROW — depends on symptom trend
                    val arrow = when (s.trend) {
                        "up" -> "↑"
                        "down" -> "↓"
                        else -> "→"
                    }

                    // ARROW COLOR
                    val arrowColor = when (s.trend) {
                        "up" -> Color(0xFFD32F2F)        // red
                        "down" -> Color(0xFF388E3C)      // green
                        else -> Color(0xFF757575)        // grey
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = s.name,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )

                            Text(
                                "Logged ${s.count} times",
                                color = Color.DarkGray,
                                fontSize = 14.sp
                            )

                            Text(
                                "Avg Severity: ${String.format("%.1f", s.avgSeverity)}",
                                color = Color.DarkGray,
                                fontSize = 14.sp
                            )
                        }

                        // The TREND ARROW
                        Text(
                            text = arrow,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = arrowColor
                        )
                    }

                    if (index != topSymptoms.lastIndex) {
                        HorizontalDivider(
                            color = Color(0xFFE0E0E0),
                            thickness = 0.7.dp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

fun previousRange(range: String): String = when (range) {
    "Today" -> "Yesterday"
    "This Week" -> "Last Week"
    "This Month" -> "Last Month"
    else -> range
}

fun computeTopSymptomsForRange(
    symptoms: List<SymptomEntity>,
    range: String
): List<TopSymptomResults> {

    val today = LocalDate.now()
    val grouped = filterSymptomsForRange(symptoms, range)

    if (range == "Today" || range == "Yesterday") {

        val targetDay = if (range == "Today") today else today.minusDays(1)
        val logs = grouped[targetDay] ?: emptyList()

        return logs
            .groupBy { it.name }
            .map { (name, entries) ->
                TopSymptomResults(
                    name = name,
                    count = entries.size,
                    avgSeverity = entries.map { it.severity }.average()
                )
            }
            .sortedByDescending { it.count }
            .take(3)
    }

    val combinedLogs = grouped.values.flatten()

    return combinedLogs
        .groupBy { it.name }
        .map { (name, entries) ->
            TopSymptomResults(
                name = name,
                count = entries.size,
                avgSeverity = entries.map { it.severity }.average()
            )
        }
        .sortedByDescending { it.count }
        .take(3)
}

fun computeTopSymptomsWithTrend(
    symptoms: List<SymptomEntity>,
    range: String
): List<SymptomWithTrend> {

    val current = computeTopSymptomsForRange(symptoms, range)
    val previous = computeTopSymptomsForRange(symptoms, previousRange(range))

    val prevMap = previous.associateBy { it.name }

    return current.map { cur ->
        val prev = prevMap[cur.name]?.avgSeverity
        val diff = if (prev != null) cur.avgSeverity - prev else 0.0

        val trend = when {
            prev == null -> "→"
            diff > 0.5 -> "↑"
            diff < -0.5 -> "↓"
            else -> "→"
        }

        SymptomWithTrend(
            name = cur.name,
            count = cur.count,
            avgSeverity = cur.avgSeverity,
            trend = trend,
            prevAvg = prev
        )
    }
}

@Composable
fun DigestiveComfortCard(typeOfRange: String, symptoms: List<SymptomEntity>) {
    val today = LocalDate.now()
    val comfortData = remember(symptoms, typeOfRange) {
        computeDigestiveComfortForRange(symptoms, typeOfRange)
    }

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

            if (symptoms.isEmpty()) {
                Text("No symptom logs available.")
                return@Column
            }

            if (typeOfRange == "Today" || typeOfRange == "Yesterday") {
                val day = if (typeOfRange == "Today") today else today.minusDays(1)
                val value = comfortData[day]

                if (value == null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        Text(
                            text = "No comfort data for ${if (typeOfRange == "Today") "today" else "yesterday"}.",
                            color = Color.DarkGray
                        )
                    }
                    return@Column
                }

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
                if (comfortData.isEmpty()) {
                    Text("No data available for selected range.")
                    return@Column
                }

                DigestiveComfortGraph(
                    data = comfortData,
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
                drawLine(
                    Color(0xFF0F9D58),
                    Offset(startX, startY),
                    Offset(endX, endY),
                    5f,
                    StrokeCap.Round
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
fun WeightTrackerCard(typeOfRange: String, entries: List<WellBeingEntity>) {
    val today = LocalDate.now()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Weight Tracker (lbs)", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            if (entries.isEmpty()) {
                Text("No weight entries logged yet.")
                return@Column
            }

            // Build map of LocalDate -> Int (rounded weight), taking the latest entry for each day
            val weightsByDate: Map<LocalDate, Int> = remember(entries) {
                entries
                    .groupBy { entry ->
                        Instant.ofEpochMilli(entry.timestamp)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    }
                    .mapValues { (_, list) ->
                        list.maxBy { it.timestamp }.weight.roundToInt()
                    }
            }

            if (typeOfRange == "Today" || typeOfRange == "Yesterday") {
                val day = if (typeOfRange == "Today") today else today.minusDays(1)
                val value = weightsByDate[day]

                if (value == null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        Text(
                            text = "No weight logged for ${if (typeOfRange == "Today") "today" else "yesterday"}.",
                            color = Color.DarkGray
                        )
                    }
                    return@Column
                }

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

                return@Column
            }

            // For ranges that show a graph: This Week, Last Week, This Month
            val startOfWeek = today.with(DayOfWeek.MONDAY)
            val lastWeekStart = startOfWeek.minusWeeks(1)
            val lastWeekEnd = startOfWeek.minusDays(1)

            val rangeData: Map<LocalDate, Int> = when (typeOfRange) {
                "This Week" -> weightsByDate.filterKeys { it in startOfWeek..today }
                "Last Week" -> weightsByDate.filterKeys { it in lastWeekStart..lastWeekEnd }
                "This Month" -> weightsByDate.filterKeys {
                    it.month == today.month && it.year == today.year
                }
                else -> emptyMap()
            }

            if (rangeData.isEmpty()) {
                Text("No weight data available for selected range.")
                return@Column
            }

            WeightGraph(data = rangeData, typeOfRange = typeOfRange)
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
        if (data.isEmpty()) return@Canvas

        val actualMin = (data.values.minOrNull() ?: 120).toFloat()
        val actualMax = (data.values.maxOrNull() ?: 200).toFloat()

        val padding = 10f

        val minY = (actualMin - padding).coerceAtLeast(80f)
        val maxY = (actualMax + padding).coerceAtMost(300f)

        val rangeY = maxY - minY

        val chartWidth = size.width
        val chartHeight = size.height * 0.85f
        val spacingX =
            if (daysToShow.size > 1) chartWidth / (daysToShow.size - 1)
            else chartWidth / 2f

        val steps = 8
        for (i in 0..steps) {
            val yValue = minY + i * (rangeY / steps)
            val y = chartHeight - ((yValue - minY) / rangeY * chartHeight)

            drawLine(
                Color.LightGray.copy(alpha = 0.3f),
                Offset(0f, y),
                Offset(chartWidth, y),
                1f
            )

            drawContext.canvas.nativeCanvas.drawText(
                yValue.roundToInt().toString(),
                -40f,
                y + 6f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = 24f
                    textAlign = android.graphics.Paint.Align.LEFT
                }
            )
        }

        val verticalInterval = if (typeOfRange == "This Month") 3 else 1
        for (i in daysToShow.indices step verticalInterval) {
            val x = if (daysToShow.size > 1) i * spacingX else chartWidth / 2f
            drawLine(
                Color.LightGray.copy(alpha = 0.25f),
                Offset(x, 0f),
                Offset(x, chartHeight),
                1f
            )
        }

        val validDates = daysToShow.filter { it <= today && data.containsKey(it) }

        if (validDates.size > 1) {
            for (i in 1 until validDates.size) {

                val startIndex = daysToShow.indexOf(validDates[i - 1])
                val endIndex = daysToShow.indexOf(validDates[i])

                val startX = if (daysToShow.size > 1) startIndex * spacingX else chartWidth / 2f
                val endX = if (daysToShow.size > 1) endIndex * spacingX else chartWidth / 2f

                val startY =
                    chartHeight - ((data[validDates[i - 1]]!! - minY) / rangeY * chartHeight)
                val endY =
                    chartHeight - ((data[validDates[i]]!! - minY) / rangeY * chartHeight)

                drawLine(
                    Color(0xFF0F9D58),
                    Offset(startX, startY),
                    Offset(endX, endY),
                    5f,
                    StrokeCap.Round
                )
            }
        }

        val labelInterval = if (typeOfRange == "This Month") 3 else 1

        daysToShow.forEachIndexed { i, date ->
            val x = if (daysToShow.size > 1) i * spacingX else chartWidth / 2f
            val value = data[date]

            if (value != null && date <= today) {
                val y = chartHeight - ((value - minY) / rangeY * chartHeight)
                drawCircle(
                    color = Color(0xFF0F9D58),
                    radius = 7f,
                    center = Offset(x, y)
                )
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

private data class RecentTrendsResult(
    val bullets: List<String>,
    val improved: Int,
    val stable: Int,
    val worse: Int
)

@Composable
fun RecentTrendsCard(
    range: String,
    symptoms: List<SymptomEntity>,
    entries: List<WellBeingEntity>
) {
    // Previous range
    val prevRange = when (range) {
        "Today" -> "Yesterday"
        "This Week" -> "Last Week"
        else -> null   // no Last Month needed
    }

    val trends = remember(range, symptoms, entries) {
        buildRecentTrends(range, prevRange, symptoms, entries)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Recent Trends:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))

            if (trends.bullets.isEmpty()) {
                Text("• Not enough data to generate trends for this range.")
            } else {
                trends.bullets.take(3).forEach { Text("• $it") }
            }

            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${trends.improved} Improved", color = Color(0xFF388E3C), fontWeight = FontWeight.Bold)
                Text(" | ${trends.stable} Stable | ")
                Text("${trends.worse} Higher", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun buildRecentTrends(
    range: String,
    prevRange: String?,
    symptoms: List<SymptomEntity>,
    entries: List<WellBeingEntity>
): RecentTrendsResult {

    val bullets = mutableListOf<String>()
    var improved = 0
    var stable = 0
    var worse = 0

    fun scoreDelta(delta: Double, betterWhenHigher: Boolean, label: String, compareLabel: String) {
        val threshold = 0.5
        val isImproved = if (betterWhenHigher) delta > threshold else delta < -threshold
        val isWorse = if (betterWhenHigher) delta < -threshold else delta > threshold

        when {
            isImproved -> { improved++; bullets += "$label improved compared to $compareLabel." }
            isWorse -> { worse++; bullets += "$label increased compared to $compareLabel." }
            else -> { stable++; bullets += "$label stayed stable compared to $compareLabel." }
        }
    }

    // Digestive comfort
    val comfortNow = computeDigestiveComfortForRange(symptoms, range).values.map { it.toDouble() }.averageOrNull()
    val comfortPrev = prevRange?.let { computeDigestiveComfortForRange(symptoms, it).values.map { v -> v.toDouble() }.averageOrNull() }

    if (comfortNow != null && comfortPrev != null) {
        scoreDelta(comfortNow - comfortPrev, betterWhenHigher = true, label = "Digestive comfort", compareLabel = prevRange)
    } else if (comfortNow != null) {
        bullets += "Digestive comfort average: ${comfortNow.roundToInt()}/10."
        stable++
    }

    // Weight
    val weightNow = averageDailyLatest(entries, range) { it.weight.toDouble() }
    val weightPrev = prevRange?.let { averageDailyLatest(entries, it) { e -> e.weight.toDouble() } }

    if (weightNow != null && weightPrev != null) {
        val delta = weightNow - weightPrev
        val threshold = 0.5
        when {
            delta > threshold -> { worse++; bullets += "Average weight increased compared to $prevRange." }
            delta < -threshold -> { improved++; bullets += "Average weight decreased compared to $prevRange." }
            else -> { stable++; bullets += "Weight remained stable compared to $prevRange." }
        }
    } else if (weightNow != null) {
        bullets += "Average weight logged: ${weightNow.roundToInt()}."
        stable++
    }

    // Stress
    val stressNow = averageDailyLatest(entries, range) { it.stressRating.toDouble() }
    val stressPrev = prevRange?.let { averageDailyLatest(entries, it) { e -> e.stressRating.toDouble() } }

    if (stressNow != null && stressPrev != null) {
        scoreDelta(stressNow - stressPrev, betterWhenHigher = false, label = "Stress level", compareLabel = prevRange)
    } else if (stressNow != null) {
        bullets += "Average stress rating: ${stressNow.roundToInt()}/10."
        stable++
    }

    // Sleep
    val sleepNow = averageDailyLatest(entries, range) { it.sleepRating.toDouble() }
    val sleepPrev = prevRange?.let { averageDailyLatest(entries, it) { e -> e.sleepRating.toDouble() } }

    if (sleepNow != null && sleepPrev != null) {
        scoreDelta(sleepNow - sleepPrev, betterWhenHigher = true, label = "Sleep quality", compareLabel = prevRange)
    } else if (sleepNow != null) {
        bullets += "Average sleep rating: ${sleepNow.roundToInt()}/10."
        stable++
    }

    // Top symptom trend
    val top = computeTopSymptomsWithTrend(symptoms, range).firstOrNull()
    if (top != null) {
        val msg = when (top.trend) {
            "↑" -> { worse++; "${top.name} severity increased in this period." }
            "↓" -> { improved++; "${top.name} severity decreased in this period." }
            else -> { stable++; "${top.name} severity stayed stable in this period." }
        }
        bullets += msg
    }

    return RecentTrendsResult(
        bullets = bullets.distinct(),
        improved = improved,
        stable = stable,
        worse = worse
    )
}

private fun averageDailyLatest(
    entries: List<WellBeingEntity>,
    range: String,
    selector: (WellBeingEntity) -> Double
): Double? {
    val zone = ZoneId.systemDefault()
    val filtered = filterWellBeingForRange(entries, range, zone)
    if (filtered.isEmpty()) return null

    val latestPerDay = filtered
        .groupBy { Instant.ofEpochMilli(it.timestamp).atZone(zone).toLocalDate() }
        .mapValues { (_, list) -> list.maxByOrNull { it.timestamp }!! }

    return latestPerDay.values.map(selector).averageOrNull()
}

private fun filterWellBeingForRange(
    entries: List<WellBeingEntity>,
    range: String,
    zone: ZoneId
): List<WellBeingEntity> {
    val today = LocalDate.now()
    val startOfWeek = today.with(DayOfWeek.MONDAY)
    val lastWeekStart = startOfWeek.minusWeeks(1)
    val lastWeekEnd = startOfWeek.minusDays(1)

    fun dateOf(e: WellBeingEntity) =
        Instant.ofEpochMilli(e.timestamp).atZone(zone).toLocalDate()

    return entries.filter {
        val d = dateOf(it)
        when (range) {
            "Today" -> d == today
            "Yesterday" -> d == today.minusDays(1)
            "This Week" -> d in startOfWeek..today
            "Last Week" -> d in lastWeekStart..lastWeekEnd
            "This Month" -> d.month == today.month && d.year == today.year
            else -> false
        }
    }
}

private fun Collection<Double>.averageOrNull(): Double? = if (isEmpty()) null else average()


