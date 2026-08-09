package com.finaudit

import android.app.Application
import com.finaudit.service.ForegroundCaptureService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FinAuditApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Automatically start background scans
        ForegroundCaptureService.start(this)
    }
}
