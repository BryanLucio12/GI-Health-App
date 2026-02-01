package com.example.gihealth.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import com.example.gihealth.data.*
import kotlin.math.roundToInt
//import com.example.gihealth.utils.ReportBuilder

// import used to make answers
import android.graphics.Paint

fun generatePdfReport(
    context: Context,
    symptoms: List<SymptomEntity>,
    userInfo: UserInfoEntity? = null,
    todayStressRating: Int? = null,
    weeklyAvgStressRating: Double? = null
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

    // Used for Alliance header text
    val headerPaint = Paint().apply {
        textSize = 28f
        isAntiAlias = true
        color = android.graphics.Color.BLACK
    }

    val tempDob = "08/11/2026"

    // make page 1 of questionnaire
    val page1Bitmap = BitmapFactory.decodeStream(
        context.assets.open("Report_page_1.png")
    )
    //saving info for pages 3 and 4
    val referencePageWidth = page1Bitmap.width
    val referencePageHeight = page1Bitmap.height

    val page1Info = PdfDocument.PageInfo.Builder(
        page1Bitmap.width,
        page1Bitmap.height,
        1
    ).create()

    val page1 = pdf.startPage(page1Info)
    val canvas1 = page1.canvas

    // draw the page 1 background
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

    // draw the page 2 background
    canvas2.drawBitmap(page2Bitmap, 0f, 0f, null)

    // answer QUESTION 5
    report.eatLessFrequency?.let { freq ->
        val x = challengeFrequencyX[freq] ?: return@let
        canvas2.drawText("✔", x, eatLessY, paint)
    }

    // QUESTION 5 - Decline social engagements
    report.declineSocialFrequency?.let { freq ->
        val x = challengeFrequencyX[freq] ?: return@let
        canvas2.drawText("✔", x, declineSocialY, paint)
    }

    // QUESTION 5 - Avoid activities I enjoy
    report.avoidActivitiesFrequency?.let { freq ->
        val x = challengeFrequencyX[freq] ?: return@let
        canvas2.drawText("✔", x, avoidActivitiesY, paint)
    }


    pdf.finishPage(page2)

    // page 3 (page 1 of GI Alliance form)
    val page3Bitmap = BitmapFactory.decodeStream(
        context.assets.open("alliance_page_1.png")
    )

    val page3 = pdf.startPage(
        PdfDocument.PageInfo.Builder(
            referencePageWidth,
            referencePageHeight,
            3
        ).create()
    )

    val canvas3 = page3.canvas

    val scaleFactor = 1.4f
    val offsetX3 = (referencePageWidth - page3Bitmap.width * scaleFactor) / 2f
    val offsetY3 = (referencePageHeight - page3Bitmap.height * scaleFactor) / 2f

    canvas3.save()
    canvas3.translate(offsetX3, offsetY3)
    canvas3.scale(scaleFactor, scaleFactor)

    canvas3.drawBitmap(page3Bitmap, 0f, 0f, null)

    val name = userInfo?.name?.trim().orEmpty()
    if (name.isNotBlank()) {
        canvas3.drawText(name, ALLIANCE_NAME_X, ALLIANCE_NAME_Y, headerPaint)
    }

    canvas3.drawText(
        tempDob,
        ALLIANCE_DOB_X,
        ALLIANCE_DOB_Y,
        headerPaint
    )
    todayStressRating?.let { stress ->
        val option = q1AllianceToday(stress)
        val (x, y) = ALLIANCE_TODAY_POSITIONS[option] ?: return@let
        canvas3.drawText("✔", x, y, paint)
    }
    weeklyAvgStressRating?.let { avg ->
        val option = q1AllianceWeekly(avg)
        val (x, y) = ALLIANCE_WEEKLY_POSITIONS[option] ?: return@let
        canvas3.drawText("✔", x, y, paint)
    }



    canvas3.restore()
    pdf.finishPage(page3)


    // page 4 (page 2 of GI Alliance form)
    val page4Bitmap = BitmapFactory.decodeStream(
        context.assets.open("alliance_page_2.png")
    )

    val page4Info = PdfDocument.PageInfo.Builder(
        referencePageWidth,
        referencePageHeight,
        4
    ).create()

    val page4 = pdf.startPage(page4Info)
    val canvas4 = page4.canvas

    // center samller bitmap of page 4 inside full size pdf page of 1 and 2

    val offsetX4 = (referencePageWidth - page4Bitmap.width*scaleFactor) / 2f
    val offsetY4 = (referencePageHeight - page4Bitmap.height*scaleFactor) / 2f
    canvas4.drawBitmap(page3Bitmap, offsetX4, offsetY4, null)

    canvas4.save()                     // save current canvas state
    canvas4.scale(scaleFactor, scaleFactor)
    canvas4.drawBitmap(page4Bitmap, offsetX4 / scaleFactor, offsetY4 / scaleFactor, null)
    canvas4.restore()

    pdf.finishPage(page4)





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

// the pairs are in x and y coords for the PDF pages (question 1)
// use these coords as reference for future questions, specifically x coords
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

// Section 5 - Challenges (Page 2)
private val challengeFrequencyX = mapOf(
    2 to 1710f, // Often
    1 to 1916f, // Sometimes
    0 to 2220f  // Never
)


private fun q1AllianceToday(stressRating: Int): String {
    val s = stressRating.coerceIn(1, 10)
    return when (s) {
        1, 2 -> "very well"
        3, 4 -> "slightly below par"
        5, 6 -> "poor"
        7, 8 -> "very poor"
        else -> "terrible" // 9-10
    }
}

private val ALLIANCE_TODAY_POSITIONS = mapOf(
    "very well" to Pair(267f, 390f),
    "slightly below par" to Pair(267f, 435f),
    "poor" to Pair(267f, 480f),
    "very poor" to Pair(267f, 522f),
    "terrible" to Pair(267f, 567f)
)
private val ALLIANCE_WEEKLY_POSITIONS = mapOf(
    "very well" to Pair(1005f, 390f),
    "slightly below par" to Pair(1005f, 435f),
    "poor" to Pair(1005f, 480f),
    "very poor" to Pair(1005f, 522f),
    "terrible" to Pair(1005f, 567f)
)
private fun q1AllianceWeekly(avg: Double): String {
    val a = avg.coerceIn(1.0, 10.0)
    return when {
        a < 2.5 -> "very well"
        a < 4.5 -> "slightly below par"
        a < 6.5 -> "poor"
        a < 8.5 -> "very poor"
        else -> "terrible"
    }
}

private const val eatLessY = 419f
private const val declineSocialY = eatLessY + 63f
private const val avoidActivitiesY = declineSocialY + 63f
private const val ALLIANCE_NAME_X = 395f
private const val ALLIANCE_NAME_Y = 290f
private const val ALLIANCE_DOB_X = 1200f
private const val ALLIANCE_DOB_Y = 290f