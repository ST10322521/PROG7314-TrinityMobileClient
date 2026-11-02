package Emeris.PROG7314.trinitymobileclient

import Emeris.PROG7314.trinitymobileclient.database.AppDatabase
import Emeris.PROG7314.trinitymobileclient.settings.UserPreferences
import android.content.Context
import androidx.room3.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

//Saves and reloads every user setting through Room, using an in-memory database.
//https://developer.android.com/training/data-storage/room/testing-db
@RunWith(AndroidJUnit4::class)
class UserPreferencesDaoTest {

    private lateinit var database: AppDatabase

    @Before
    fun openDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder<AppDatabase>(context).build()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun savedSettings_areReadBack() = runBlocking {
        val dao = database.userPreferencesDao()
        val saved = UserPreferences(
            userId = "user-1",
            theme = "Light",
            autoLockMinutes = 10,
            language = "zu",
            biometricEnabled = true,
            notificationsEnabled = false
        )

        dao.savePreferences(saved)

        assertEquals(saved, dao.getPreferences("user-1"))
    }

    @Test
    fun savingAgain_updatesTheSameUser() = runBlocking {
        val dao = database.userPreferencesDao()
        dao.savePreferences(UserPreferences("user-1", "Dark", 5, "en", false, true))
        dao.savePreferences(UserPreferences("user-1", "Dark", 5, "af", true, true))

        val loaded = dao.getPreferences("user-1")!!
        assertEquals("af", loaded.language)
        assertEquals(true, loaded.biometricEnabled)
    }

    @Test
    fun unknownUser_hasNoSettings() = runBlocking {
        assertNull(database.userPreferencesDao().getPreferences("nobody"))
    }
}
