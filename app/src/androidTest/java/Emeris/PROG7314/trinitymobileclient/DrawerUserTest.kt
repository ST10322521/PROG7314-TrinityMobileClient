package Emeris.PROG7314.trinitymobileclient

import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DrawerUserTest {

    @Test
    fun drawerFooter_isPopulated_whenNoUserIsSignedIn() {
        ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val username = activity.findViewById<TextView>(R.id.tvUsername)
                val initials = activity.findViewById<TextView>(R.id.tvAvatarInitials)

                assertTrue(
                    "Username field should never be blank",
                    username.text.isNotBlank()
                )
                assertTrue(
                    "Avatar initials should never be blank",
                    initials.text.isNotBlank()
                )
            }
        }
    }

    @Test
    fun drawerFooter_doesNotShowLayoutPlaceholders() {
        ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val username = activity.findViewById<TextView>(R.id.tvUsername)

                assertFalse(
                    "Drawer is still showing the layout placeholder text",
                    username.text.toString() == "User Name"
                )
            }
        }
    }

    @Test
    fun drawerFooter_survivesNavigation() {
        ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                activity.findViewById<android.view.View>(R.id.nav_settings).performClick()
                activity.supportFragmentManager.executePendingTransactions()

                val username = activity.findViewById<TextView>(R.id.tvUsername)
                assertTrue(
                    "Username field should still be populated after navigating",
                    username.text.isNotBlank()
                )
            }
        }
    }
}
