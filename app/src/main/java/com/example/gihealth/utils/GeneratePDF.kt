package com.example.gihealth.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
// import used to make answers
// import android.graphics.Paint

fun generatePdfReport(context: Context) {
    val pdf = PdfDocument()

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

    // Note of how to make answers to the questionnaire
    // val paint = Paint().apply { textSize = 40f }
    // canvas1.drawText("User Answer", x, y, paint)

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

    pdf.finishPage(page2)

    // save the file
    val file = File(context.filesDir, "Health_Report.pdf")
    FileOutputStream(file).use { out ->
        pdf.writeTo(out)
    }

    pdf.close()

    Toast.makeText(context, "PDF saved: ${file.absolutePath}", Toast.LENGTH_LONG).show()
}