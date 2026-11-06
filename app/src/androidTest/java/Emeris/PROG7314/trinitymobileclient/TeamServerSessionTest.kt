package Emeris.PROG7314.trinitymobileclient

import Emeris.PROG7314.trinitymobileclient.auth.AuthProvider
import Emeris.PROG7314.trinitymobileclient.auth.TeamServerSession
import Emeris.PROG7314.trinitymobileclient.database.AppDatabase
import Emeris.PROG7314.trinitymobileclient.settings.SettingsRepository
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TeamServerSessionTest {

    private lateinit var context: Context
    private lateinit var database: AppDatabase

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        FirebaseAuth.getInstance().signOut()
        database = Room.inMemoryDatabaseBuilder<AppDatabase>(context).build()
        TeamServerSession.clear()
    }

    @After
    fun tearDown() {
        TeamServerSession.clear()
        database.close()
    }

    @Test
    fun teamServerLogin_isTheSignedInUser() {
        TeamServerSession.start(context, "operator1")
        val auth = AuthProvider.repository()

        assertEquals("teamserver:operator1", auth.currentUserId())
        assertEquals("operator1", auth.currentUserUsername())
    }

    @Test
    fun teamServerLogin_canSaveAndReloadSettings() = runBlocking {
        TeamServerSession.start(context, "operator1")
        val settings = SettingsRepository(AuthProvider.repository(), database)

        val saved = settings.savePreferences(
            theme = "Dark",
            autoLockMinutes = 10,
            language = "zu",
            biometricEnabled = true,
            notificationsEnabled = false
        )
        val loaded = settings.getPreferences()

        assertTrue(saved)
        assertEquals("teamserver:operator1", loaded?.userId)
        assertEquals("Dark", loaded?.theme)
        assertEquals(10, loaded?.autoLockMinutes)
        assertEquals("zu", loaded?.language)
    }

    @Test
    fun logout_endsTheTeamServerSession() {
        TeamServerSession.start(context, "operator1")
        AuthProvider.repository().logout()

        assertNull(AuthProvider.repository().currentUserId())
        assertNull(TeamServerSession.username)
    }
}
