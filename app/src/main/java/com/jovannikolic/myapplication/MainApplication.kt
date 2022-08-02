package com.jovannikolic.myapplication

import android.app.Application
import database.ShowsDatabase
import java.util.concurrent.Executors
import models.Show

class MainApplication : Application() {

    val database by lazy{
        ShowsDatabase.getDatabase(this)
    }

    private val shows = emptyList<Show>()

    override fun onCreate() {
        super.onCreate()
        Executors.newSingleThreadExecutor().execute{
            database.showDao().insertAllShows(shows)
        }
    }
}