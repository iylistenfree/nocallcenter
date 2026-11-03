package com.nocallcenter.app

import android.app.Application
import com.nocallcenter.app.util.NotificationHelper

class NoCallCenterApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize notification channels
        NotificationHelper(this)
    }
}
