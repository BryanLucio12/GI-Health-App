package com.example.gihealth.utils

import com.example.gihealth.data.FoodEntity
import com.example.gihealth.data.SymptomEntity
import com.example.gihealth.data.WellBeingEntity
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

data class RecentTrendsSection(
    val title: String,
    val items: List<String>
)

data class RecentTrendsSummary(
    val quickBullets: List<String>,
    val improved: Int,
    val stable: Int,
    val worse: Int,
    val sections: List<RecentTrendsSection>
)

private data class DailyCombinedData(
    val date: LocalDate,
    val avgSymptomSeverity: Double?,
    val symptomCount: Int,
    val symptomNames: List<String>,
    val avgStress: Double?,
    val avgSleep: Double?,
    val looseStools: Int?,
    val foods: List<String>,
    val foodLogCount: Int
)

private data class FoodPattern(
    val food: String,
    val eatenAvg: Double,
    val notEatenAvg: Double,
    val impact: Double,
    val eatenDays: Int,
    val commonSymptoms: List<String>
)

object TrendsAnalyzer {

    private val foodDateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH)

    fun buildSummary(
        range: String,
        symptoms: List<SymptomEntity>,
        entries: List<WellBeingEntity>,
        foods: List<FoodEntity>
    ): RecentTrendsSummary {

        val dailyData = buildDailyData(range, symptoms, entries, foods)

        if (dailyData.size < 2) {
            return RecentTrendsSummary(
                quickBullets = listOf("Not enough data yet for detailed recent trends."),
                improved = 0,
                stable = 1,
                worse = 0,
                sections = listOf(
                    RecentTrendsSection(
                        title = "Overview",
                        items = listOf("Add more logs in this range to unlock trend insights.")
                    )
                )
            )
        }

        val sections = mutableListOf<RecentTrendsSection>()
        val quickBullets = mutableListOf<String>()

        var improved = 0
        var stable = 0
        var worse = 0

        val validSymptomDays = dailyData.filter { it.avgSymptomSeverity != null }

        // Stress vs symptoms
        val stressPairs = validSymptomDays.filter { it.avgStress != null }
        val stressCorr = correlation(
            stressPairs.map { it.avgStress!! },
            stressPairs.map { it.avgSymptomSeverity!! }
        )

        val stressItems = mutableListOf<String>()
        if (stressCorr != null) {
            when {
                stressCorr >= 0.35 -> {
                    stressItems += "Higher stress is associated with worse symptom days."
                    quickBullets += "Stress appears associated with worse symptom days."
                    worse++
                }
                stressCorr <= -0.35 -> {
                    stressItems += "Stress does not show a worsening pattern in this range."
                    quickBullets += "Stress does not show a worsening pattern in this range."
                    improved++
                }
                else -> {
                    stressItems += "Stress does not show a clear pattern yet."
                    stable++
                }
            }

            val highStressAvg = averageSymptomsFor(
                validSymptomDays.filter { (it.avgStress ?: 0.0) >= 7.0 }
            )
            val lowStressAvg = averageSymptomsFor(
                validSymptomDays.filter { (it.avgStress ?: 0.0) in 0.0..4.0 }
            )

            if (highStressAvg != null && lowStressAvg != null) {
                stressItems += "Average symptom severity on high-stress days: ${format1(highStressAvg)}."
                stressItems += "Average symptom severity on low-stress days: ${format1(lowStressAvg)}."
            }
        } else {
            stressItems += "Not enough data yet to detect a clear stress pattern."
            stable++
        }
        sections += RecentTrendsSection("Stress and Symptoms", stressItems)

        // Sleep vs symptoms
        val sleepPairs = validSymptomDays.filter { it.avgSleep != null }
        val sleepCorr = correlation(
            sleepPairs.map { it.avgSleep!! },
            sleepPairs.map { it.avgSymptomSeverity!! }
        )

        val sleepItems = mutableListOf<String>()
        if (sleepCorr != null) {
            when {
                sleepCorr <= -0.35 -> {
                    sleepItems += "Better sleep is associated with lower symptom severity."
                    quickBullets += "Better sleep appears associated with lower symptom severity."
                    improved++
                }
                sleepCorr >= 0.35 -> {
                    sleepItems += "Sleep does not show a clearly helpful pattern in this range."
                    quickBullets += "Sleep does not show a clearly helpful pattern in this range."
                    worse++
                }
                else -> {
                    sleepItems += "Sleep does not show a strong pattern yet."
                    stable++
                }
            }

            val lowSleepAvg = averageSymptomsFor(
                validSymptomDays.filter { (it.avgSleep ?: 0.0) <= 4.0 }
            )
            val goodSleepAvg = averageSymptomsFor(
                validSymptomDays.filter { (it.avgSleep ?: 0.0) >= 7.0 }
            )

            if (lowSleepAvg != null && goodSleepAvg != null) {
                sleepItems += "Average symptom severity on lower-sleep-rating days: ${format1(lowSleepAvg)}."
                sleepItems += "Average symptom severity on better-sleep-rating days: ${format1(goodSleepAvg)}."
            }
        } else {
            sleepItems += "Not enough data yet to detect a clear sleep pattern."
            stable++
        }
        sections += RecentTrendsSection("Sleep and Symptoms", sleepItems)

        // Loose stools vs symptoms
        val stoolPairs = validSymptomDays.filter { it.looseStools != null }
        val stoolCorr = correlation(
            stoolPairs.map { it.looseStools!!.toDouble() },
            stoolPairs.map { it.avgSymptomSeverity!! }
        )

        val stoolItems = mutableListOf<String>()
        if (stoolCorr != null) {
            when {
                stoolCorr >= 0.35 -> {
                    stoolItems += "More loose stools are associated with worse symptom days."
                    quickBullets += "Loose stools appear associated with worse symptom days."
                    worse++
                }
                stoolCorr <= -0.35 -> {
                    stoolItems += "Loose stools do not show a worsening pattern in this range."
                    improved++
                }
                else -> {
                    stoolItems += "Loose stools do not show a clear pattern yet."
                    stable++
                }
            }
        } else {
            stoolItems += "Not enough data yet to detect a clear digestion pattern."
            stable++
        }
        sections += RecentTrendsSection("Digestive Pattern", stoolItems)

        // Food patterns
        val foodItems = mutableListOf<String>()
        val foodPatterns = buildFoodPatterns(validSymptomDays)

        if (foodPatterns.isEmpty()) {
            foodItems += "Not enough repeated food and symptom overlap yet to identify a clear pattern."
            stable++
        } else {
            val topPatterns = foodPatterns.take(3)

            topPatterns.forEach { pattern ->
                val direction = when {
                    pattern.impact >= 0.4 -> "shows up more on higher-symptom days"
                    pattern.impact <= -0.4 -> "shows up more on lower-symptom days"
                    else -> "does not show a strong pattern yet"
                }

                val symptomPart = if (pattern.commonSymptoms.isNotEmpty()) {
                    " Common symptoms on those days: ${pattern.commonSymptoms.joinToString(", ")}."
                } else {
                    ""
                }

                foodItems += "${pattern.food} $direction (avg severity ${format1(pattern.eatenAvg)} when eaten vs ${format1(pattern.notEatenAvg)} when not eaten).$symptomPart"
            }

            val strongestFood = topPatterns.firstOrNull()
            if (strongestFood != null && abs(strongestFood.impact) >= 0.4) {
                quickBullets += if (strongestFood.impact > 0) {
                    "${strongestFood.food} appears more often on higher-symptom days."
                } else {
                    "${strongestFood.food} appears more often on lower-symptom days."
                }

                if (strongestFood.impact > 0) worse++ else improved++
            } else {
                stable++
            }
        }
        sections += RecentTrendsSection("Food and Symptoms", foodItems)

        // Reverse-direction / daily behavior patterns
        val behaviorItems = mutableListOf<String>()

        val foodCountPairs = validSymptomDays.filter { it.foodLogCount >= 0 }
        val foodCountCorr = correlation(
            foodCountPairs.map { it.foodLogCount.toDouble() },
            foodCountPairs.map { it.avgSymptomSeverity!! }
        )

        if (foodCountCorr != null) {
            when {
                foodCountCorr <= -0.35 ->
                    behaviorItems += "Higher-symptom days tend to have fewer food logs recorded."
                foodCountCorr >= 0.35 ->
                    behaviorItems += "Higher-symptom days tend to have more food logs recorded."
                else ->
                    behaviorItems += "Food logging amount does not change much on worse symptom days."
            }
        } else {
            behaviorItems += "Not enough data yet to compare symptom-heavy days with food logging amount."
        }

        val symptomStressAvg = averageOfNotNull(validSymptomDays.map { it.avgStress })
        val symptomSleepAvg = averageOfNotNull(validSymptomDays.map { it.avgSleep })
        if (symptomStressAvg != null) {
            behaviorItems += "Average stress rating across symptom days: ${format1(symptomStressAvg)}."
        }
        if (symptomSleepAvg != null) {
            behaviorItems += "Average sleep rating across symptom days: ${format1(symptomSleepAvg)}."
        }

        sections += RecentTrendsSection("Symptoms and Daily Habits", behaviorItems)

        // Contribution summary
        val contributionItems = mutableListOf<String>()
        val contributionMap = listOf(
            "Stress" to stressCorr?.let { abs(it) },
            "Sleep" to sleepCorr?.let { abs(it) },
            "Loose stools" to stoolCorr?.let { abs(it) },
            "Food logging amount" to foodCountCorr?.let { abs(it) }
        ).filter { it.second != null }
            .sortedByDescending { it.second }

        if (contributionMap.isNotEmpty()) {
            val top = contributionMap.first()
            contributionItems += "Strongest pattern in this range: ${top.first}."
            contributionMap.forEach { (name, value) ->
                val label = when {
                    value!! >= 0.60 -> "strong"
                    value >= 0.35 -> "moderate"
                    else -> "weak"
                }

                val sentence = when (label) {
                    "strong" -> "$name appears to have a strong influence in this range."
                    "moderate" -> "$name shows a noticeable influence in this range."
                    else -> "$name shows only a limited influence in this range."
                }

                contributionItems += sentence
            }
        } else {
            contributionItems += "Not enough matched data to rank contributing factors yet."
        }
        sections += RecentTrendsSection("Overall Contribution", contributionItems)

        return RecentTrendsSummary(
            quickBullets = quickBullets.distinct().take(4).ifEmpty {
                listOf("Patterns are still limited in this range, but more logs will improve the analysis.")
            },
            improved = improved,
            stable = stable,
            worse = worse,
            sections = sections
        )
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

    private fun buildDailyData(
        range: String,
        symptoms: List<SymptomEntity>,
        entries: List<WellBeingEntity>,
        foods: List<FoodEntity>
    ): List<DailyCombinedData> {
        val zone = ZoneId.systemDefault()

        val symptomsByDate = filterSymptomsForRange(symptoms, range)

        val entriesByDate = filterWellBeingForRange(entries, range, zone)
            .groupBy { Instant.ofEpochMilli(it.timestamp).atZone(zone).toLocalDate() }

        val foodsByDate = filterFoodsForRange(foods, range)
            .groupBy { parseFoodDate(it.date) }

        val allDates = (symptomsByDate.keys + entriesByDate.keys + foodsByDate.keys.filterNotNull())
            .toSortedSet()

        return allDates.map { date ->
            val daySymptoms = symptomsByDate[date].orEmpty()
            val dayEntries = entriesByDate[date].orEmpty()
            val dayFoods = foodsByDate[date].orEmpty()

            DailyCombinedData(
                date = date,
                avgSymptomSeverity = daySymptoms.map { it.severity.toDouble() }.averageOrNull(),
                symptomCount = daySymptoms.size,
                symptomNames = daySymptoms.map { it.name },
                avgStress = dayEntries.map { it.stressRating.toDouble() }.averageOrNull(),
                avgSleep = dayEntries.map { it.sleepRating.toDouble() }.averageOrNull(),
                looseStools = if (dayEntries.isNotEmpty()) dayEntries.maxByOrNull { it.timestamp }?.looseStoolsCount else null,
                foods = dayFoods.map { it.name.trim() }.filter { it.isNotBlank() },
                foodLogCount = dayFoods.size
            )
        }
    }

    private fun filterFoodsForRange(
        foods: List<FoodEntity>,
        range: String
    ): List<FoodEntity> {
        val today = LocalDate.now()
        val startOfWeek = today.with(java.time.DayOfWeek.MONDAY)
        val lastWeekStart = startOfWeek.minusWeeks(1)
        val lastWeekEnd = startOfWeek.minusDays(1)

        return foods.filter { food ->
            val d = parseFoodDate(food.date) ?: return@filter false
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

    private fun parseFoodDate(date: String): LocalDate? {
        return try {
            LocalDate.parse(date, foodDateFormatter)
        } catch (_: Exception) {
            null
        }
    }

    private fun buildFoodPatterns(days: List<DailyCombinedData>): List<FoodPattern> {
        val allFoodNames = days.flatMap { it.foods }
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()

        return allFoodNames.mapNotNull { food ->
            val eatenDays = days.filter { day ->
                day.foods.any { it.equals(food, ignoreCase = true) } && day.avgSymptomSeverity != null
            }

            val notEatenDays = days.filter { day ->
                day.foods.none { it.equals(food, ignoreCase = true) } && day.avgSymptomSeverity != null
            }

            if (eatenDays.size < 2 || notEatenDays.size < 2) return@mapNotNull null

            val eatenAvg = eatenDays.map { it.avgSymptomSeverity!! }.average()
            val notEatenAvg = notEatenDays.map { it.avgSymptomSeverity!! }.average()

            val commonSymptoms = eatenDays
                .flatMap { it.symptomNames }
                .groupingBy { it }
                .eachCount()
                .entries
                .sortedByDescending { it.value }
                .take(2)
                .map { it.key }

            FoodPattern(
                food = food,
                eatenAvg = eatenAvg,
                notEatenAvg = notEatenAvg,
                impact = eatenAvg - notEatenAvg,
                eatenDays = eatenDays.size,
                commonSymptoms = commonSymptoms
            )
        }.sortedByDescending { abs(it.impact) }
    }

    private fun averageSymptomsFor(days: List<DailyCombinedData>): Double? {
        return days.mapNotNull { it.avgSymptomSeverity }.averageOrNull()
    }

    private fun averageOfNotNull(values: List<Double?>): Double? {
        return values.filterNotNull().averageOrNull()
    }

    private fun correlation(x: List<Double>, y: List<Double>): Double? {
        if (x.size != y.size || x.size < 2) return null

        val xMean = x.average()
        val yMean = y.average()

        var numerator = 0.0
        var xDenominator = 0.0
        var yDenominator = 0.0

        for (i in x.indices) {
            val xDiff = x[i] - xMean
            val yDiff = y[i] - yMean
            numerator += xDiff * yDiff
            xDenominator += xDiff * xDiff
            yDenominator += yDiff * yDiff
        }

        val denominator = kotlin.math.sqrt(xDenominator * yDenominator)
        return if (denominator == 0.0) null else numerator / denominator
    }

    private fun List<Double>.averageOrNull(): Double? {
        return if (isEmpty()) null else average()
    }

    private fun format1(value: Double): String = String.format(java.util.Locale.getDefault(), "%.1f", value)
}