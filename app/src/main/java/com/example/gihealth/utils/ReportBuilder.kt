package com.example.gihealth.utils

import com.example.gihealth.data.*
import java.time.Instant
import java.time.ZoneId
import kotlin.math.roundToInt

const val FLARE_SEVERITY_THRESHOLD = 3

class ReportBuilder {

    fun build(symptoms: List<SymptomEntity>): PDFReport {

        // question 1
        val bowelRows = symptoms.filter {
            it.name == "Bowel Movement"
        }

        val abdominalPainRows = symptoms.filter {
            it.name == "Abdominal pain"
        }

        // QUESTION 1 -  Compute average per day over the period
        val avgBowelMovements = if (bowelRows.isNotEmpty()) {
            bowelRows
                .map { it.severity }
                .average()
                .roundToInt()
        } else {
            0
        }

        // QUESTION 2 - Compute average severity
        val avgAbdominalPain = if (abdominalPainRows.isNotEmpty()) {
            abdominalPainRows.map { it.severity }.average().roundToInt()
                .coerceIn(1, 10)
        } else {
            1
        }

        // QUESTION 3 - Compute number of flares
        val flaresPastYear = countFlaresPastYear(symptoms)

        val rectalBleedingFrequency = computeRectalBleedingFrequency(symptoms)

        // QUESTION 7 - NAUSEA
        val nauseaRows = symptoms.filter { it.name == "Nausea" }

        val nauseaChange = if (nauseaRows.size >= 2) {
            val first = nauseaRows.first().severity
            val last = nauseaRows.last().severity

            when {
                last > first -> 0   // Increased
                last < first -> 1   // Decreased
                else -> 2           // Stayed the same
            }
        } else {
            null
        }

        // QUESTION 7 - WEIGHT
        val weightRows = symptoms
            .filter { it.name == "Weight" }
            .sortedBy { it.timestamp }

        val weightChange: Int?
        val weightDelta: Int?

        if (weightRows.size >= 2) {
            val first = weightRows.first().severity.toFloat()
            val last = weightRows.last().severity.toFloat()

            weightChange = when {
                last > first -> 0   // Increased
                last < first -> 1   // Decreased
                else -> 2           // Stayed the Same
            }

            weightDelta = kotlin.math.abs(last - first).roundToInt()
        } else {
            weightChange = null
            weightDelta = null
        }

        // return PDF ready model
        return PDFReport(
            bowelMovementsPerDay = avgBowelMovements,
            avgAbdominalPain = avgAbdominalPain,
            flaresPastYear = flaresPastYear,
            rectalBleedingFrequency = rectalBleedingFrequency,
            nauseaChange = nauseaChange,
            weightChange = weightChange,
            weightDeltaLbs = weightDelta
        )
    }

    val flareCoreSymptoms = setOf(
        "Abdominal pain",
        "Bowel Movement",
        "Diarrhea",
        "Blood in stool",
        "Incontinence of stool",
        "Loss of appetite",
        "Fatigue",
        "Fever",
        "Night sweats",
        "Weight loss"
    )

    // a flare will count if a symptom has above severity 3 for a day
    private fun countFlaresPastYear(symptoms: List<SymptomEntity>): Int {

        val oneYearAgo = System.currentTimeMillis() - 365L * 24 * 60 * 60 * 1000

        val flareSymptoms = symptoms
            .filter {
                it.timestamp >= oneYearAgo &&
                        it.name in flareCoreSymptoms &&
                        it.severity >= FLARE_SEVERITY_THRESHOLD
            }

        val symptomsByDay = flareSymptoms.groupBy { symptom ->
            Instant.ofEpochMilli(symptom.timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }

        return symptomsByDay.count { (_, dailySymptoms) ->
            dailySymptoms.map { it.name }.distinct().size >= 2
        }
    }

    // QUESTION 4
    private fun computeRectalBleedingFrequency(symptoms: List<SymptomEntity>): Int {
        val now = System.currentTimeMillis()
        val thirtyDaysAgo = now - 30L * 24 * 60 * 60 * 1000

        val bleedingLogs = symptoms.filter {
            it.name == "Blood in stool" && it.timestamp >= thirtyDaysAgo
        }

        if (bleedingLogs.isEmpty()) return 0 // Never

        val daysWithBleeding = bleedingLogs
            .groupBy {
                Instant.ofEpochMilli(it.timestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            }
            .size

        val totalDays = 30
        val ratio = daysWithBleeding.toDouble() / totalDays

        return when {
            ratio == 0.0 -> 0          // Never
            ratio <= 0.25 -> 1         // Trace
            ratio <= 0.50 -> 2         // Occasionally
            else -> 3                  // Usually
        }
    }


}