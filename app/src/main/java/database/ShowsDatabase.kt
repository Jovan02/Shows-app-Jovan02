package database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import models.Review
import models.ReviewEntity
import models.Show

@Database(
    entities = [
        Show::class,
        ReviewEntity::class
    ],
    version = 2
)

abstract class ShowsDatabase : RoomDatabase(){

    companion object {

        @Volatile
        private var INSTANCE: ShowsDatabase? = null

        fun getDatabase(context: Context): ShowsDatabase {
            return INSTANCE ?: synchronized(this) {
                val database = Room.databaseBuilder(
                    context,
                    ShowsDatabase::class.java,
                    "show_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = database
                database
            }
        }
    }
    abstract fun showDao(): ShowDao
    abstract fun reviewDao(): ReviewDao
}