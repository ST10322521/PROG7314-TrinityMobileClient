package Emeris.PROG7314.trinitymobileclient

import Emeris.PROG7314.trinitymobileclient.database.AppDatabase
import Emeris.PROG7314.trinitymobileclient.database.MIGRATION_1_2
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsMigrationTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val dbName = "settings-migration-test"

    @Before
    fun createVersion1Database() {
        context.deleteDatabase(dbName)

        val db = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(dbName), null)
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `user_preferences` (`userId` TEXT NOT NULL, " +
                "`theme` TEXT NOT NULL, `autoLockMinutes` INTEGER NOT NULL, PRIMARY KEY(`userId`))"
        )
        db.execSQL("INSERT INTO user_preferences VALUES ('user-1', 'Dark', 15)")
        db.version = 1
        db.close()
    }

    @After
    fun cleanUp() {
        context.deleteDatabase(dbName)
    }

    @Test
    fun migration1To2_keepsExistingSettings_andAddsDefaults() = runBlocking {
        val database = Room.databaseBuilder<AppDatabase>(context, context.getDatabasePath(dbName).path)
            .addMigrations(MIGRATION_1_2)
            .build()

        val preferences = database.userPreferencesDao().getPreferences("user-1")
        database.close()

        assertNotNull("Row should survive the migration", preferences)
        assertEquals("Dark", preferences!!.theme)
        assertEquals(15, preferences.autoLockMinutes)
        assertEquals("en", preferences.language)
        assertEquals(false, preferences.biometricEnabled)
        assertEquals(true, preferences.notificationsEnabled)
    }
}
