package com.example.myapplication

import android.app.Application
import com.google.android.material.color.DynamicColors

class Notes: Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}