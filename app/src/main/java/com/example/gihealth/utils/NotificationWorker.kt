package com.example.gihealth.utils

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.Data

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val title = inputData.getString("title") ?: "Reminder"
        val message = inputData.getString("message") ?: "Don't forget to log your health data!"
        val id = inputData.getInt("id", 1)

        NotificationHelper.showNotification(applicationContext, title, message, id)
        return Result.success()
    }
}
