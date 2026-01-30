package com.example.gihealth.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import com.example.gihealth.data.*
//import com.example.gihealth.utils.ReportBuilder

// import used to make answers
import android.graphics.Paint

fun generatePdfReport(
    context: Context,
    symptoms: List<SymptomEntity>
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

    val page3Info = PdfDocument.PageInfo.Builder(
        page3Bitmap.width,
        page3Bitmap.height,
        3
    ).create()

    val page3 = pdf.startPage(page3Info)
    val canvas3 = page3.canvas

    // draw the page 3 background
    canvas3.drawBitmap(page3Bitmap, 0f, 0f, null)

    pdf.finishPage(page3)

    // page 4 (page 2 of GI Alliance form)
    val page4Bitmap = BitmapFactory.decodeStream(
        context.assets.open("alliance_page_2.png")
    )

    val page4Info = PdfDocument.PageInfo.Builder(
        page4Bitmap.width,
        page4Bitmap.height,
        4
    ).create()

    val page4 = pdf.startPage(page4Info)
    val canvas4 = page4.canvas

    // draw the page 3 background
    canvas4.drawBitmap(page4Bitmap, 0f, 0f, null)

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
private const val eatLessY = 419f
private const val declineSocialY = eatLessY + 63f
private const val avoidActivitiesY = declineSocialY + 63f
