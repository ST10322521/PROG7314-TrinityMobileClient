package Emeris.PROG7314.trinitymobileclient

import Emeris.PROG7314.trinitymobileclient.settings.ThemeManager
import Emeris.PROG7314.trinitymobileclient.settings.Themes
import androidx.appcompat.app.AppCompatDelegate
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

// saved auto-lock works straight away. sign out puts the theme back to system
@RunWith(AndroidJUnit4::class)
class AutoLockAndThemeTest {

    @Test
    fun applyAutoLock_updatesStoredMinutes() {
        ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                activity.applyAutoLock(0)
                assertEquals(0, activity.autoLockMinutesForTest())

                activity.applyAutoLock(5)
                assertEquals(5, activity.autoLockMinutesForTest())
            }
        }
    }

    @Test
    fun signOutThemeReset_returnsToFollowSystem() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        ThemeManager.applyTheme(Themes.Auto)

        assertEquals(
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
            AppCompatDelegate.getDefaultNightMode()
        )
    }
}
