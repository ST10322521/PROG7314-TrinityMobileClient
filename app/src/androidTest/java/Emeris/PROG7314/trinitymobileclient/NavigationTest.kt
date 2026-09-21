package Emeris.PROG7314.trinitymobileclient

import android.view.View
import androidx.fragment.app.Fragment
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI/navigation tests for the app shell. Each test launches HomeActivity and
 * navigates through the real navigation-drawer click handlers, then asserts the
 * expected fragment is the one showing in the content container.
 *
 * This is the regression test for the fragment-binding crash: constructing the
 * Listeners, Payloads, Logs or Settings fragment used to throw a
 * NullPointerException, so navigating to any of those screens crashed the app.
 *
 * Navigation is driven with View.performClick() on the UI thread (firing the
 * exact OnClickListener the drawer wires up) and screens are verified through the
 * FragmentManager. Espresso is intentionally not used: its event-injection layer
 * is incompatible with the API 37 emulator, and this approach is faster and
 * runs on any API level or in CI.
 */
@RunWith(AndroidJUnit4::class)
class NavigationTest {

    // Fires a drawer item's real click handler and lets the transaction complete.
    private fun ActivityScenario<HomeActivity>.navigateVia(navItemId: Int) {
        onActivity { activity ->
            activity.findViewById<View>(navItemId).performClick()
            activity.supportFragmentManager.executePendingTransactions()
        }
    }

    // Asserts the fragment currently in the content container is of the given type.
    private fun ActivityScenario<HomeActivity>.assertScreen(expected: Class<out Fragment>) {
        onActivity { activity ->
            activity.supportFragmentManager.executePendingTransactions()
            val current = activity.supportFragmentManager.findFragmentById(R.id.content_container)
            assertNotNull("No fragment is showing in the content container", current)
            assertTrue(
                "Expected ${expected.simpleName} but was ${current?.javaClass?.simpleName}",
                expected.isInstance(current)
            )
        }
    }

    @Test
    fun appLaunches_showsDashboard() {
        ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
            scenario.assertScreen(DashboardFragment::class.java)
        }
    }

    @Test
    fun navigateToListeners_screenIsShown() {
        ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
            scenario.navigateVia(R.id.nav_listeners)
            scenario.assertScreen(ListenersFragment::class.java)
        }
    }

    @Test
    fun navigateToPayloads_screenIsShown() {
        ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
            scenario.navigateVia(R.id.nav_payloads)
            scenario.assertScreen(PayloadsFragment::class.java)
        }
    }

    @Test
    fun navigateToLogs_screenIsShown() {
        ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
            scenario.navigateVia(R.id.nav_logs)
            scenario.assertScreen(LogsFragment::class.java)
        }
    }

    @Test
    fun navigateToSettings_screenIsShown() {
        ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
            scenario.navigateVia(R.id.nav_settings)
            scenario.assertScreen(SettingsFragment::class.java)
        }
    }

    @Test
    fun navigateBackToDashboard_afterVisitingSettings() {
        ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
            scenario.navigateVia(R.id.nav_settings)
            scenario.assertScreen(SettingsFragment::class.java)
            scenario.navigateVia(R.id.nav_dashboard)
            scenario.assertScreen(DashboardFragment::class.java)
        }
    }
}
