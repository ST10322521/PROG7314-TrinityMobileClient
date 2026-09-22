package Emeris.PROG7314.trinitymobileclient

import Emeris.PROG7314.trinitymobileclient.auth.AuthProvider
import Emeris.PROG7314.trinitymobileclient.auth.AuthRepository
import Emeris.PROG7314.trinitymobileclient.databinding.ActivityHomeBinding
import Emeris.PROG7314.trinitymobileclient.model.NavList
import Emeris.PROG7314.trinitymobileclient.settings.SettingsDefaults
import Emeris.PROG7314.trinitymobileclient.settings.SettingsProvider
import Emeris.PROG7314.trinitymobileclient.settings.ThemeManager
import Emeris.PROG7314.trinitymobileclient.settings.Themes
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import Emeris.PROG7314.trinitymobileclient.model.Agent
import Emeris.PROG7314.trinitymobileclient.ui.agentmanager.AgentInterfaceFragment
import Emeris.PROG7314.trinitymobileclient.ui.dashboard.DashboardFragment
import Emeris.PROG7314.trinitymobileclient.ui.commandconsole.CommandConsoleFragment
import Emeris.PROG7314.trinitymobileclient.ui.listenermanager.ListenerManagerFragment
import Emeris.PROG7314.trinitymobileclient.ui.logs.LogsFragment
import Emeris.PROG7314.trinitymobileclient.ui.payloads.PayloadsFragment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

// Shell activity: nav drawer + shared app bar. Swaps fragments into content_container.
// Manual nav, no Nav Component. View binding renames drawer ids, so nav_logout is
// binding.navDrawer.navLogout.
/**
 * https://developer.android.com/guide/fragments/transactions
 * https://developer.android.com/reference/android/os/Looper
 * https://developer.android.com/reference/java/lang/Runnable
 * https://developer.android.com/reference/android/view/ViewGroup#dispatchTouchEvent(android.view.MotionEvent)
 */
class HomeActivity : AppCompatActivity() {

    //View bindings
    private lateinit var binding: ActivityHomeBinding

    //auth repo for signing out
    private lateinit var authRepository: AuthRepository

    private val lockHandler = Handler(Looper.getMainLooper())
    private var autoLockMinutes = 0

    private val autoLockRunnable = Runnable {
        authRepository.logout()

        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    //Nav list Ids
    private val navItemIds = NavList.navItemIds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authRepository = AuthProvider.repository()

        loadTheme()
        loadAutoLock()

        binding.btnMenu.setOnClickListener {
            Log.d(TAG, "drawer opened")
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        //App-bar settings gear: shortcut to the Settings screen.
        binding.btnSettings.setOnClickListener {
            selectNavItem(R.id.nav_settings)
            showScreen(SettingsFragment(), sharedAppBar = true)
        }

        showSignedInUser()
        wireDrawer()
        handleBackNavigation()

        //start on Dashboard
        if (savedInstanceState == null) {
            selectNavItem(R.id.nav_dashboard)
            showScreen(DashboardFragment(), sharedAppBar = true)
        }
    }

    //Fills the drawer footer and app-bar avatar with the signed-in user's name and initials.
    //Name comes from AuthRepository; falls back to placeholders when no user.
    private fun showSignedInUser() {
        val username = authRepository.currentUserUsername()
        Log.d(TAG, "drawer user = ${username ?: "none"}")

        val initials = initialsOf(username) ?: getString(R.string.drawer_default_initials)

        binding.navDrawer.tvUsername.text =
            username ?: getString(R.string.drawer_default_username)
        binding.navDrawer.tvAvatarInitials.text = initials
        binding.tvAppBarInitials.text = initials
    }

    //Up to two uppercase initials from a name or email.
    //"ada lovelace" -> "AL", "ada@trinity.io" -> "A", blank/null -> null.
    private fun initialsOf(username: String?): String? {
        val name = username?.substringBefore('@')?.trim().orEmpty()
        if (name.isEmpty()) return null

        return name.split(' ', '.', '_')
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().uppercaseChar() }
            .joinToString("")
            .ifEmpty { null }
    }

    //Back: close drawer, else pop detail screen, else leave the activity.
    private fun handleBackNavigation() {
        onBackPressedDispatcher.addCallback(this) {
            when {
                binding.drawerLayout.isDrawerOpen(GravityCompat.START) -> {
                    Log.d(TAG, "back -> close drawer")
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }

                supportFragmentManager.backStackEntryCount > 0 -> {
                    Log.d(TAG, "back -> pop detail screen")
                    supportFragmentManager.popBackStack()
                    //restore shared app bar + drawer
                    binding.appBar.visibility = View.VISIBLE
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                }

                else -> {
                    Log.d(TAG, "back -> leave HomeActivity")
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }

    //Every drawer route. Each row selects itself, swaps in its fragment and
    //closes the drawer; logout also clears the session and returns to login.
    private fun wireDrawer() {
        binding.navDrawer.navDashboard.setOnClickListener {
            selectNavItem(R.id.nav_dashboard)
            showScreen(DashboardFragment(), sharedAppBar = true); closeDrawer()
        }
        binding.navDrawer.navListeners.setOnClickListener {
            selectNavItem(R.id.nav_listeners)
            showScreen(ListenerManagerFragment(), sharedAppBar = false, addToBackStack = true); closeDrawer()
        }
        binding.navDrawer.navConsole.setOnClickListener {
            selectNavItem(R.id.nav_console)
            showScreen(CommandConsoleFragment(), sharedAppBar = false, addToBackStack = true); closeDrawer()
        }
        binding.navDrawer.navPayloads.setOnClickListener {
            selectNavItem(R.id.nav_payloads)
            showScreen(PayloadsFragment(), sharedAppBar = true); closeDrawer()
        }
        binding.navDrawer.navLogs.setOnClickListener {
            selectNavItem(R.id.nav_logs)
            showScreen(LogsFragment(), sharedAppBar = true); closeDrawer()
        }
        binding.navDrawer.navSettings.setOnClickListener {
            selectNavItem(R.id.nav_settings)
            showScreen(SettingsFragment(), sharedAppBar = true); closeDrawer()
        }
        binding.navDrawer.navLogout.setOnClickListener {
            closeDrawer()

            Log.d("firebaseAuth", "Logout Selected")
            authRepository.logout()

            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    //Highlight the selected drawer row, reset the rest.
    private fun selectNavItem(selectedId: Int) {
        val rippleBg = TypedValue().also {
            theme.resolveAttribute(android.R.attr.selectableItemBackground, it, true)
        }.resourceId

        for (id in navItemIds) {
            val row = when (id) {
                R.id.nav_dashboard -> binding.navDrawer.navDashboard
                R.id.nav_listeners -> binding.navDrawer.navListeners
                R.id.nav_console -> binding.navDrawer.navConsole
                R.id.nav_payloads -> binding.navDrawer.navPayloads
                R.id.nav_logs -> binding.navDrawer.navLogs
                R.id.nav_settings -> binding.navDrawer.navSettings
                else -> continue
            }

            val icon = row.getChildAt(0) as ImageView
            val label = row.getChildAt(1) as TextView
            val selected = id == selectedId

            row.setBackgroundResource(if (selected) R.drawable.bg_drawer_selected else rippleBg)

            val tint = ContextCompat.getColor(
                this, if (selected) R.color.brand_orange else R.color.text_secondary
            )
            icon.setColorFilter(tint)
            label.setTextColor(tint)
            label.setTypeface(null, if (selected) Typeface.BOLD else Typeface.NORMAL)
        }
    }

    // called from the Dashboard agent list
    fun openAgentInterface(agent: Agent) {
        showScreen(AgentInterfaceFragment.newInstance(agent.id), sharedAppBar = false, addToBackStack = true)
    }

    //Single entry point for every screen change.
    //sharedAppBar = false hides the app bar and locks the drawer (detail screens).
    //addToBackStack = true for pushed detail screens only.
    private fun showScreen(
        fragment: Fragment,
        sharedAppBar: Boolean,
        addToBackStack: Boolean = false
    ) {
        Log.d(TAG, "nav -> ${fragment::class.simpleName} (sharedAppBar=$sharedAppBar)")

        binding.appBar.visibility = if (sharedAppBar) View.VISIBLE else View.GONE
        binding.drawerLayout.setDrawerLockMode(
            if (sharedAppBar) DrawerLayout.LOCK_MODE_UNLOCKED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        )

        supportFragmentManager.beginTransaction()
            .replace(R.id.content_container, fragment)
            .also { if (addToBackStack) it.addToBackStack(null) }
            .commit()
    }

    private fun loadTheme(){
        val settingsRepository = SettingsProvider.repository(this)

        lifecycleScope.launch {
            val preferences = settingsRepository.getPreferences()

            preferences?.let {
                ThemeManager.applyTheme(Themes.valueOf(it.theme))
            }
        }
    }

    private fun loadAutoLock(){
        val settingsRepository = SettingsProvider.repository(this)

        lifecycleScope.launch {
            val preferences = settingsRepository.getPreferences()

            autoLockMinutes = preferences?.autoLockMinutes ?: SettingsDefaults.DEFAULT_AUTO_LOCK_MINUTES

            resetAutoLockTimer()
        }
    }

    private fun resetAutoLockTimer() {
        lockHandler.removeCallbacks(autoLockRunnable)

        if (autoLockMinutes > 0) {
            lockHandler.postDelayed(
                autoLockRunnable,
                autoLockMinutes * 60 * 1000L
            )
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        resetAutoLockTimer()
        return super.dispatchTouchEvent(ev)
    }

    override fun onDestroy() {
        lockHandler.removeCallbacks(autoLockRunnable)
        super.onDestroy()
    }
    private fun closeDrawer() = binding.drawerLayout.closeDrawer(GravityCompat.START)

    private companion object {
        const val TAG = "HomeActivity"
    }
}
