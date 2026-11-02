package Emeris.PROG7314.trinitymobileclient.database

import androidx.room3.migration.Migration
import androidx.sqlite.execSQL

//https://developer.android.com/training/data-storage/room/migrating-db-versions
//v1 -> v2: adds language, biometric and notification settings.
//Existing rows keep their theme and auto-lock and get the defaults for the new columns.
val MIGRATION_1_2 = Migration(1, 2) { connection ->
    connection.execSQL("ALTER TABLE user_preferences ADD COLUMN language TEXT NOT NULL DEFAULT 'en'")
    connection.execSQL("ALTER TABLE user_preferences ADD COLUMN biometricEnabled INTEGER NOT NULL DEFAULT 0")
    connection.execSQL("ALTER TABLE user_preferences ADD COLUMN notificationsEnabled INTEGER NOT NULL DEFAULT 1")
}
