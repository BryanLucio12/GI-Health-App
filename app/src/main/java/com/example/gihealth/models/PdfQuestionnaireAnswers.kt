package com.example.gihealth.models

data class PdfQuestionnaireAnswers(

    // USER Question 1
    val eatLess: Int?,
    val declineSocial: Int?,
    val avoidActivities: Int?,
    val arriveLateLeaveEarly: Int?,
    val missWorkOrSchool: Int?,
    val loseSexualDesire: Int?,
    val inBedAllOrMostOfDay: Int?,

    // USER Question 2
    val anxious: Int?,
    val depressed: Int?,
    val frustrated: Int?,
    val isolated: Int?,
    val stressed: Int?,
    val helpless: Int?,
    val overwhelmed: Int?,
    val angry: Int?,
    val sad: Int?,
    val embarrassed: Int?,
    val guilty: Int?,
    val noneOfTheAbove: Int?,

    // USER Question 3
    val appetite: Int?,

    // USER Question 4
    val question9a: Int?,
    val question9b: String?,
    val question9c: String?,

    // USER Question 5
    val question10a: String?,
)