package com.example.gihealth.utils

import com.example.gihealth.data.*
import kotlin.math.roundToInt


class ReportBuilder {

    fun build(symptoms: List<SymptomEntity>): PDFReport {

        // question 1
        val bowelRows = symptoms.filter {
            it.name == "Bowel Movement"
        }

        // q1 -  Compute average per day over the period
        val avgBowelMovements = if (bowelRows.isNotEmpty()) {
            bowelRows
                .map { it.severity }
                .average()
                .roundToInt()
        } else {
            0
        }

        // return PDF-ready model
        return PDFReport(
            bowelMovementsPerDay = avgBowelMovements
        )
    }
}