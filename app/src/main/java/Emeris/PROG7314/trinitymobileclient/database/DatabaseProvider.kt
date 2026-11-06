package Emeris.PROG7314.trinitymobileclient.database

import android.content.Context
import androidx.room3.Room

/**
 * developer.android.com/training/data-storage/room
 */
object DatabaseProvider {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE?: synchronized(this) {
            INSTANCE?: Room.databaseBuilder<AppDatabase>(
                context.applicationContext,
                "trinity_database"
            ).build().also { INSTANCE = it }
        }
    }
}