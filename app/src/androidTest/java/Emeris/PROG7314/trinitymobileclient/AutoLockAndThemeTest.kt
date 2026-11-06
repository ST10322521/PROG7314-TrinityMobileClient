package Emeris.PROG7314.trinitymobileclient

import Emeris.PROG7314.trinitymobileclient.settings.ThemeManager
import Emeris.PROG7314.trinitymobileclient.settings.Themes
import androidx.appcompat.app.AppCompatDelegate
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

//Checks that a saved auto-lock time takes effect straight away and that the
//theme reset used on sign out goes back to following the system theme.
//No Espresso (broken on API 37), same pattern as the other UI tests.
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

        //the same reset call that SettingsFragment.signOut() ends with
        ThemeManager.applyTheme(Themes.Auto)

        assertEquals(
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
            AppCompatDelegate.getDefaultNightMode()
        )
    }
}
