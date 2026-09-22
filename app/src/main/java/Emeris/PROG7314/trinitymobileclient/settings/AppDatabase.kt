package Emeris.PROG7314.trinitymobileclient.settings

import androidx.room3.Database
import androidx.room3.RoomDatabase

/**
 * developer.android.com/training/data-storage/room
 */
@Database(
    entities = [UserPreferences::class],
    version = 1
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userPreferencesDao(): UserPreferencesDao
}