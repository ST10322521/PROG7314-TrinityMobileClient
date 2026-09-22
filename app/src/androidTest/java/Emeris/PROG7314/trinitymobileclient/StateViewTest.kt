package Emeris.PROG7314.trinitymobileclient

import Emeris.PROG7314.trinitymobileclient.ui.StateView
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for the shared loading / empty / error / success UI states.
 *
 * Each test launches HomeActivity, navigates to a real screen, then drives the
 * fragment's state functions and asserts what the StateView is actually
 * showing. Like NavigationTest, this avoids Espresso — its event-injection
 * layer is incompatible with the API 37 emulator — and instead asserts against
 * the view hierarchy directly on the UI thread.
 */
@RunWith(AndroidJUnit4::class)
class StateViewTest {

    //Navigates to a drawer screen and hands back its fragment.
    private inline fun <reified T : androidx.fragment.app.Fragment> withScreen(
        navItemId: Int,
        crossinline block: (HomeActivity, T) -> Unit
    ) {
        ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                activity.findViewById<View>(navItemId).performClick()
                activity.supportFragmentManager.executePendingTransactions()

                val fragment = activity.supportFragmentManager
                    .findFragmentById(R.id.content_container)
                assertTrue(
                    "Expected ${T::class.simpleName} but was ${fragment?.javaClass?.simpleName}",
                    fragment is T
                )
                block(activity, fragment as T)
            }
        }
    }

    private fun HomeActivity.stateView(id: Int): StateView = findViewById(id)

    private fun StateView.progress(): ProgressBar = findViewById(R.id.state_progress)
    private fun StateView.icon(): ImageView = findViewById(R.id.state_icon)
    private fun StateView.title(): TextView = findViewById(R.id.state_title)
    private fun StateView.message(): TextView = findViewById(R.id.state_message)
    private fun StateView.retry(): View = findViewById(R.id.state_retry)

    @Test
    fun listeners_opensOnEmptyState() {
        withScreen<ListenersFragment>(R.id.nav_listeners) { activity, _ ->
            val state = activity.stateView(R.id.state_listeners)

            assertEquals("State block should be visible", View.VISIBLE, state.visibility)
            assertEquals("Icon should show for empty", View.VISIBLE, state.icon().visibility)
            assertEquals("Spinner should be hidden", View.GONE, state.progress().visibility)
            assertEquals(
                activity.getString(R.string.state_listeners_empty_title),
                state.title().text.toString()
            )
        }
    }

    @Test
    fun loadingState_showsSpinnerAndHidesIcon() {
        withScreen<LogsFragment>(R.id.nav_logs) { activity, fragment ->
            fragment.showLoading()

            val state = activity.stateView(R.id.state_logs)
            assertEquals("Spinner should show", View.VISIBLE, state.progress().visibility)
            assertEquals("Icon should be hidden", View.GONE, state.icon().visibility)
            assertEquals(
                activity.getString(R.string.state_loading),
                state.title().text.toString()
            )
        }
    }

    @Test
    fun errorState_withRetry_showsRetryButton() {
        withScreen<ListenersFragment>(R.id.nav_listeners) { activity, fragment ->
            var retried = false
            fragment.showError { retried = true }

            val state = activity.stateView(R.id.state_listeners)
            assertEquals("Retry should show", View.VISIBLE, state.retry().visibility)
            assertEquals(
                activity.getString(R.string.state_error_title),
                state.title().text.toString()
            )

            //The retry affordance must actually invoke the handler.
            state.retry().performClick()
            assertTrue("Retry handler was not invoked", retried)
        }
    }

    @Test
    fun errorState_withoutRetry_hidesRetryButton() {
        withScreen<ListenersFragment>(R.id.nav_listeners) { activity, fragment ->
            fragment.showError(onRetry = null)

            val state = activity.stateView(R.id.state_listeners)
            assertEquals("Retry should stay hidden", View.GONE, state.retry().visibility)
        }
    }

    @Test
    fun successState_showsConfirmationCopy() {
        withScreen<PayloadsFragment>(R.id.nav_payloads) { activity, fragment ->
            fragment.showSuccess()

            val state = activity.stateView(R.id.state_payloads)
            assertEquals("Icon should show for success", View.VISIBLE, state.icon().visibility)
            assertEquals(
                activity.getString(R.string.state_success_title),
                state.title().text.toString()
            )
            assertEquals(View.VISIBLE, state.message().visibility)
        }
    }

    @Test
    fun contentState_hidesStateBlockAndRevealsContent() {
        withScreen<LogsFragment>(R.id.nav_logs) { activity, fragment ->
            fragment.showContent()

            val state = activity.stateView(R.id.state_logs)
            val content = activity.findViewById<View>(R.id.logs_content)

            assertEquals("State block should be hidden", View.GONE, state.visibility)
            assertEquals("Content should be visible", View.VISIBLE, content.visibility)
        }
    }

    @Test
    fun switchingStates_doesNotLeaveStaleViewsVisible() {
        withScreen<ListenersFragment>(R.id.nav_listeners) { activity, fragment ->
            val state = activity.stateView(R.id.state_listeners)

            //error -> loading must clear the retry button and the icon
            fragment.showError { }
            fragment.showLoading()

            assertEquals("Retry left over from error", View.GONE, state.retry().visibility)
            assertEquals("Icon left over from error", View.GONE, state.icon().visibility)
            assertEquals("Spinner should show", View.VISIBLE, state.progress().visibility)

            //loading -> empty must clear the spinner again
            fragment.showEmpty()
            assertEquals("Spinner left over from loading", View.GONE, state.progress().visibility)
            assertEquals("Icon should show for empty", View.VISIBLE, state.icon().visibility)
        }
    }

    @Test
    fun rapidStateChanges_doNotCrash() {
        withScreen<ListenersFragment>(R.id.nav_listeners) { activity, fragment ->
            repeat(10) {
                fragment.showLoading()
                fragment.showError { }
                fragment.showEmpty()
                fragment.showContent()
            }

            //Still responsive and in a known state afterwards.
            fragment.showEmpty()
            val state = activity.stateView(R.id.state_listeners)
            assertEquals(View.VISIBLE, state.visibility)
        }
    }
}
