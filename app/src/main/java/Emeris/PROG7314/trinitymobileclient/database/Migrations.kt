package Emeris.PROG7314.trinitymobileclient.database

import androidx.room.migration.Migration
import androidx.sqlite.execSQL

val MIGRATION_1_2 = Migration(1, 2) { connection ->
    connection.execSQL("ALTER TABLE user_preferences ADD COLUMN language TEXT NOT NULL DEFAULT 'en'")
    connection.execSQL("ALTER TABLE user_preferences ADD COLUMN biometricEnabled INTEGER NOT NULL DEFAULT 0")
    connection.execSQL("ALTER TABLE user_preferences ADD COLUMN notificationsEnabled INTEGER NOT NULL DEFAULT 1")
}
