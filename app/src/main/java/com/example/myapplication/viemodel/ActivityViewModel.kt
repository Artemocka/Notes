package com.example.myapplication.viemodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.DatabaseProviderWrap

class ActivityViewModel(app: Application) : AndroidViewModel(app){
    init {
        DatabaseProviderWrap.createDao(app)
    }
}