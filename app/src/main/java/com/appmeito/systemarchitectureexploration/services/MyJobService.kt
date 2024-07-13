package com.appmeito.systemarchitectureexploration.services

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log

class MyJobService : JobService() {

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d("MyJobService", "Job started")

        // Perform your background work here

        return true // Return true if the job is still running
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d("MyJobService", "Job stopped")
        return true // Return true to reschedule the job
    }
}
