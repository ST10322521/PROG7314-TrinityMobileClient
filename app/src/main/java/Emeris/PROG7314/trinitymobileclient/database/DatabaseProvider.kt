package Emeris.PROG7314.trinitymobileclient.database

import android.content.Context
import androidx.room.Room

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
            ).addMigrations(MIGRATION_1_2).build().also { INSTANCE = it }
        }
    }
}