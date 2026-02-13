package com.example.gihealth.utils

data class PDFReport(
    val bowelMovementsPerDay: Int?,
    val avgAbdominalPain: Int?,
    val flaresPastYear: Int?,
    val rectalBleedingFrequency: Int?,
    val nauseaChange: Int?,
    val weightChange: Int?,
    val weightDeltaLbs: Int?,
    val todayAbdominalPain: Float?,
    val weeklyAvgAbdominalPain: Float?,
    val todayStressRating: Float?,
    val weeklyAvgStressRating: Float?
)
