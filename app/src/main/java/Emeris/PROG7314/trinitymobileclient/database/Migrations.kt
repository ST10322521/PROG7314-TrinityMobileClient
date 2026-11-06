package Emeris.PROG7314.trinitymobileclient.database

import androidx.room.migration.Migration
import androidx.sqlite.execSQL

// v1 -> v2: adds the 3 new settings, old rows keep there theme and auto-lock
// https://developer.android.com/training/data-storage/room/migrating-db-versions
val MIGRATION_1_2 = Migration(1, 2) { connection ->
    connection.execSQL("ALTER TABLE user_preferences ADD COLUMN language TEXT NOT NULL DEFAULT 'en'")
    connection.execSQL("ALTER TABLE user_preferences ADD COLUMN biometricEnabled INTEGER NOT NULL DEFAULT 0")
    connection.execSQL("ALTER TABLE user_preferences ADD COLUMN notificationsEnabled INTEGER NOT NULL DEFAULT 1")
}
