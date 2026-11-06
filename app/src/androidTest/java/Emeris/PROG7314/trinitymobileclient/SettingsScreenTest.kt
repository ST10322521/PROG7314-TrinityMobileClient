package Emeris.PROG7314.trinitymobileclient

import Emeris.PROG7314.trinitymobileclient.auth.AuthProvider
import Emeris.PROG7314.trinitymobileclient.settings.SettingsDefaults
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

// Settings screen checks. no Espresso, its broken on API 37. so we use performClick()
@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {

    private fun openSettings(block: (HomeActivity) -> Unit) {
        ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                activity.findViewById<View>(R.id.nav_settings).performClick()
                activity.supportFragmentManager.executePendingTransactions()
                block(activity)
            }
        }
    }

    @Test
    fun settingsScreen_opensFromDrawer() {
        openSettings { activity ->
            val fragment = activity.supportFragmentManager.findFragmentById(R.id.content_container)
            assertTrue(fragment is SettingsFragment)
        }
    }

    @Test
    fun preferenceRows_showRealValues() {
        openSettings { activity ->
            val language = activity.findViewById<TextView>(R.id.tvLanguage).text.toString()
            val autoLock = activity.findViewById<TextView>(R.id.tvAutoLock).text.toString()

            assertTrue(
                "Language should be one of the supported languages, was '$language'",
                language in SettingsDefaults.languageOptions.keys
            )
            assertTrue(
                "Auto-lock should be one of the options, was '$autoLock'",
                autoLock in SettingsDefaults.autoLockOptions.keys
            )
        }
    }

    @Test
    fun toggles_canBeSwitched() {
        openSettings { activity ->
            val biometric = activity.findViewById<SwitchCompat>(R.id.switch_biometric)
            val notifications = activity.findViewById<SwitchCompat>(R.id.switch_notifications)

            val biometricBefore = biometric.isChecked
            biometric.performClick()
            assertEquals(!biometricBefore, biometric.isChecked)

            val notificationsBefore = notifications.isChecked
            notifications.performClick()
            assertEquals(!notificationsBefore, notifications.isChecked)
        }
    }

    @Test
    fun accountCard_isNeverBlank() {
        openSettings { activity ->
            val name = activity.findViewById<TextView>(R.id.tv_account_name)
            val initials = activity.findViewById<TextView>(R.id.tv_account_initials)
            val signOut = activity.findViewById<View>(R.id.btn_sign_out)

            assertTrue(name.text.isNotBlank())
            assertTrue(initials.text.isNotBlank())

            val signedIn = AuthProvider.repository().currentUserId() != null
            assertEquals(signedIn, signOut.visibility == View.VISIBLE)
        }
    }

    @Test
    fun aboutCard_showsVersion() {
        openSettings { activity ->
            val version = activity.findViewById<TextView>(R.id.tv_app_version).text.toString()
            assertTrue("Version text was '$version'", version.contains("1.0"))
        }
    }

    @Test
    fun categoryRow_highlightsWhenTapped() {
        openSettings { activity ->
            val general = activity.findViewById<View>(R.id.cat_general)
            val about = activity.findViewById<View>(R.id.cat_about)

            assertTrue("General starts selected", general.isSelected)

            about.performClick()

            assertTrue("About should be selected after tapping it", about.isSelected)
            assertFalse("General should no longer be selected", general.isSelected)
        }
    }

    private fun withLoadedSettings(block: (ActivityScenario<HomeActivity>) -> Unit) {
        ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                activity.findViewById<View>(R.id.nav_settings).performClick()
                activity.supportFragmentManager.executePendingTransactions()
            }
            Thread.sleep(1500)
            block(scenario)
        }
    }

    private fun settingsOf(activity: HomeActivity) =
        activity.supportFragmentManager.findFragmentById(R.id.content_container) as SettingsFragment

    @Test
    fun unsavedChanges_trackedAgainstSavedValues() {
        withLoadedSettings { scenario ->
            scenario.onActivity { activity ->
                val settings = settingsOf(activity)
                val biometric = activity.findViewById<SwitchCompat>(R.id.switch_biometric)

                assertFalse("Nothing changed yet", settings.hasUnsavedChanges())
                biometric.performClick()
                assertTrue("Flipped a switch", settings.hasUnsavedChanges())
                biometric.performClick()
                assertFalse("Flipped it back", settings.hasUnsavedChanges())
            }
        }
    }

    @Test
    fun leavingWithUnsavedChanges_staysOnSettingsUntilConfirmed() {
        withLoadedSettings { scenario ->
            scenario.onActivity { activity ->
                activity.findViewById<View>(R.id.switch_biometric).performClick()
                activity.findViewById<View>(R.id.nav_dashboard).performClick()
                activity.supportFragmentManager.executePendingTransactions()

                assertTrue(
                    activity.supportFragmentManager.findFragmentById(R.id.content_container)
                        is SettingsFragment
                )
            }
        }
    }

    @Test
    fun leavingWithoutChanges_navigatesStraightAway() {
        withLoadedSettings { scenario ->
            scenario.onActivity { activity ->
                activity.findViewById<View>(R.id.nav_dashboard).performClick()
                activity.supportFragmentManager.executePendingTransactions()

                assertTrue(
                    activity.supportFragmentManager.findFragmentById(R.id.content_container)
                        is DashboardFragment
                )
            }
        }
    }

    @Test
    fun rebuild_keepsCategoryAndUnsavedPicks() {
        withLoadedSettings { scenario ->
            var biometricBefore = false
            scenario.onActivity { activity ->
                activity.findViewById<View>(R.id.cat_about).performClick()
                val biometric = activity.findViewById<SwitchCompat>(R.id.switch_biometric)
                biometric.performClick()
                biometricBefore = biometric.isChecked
            }

            scenario.recreate()
            Thread.sleep(1500)

            scenario.onActivity { activity ->
                assertTrue(
                    "About should still be highlighted",
                    activity.findViewById<View>(R.id.cat_about).isSelected
                )
                assertEquals(
                    "Unsaved switch change should survive",
                    biometricBefore,
                    activity.findViewById<SwitchCompat>(R.id.switch_biometric).isChecked
                )
                assertTrue("Still counts as unsaved", settingsOf(activity).hasUnsavedChanges())
            }
        }
    }

    @Test
    fun signedOut_accountCardExplainsWhy() {
        openSettings { activity ->
            if (AuthProvider.repository().currentUserId() != null) return@openSettings

            val email = activity.findViewById<TextView>(R.id.tv_account_email).text.toString()
            assertEquals(activity.getString(R.string.settings_account_signed_out_hint), email)
        }
    }
}
