package com.jovannikolic.myapplication.ui.activity

import android.app.Application
import database.ShowsDatabase
import java.util.concurrent.Executors
import models.Review
import models.ReviewEntity
import models.Show

class MainApplication : Application() {
    val database by lazy{
        ShowsDatabase.getDatabase(this)
    }
}