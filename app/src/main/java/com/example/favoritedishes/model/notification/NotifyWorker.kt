package com.example.favoritedishes.model.notification

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotifyWorker(context: Context, workerParams: WorkerParameters): Worker(context, workerParams) {
    override fun doWork(): Result {
        Log.i("Notify worker", "doWork function is called")

        return Result.success()
    }

}