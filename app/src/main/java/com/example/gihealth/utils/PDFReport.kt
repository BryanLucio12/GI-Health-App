package com.example.gihealth.utils

data class PDFReport(
    val bowelMovementsPerDay: Int?,
    val avgAbdominalPain: Int?,
    val flaresPastYear: Int?,
    val eatLessFrequency: Int?,
    val declineSocialFrequency: Int?,
    val avoidActivitiesFrequency: Int?
)
