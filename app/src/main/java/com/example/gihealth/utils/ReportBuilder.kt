package com.example.gihealth.utils

import com.example.gihealth.data.*
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


        // return PDF-ready model
        return PDFReport(
            bowelMovementsPerDay = avgBowelMovements,
            avgAbdominalPain = avgAbdominalPain,
            flaresPastYear = flaresPastYear
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
            java.time.Instant.ofEpochMilli(symptom.timestamp)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()
        }

        return symptomsByDay.count { (_, dailySymptoms) ->
            dailySymptoms.map { it.name }.distinct().size >= 2
        }
    }


}