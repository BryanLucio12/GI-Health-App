package com.example.gihealth.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import com.example.gihealth.data.*
import com.example.gihealth.models.PdfQuestionnaireAnswers

fun generatePdfReport(
    context: Context,
    symptoms: List<SymptomEntity>,
    answers: PdfQuestionnaireAnswers
) {
    val pdf = PdfDocument()

    val report = ReportBuilder().build(symptoms)
    val paint = Paint().apply {
        textSize = 40f
    }

    val hollowCirclePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f   // thickness of the outline
        isAntiAlias = true
    }

    // make page 1 of questionnaire
    val page1Bitmap = BitmapFactory.decodeStream(
        context.assets.open("Report_page_1.png")
    )

    val page1Info = PdfDocument.PageInfo.Builder(
        page1Bitmap.width,
        page1Bitmap.height,
        1
    ).create()

    val page1 = pdf.startPage(page1Info)
    val canvas1 = page1.canvas

    canvas1.drawBitmap(page1Bitmap, 0f, 0f, null)

    // draw answers to QUESTION 1
    report.bowelMovementsPerDay?.let { avg ->
        val label = bowelMovementLabel(avg)
        val (x, y) = bowelMovementPositions[label]!!
        canvas1.drawText("✔", x, y, paint)
    }

    // draw answers QUESTION 2
    report.avgAbdominalPain?.let { pain ->
        val x = abdominalPainPositionX[pain]!!
        val y = 2100f
        canvas1.drawCircle(x, y, 36f, hollowCirclePaint)
    }

    // answers QUESTION 3
    report.flaresPastYear?.let { count ->
        val label = flareCountLabel(count)
        val (x, y) = flareCountPositions[label]!!
        canvas1.drawText("✔", x, y, paint)
    }

    pdf.finishPage(page1)

    // draw page 2
    val page2Bitmap = BitmapFactory.decodeStream(
        context.assets.open("Report_page_2.png")
    )

    val page2Info = PdfDocument.PageInfo.Builder(
        page2Bitmap.width,
        page2Bitmap.height,
        2
    ).create()

    val page2 = pdf.startPage(page2Info)
    val canvas2 = page2.canvas

    canvas2.drawBitmap(page2Bitmap, 0f, 0f, null)

    answers.eatLess?.let { freq ->
        val x = challengeFrequencyX[freq] ?: return@let
        canvas2.drawText("✔", x, eatLessY, paint)
    }

    answers.declineSocial?.let { freq ->
        val x = challengeFrequencyX[freq] ?: return@let
        canvas2.drawText("✔", x, declineSocialY, paint)
    }

    answers.avoidActivities?.let { freq ->
        val x = challengeFrequencyX[freq] ?: return@let
        canvas2.drawText("✔", x, avoidActivitiesY, paint)
    }

    answers.arriveLateLeaveEarly?.let { freq ->
        val x = challengeFrequencyX[freq] ?: return@let
        canvas2.drawText("✔", x, arriveLateLeaveEarlyY, paint)
    }

    answers.missWorkOrSchool?.let { freq ->
        val x = challengeFrequencyX[freq] ?: return@let
        canvas2.drawText("✔", x, missWorkOrSchoolY, paint)
    }

    answers.loseSexualDesire?.let { freq ->
        val x = challengeFrequencyX[freq] ?: return@let
        canvas2.drawText("✔", x, loseSexualDesireY, paint)
    }

    answers.inBedAllOrMostOfDay?.let { freq ->
        val x = challengeFrequencyX[freq] ?: return@let
        canvas2.drawText("✔", x, InBedAllorMostOfDayY, paint)
    }



   answers.anxious?.takeIf { it == 1 }?.let {
        canvas2.drawText("✔", 1632f, 1254f, paint)
    }

    answers.depressed?.takeIf { it == 1 }?.let {
        canvas2.drawText("✔", 1632f,1130f , paint)
    }

    answers.frustrated?.takeIf { it == 1 }?.let {
        canvas2.drawText("✔", 932f, 1316f, paint)
    }

    answers.isolated?.takeIf { it == 1 }?.let {
        canvas2.drawText("✔", 932f, 1130f, paint)
    }

    answers.stressed?.takeIf { it == 1 }?.let {
        canvas2.drawText("✔", 1256f, 1130f, paint)
    }

    answers.helpless?.takeIf { it == 1 }?.let {
        canvas2.drawText("✔", 932f, 1192f, paint)
    }

    answers.overwhelmed?.takeIf { it == 1 }?.let {
        canvas2.drawText("✔", 1256f, 1192f, paint)
    }

    answers.angry?.takeIf { it == 1 }?.let {
        canvas2.drawText("✔", 1632f, 1192f, paint)
    }

    answers.sad?.takeIf { it == 1 }?.let {
        canvas2.drawText("✔", 932f, 1254f, paint)
    }

    answers.embarrassed?.takeIf { it == 1 }?.let {
        canvas2.drawText("✔", 1256f, 1254f, paint)
    }

    answers.guilty?.takeIf { it == 1 }?.let {
        canvas2.drawText("✔", 1256f, 1316f, paint)
    }

    answers.noneOfTheAbove?.takeIf { it == 1 }?.let {
        canvas2.drawText("✔", 932f, 1378f, paint)
    }

    answers.appetite?.let { value ->
        val x = appetiteX[value] ?: return@let
        canvas2.drawText("✔", x, appetiteY, paint)
    }


    answers.question9a?.let {
        if (it == 1) {
            canvas2.drawText("✔", question9X, question9aY, paint)
        }
    }



    pdf.finishPage(page2)

    // save the file
    val file = File(context.filesDir, "Health_Report.pdf")
    FileOutputStream(file).use { out ->
        pdf.writeTo(out)
    }

    pdf.close()

    Toast.makeText(context, "PDF saved: ${file.absolutePath}", Toast.LENGTH_LONG).show()
}

private fun bowelMovementLabel(value: Int): String =
    when {
        value == 0 -> "0"
        value <= 2 -> "1-2"
        value <= 5 -> "3-5"
        value <= 9 -> "7-9"
        value <= 12 -> "10-12"
        else -> "12+"
    }

private val bowelMovementPositions = mapOf(
    "0" to Pair(912f, 1750f),
    "1-2" to Pair(912f, 1815f),
    "3-5" to Pair(1312f, 1750f),
    "7-9" to Pair(1312f, 1815f),
    "10-12" to Pair(1710f, 1750f),
    "12+" to Pair(1710f, 1815f)
)

private val abdominalPainPositionX = mapOf(
    1 to 965f,
    2 to 1065f,
    3 to 1165f,
    4 to 1270f,
    5 to 1370f,
    6 to 1470f,
    7 to 1570f,
    8 to 1670f,
    9 to 1772f,
    10 to 1872f
)

private fun flareCountLabel(count: Int): String =
    when {
        count == 0 -> "0"
        count <= 2 -> "1-2"
        count <= 5 -> "3-5"
        count <= 9 -> "7-9"
        count <= 12 -> "10-12"
        else -> "12+"
    }

private val flareCountPositions = mapOf(
    "0" to Pair(912f, 2710f),
    "1-2" to Pair(912f, 2775f),
    "3-5" to Pair(1312f, 2710f),
    "7-9" to Pair(1312f, 2775f),
    "10-12" to Pair(1710f, 2710f),
    "12+" to Pair(1710f, 2775f)
)

// Section 5 – Challenges (Page 2)
private val challengeFrequencyX = mapOf(
    2 to 1710f, // Often
    1 to 1916f, // Sometimes
    0 to 2220f  // Never
)
private const val eatLessY = 419f
private const val declineSocialY = eatLessY + 63f
private const val avoidActivitiesY = declineSocialY + 63f
private const val arriveLateLeaveEarlyY = avoidActivitiesY + 63f
private const val missWorkOrSchoolY = arriveLateLeaveEarlyY + 63f
private const val loseSleepY = missWorkOrSchoolY + 62f
private const val loseSexualDesireY = loseSleepY + 62f
private const val InBedAllorMostOfDayY = loseSexualDesireY + 61f


private val appetiteX = mapOf(
    0 to 932f,
    1 to 1256f,
    2 to 1632f
)

private const val appetiteY = 1700f

private const val question9X = 932f
private const val question9aY = 1900f
