package Emeris.PROG7314.trinitymobileclient

import Emeris.PROG7314.trinitymobileclient.auth.AuthProvider
import Emeris.PROG7314.trinitymobileclient.auth.AuthRepository
import Emeris.PROG7314.trinitymobileclient.databinding.ActivityHomeBinding
import Emeris.PROG7314.trinitymobileclient.model.NavList
import Emeris.PROG7314.trinitymobileclient.settings.SettingsDefaults
import Emeris.PROG7314.trinitymobileclient.settings.SettingsProvider
import Emeris.PROG7314.trinitymobileclient.settings.ThemeManager
import Emeris.PROG7314.trinitymobileclient.settings.Themes
import Emeris.PROG7314.trinitymobileclient.ui.initialsOf
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.core.content.edit

/**
 * https://developer.android.com/reference/android/os/Looper
 * https://developer.android.com/reference/java/lang/Runnable
 * https://developer.android.com/identity/sign-in/biometric-auth
 * https://developer.android.com/reference/android/view/ViewGroup#dispatchTouchEvent(android.view.MotionEvent)
 */
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private lateinit var authRepository: AuthRepository

    private val lockHandler = Handler(Looper.getMainLooper())
    private var autoLockMinutes = 0

    private val autoLockRunnable = Runnable {
        startActivity(
            Intent(this, LoginActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        finish()
    }

    private val navItemIds = NavList.navItemIds

    // Biometric Auth
    private val biometricPreferences by lazy {
        getSharedPreferences(
            "biometric_preferences",
            MODE_PRIVATE
        )
    }

    private companion object {
        const val TAG = "HomeActivity"
        const val BIOMETRIC_LOGIN_ENABLED = "biometric_login_enabled"
        const val BIOMETRIC_USER_ID = "biometric_user_id"
    }

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

        binding.btnSettings.setOnClickListener {
            selectNavItem(R.id.nav_settings)
            showScreen(SettingsFragment(), sharedAppBar = true)
        }

        showSignedInUser()
        wireDrawer()
        handleBackNavigation()

        if (savedInstanceState == null) {
            selectNavItem(R.id.nav_dashboard)
            showScreen(DashboardFragment(), sharedAppBar = true)
        }
    }

    private fun showSignedInUser() {
        val username = authRepository.currentUserUsername()
        Log.d(TAG, "drawer user = ${username ?: "none"}")

        val initials = initialsOf(username) ?: getString(R.string.drawer_default_initials)

        binding.navDrawer.tvUsername.text =
            username ?: getString(R.string.drawer_default_username)
        binding.navDrawer.tvAvatarInitials.text = initials
        binding.tvAppBarInitials.text = initials
    }

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
                    binding.appBar.visibility = View.VISIBLE
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                }

                else -> {
                    val leave = {
                        Log.d(TAG, "back -> leave HomeActivity")
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                    val settings = currentScreen() as? SettingsFragment
                    if (settings?.hasUnsavedChanges() == true) {
                        settings.confirmDiscard(onDiscard = leave)
                    } else {
                        leave()
                    }
                }
            }
        }
    }

    private fun wireDrawer() {
        binding.navDrawer.navDashboard.setOnClickListener {
            selectNavItem(R.id.nav_dashboard)
            showScreen(DashboardFragment(), sharedAppBar = true); closeDrawer()
        }
        binding.navDrawer.navListeners.setOnClickListener {
            selectNavItem(R.id.nav_listeners)
            showScreen(ListenersFragment(), sharedAppBar = true); closeDrawer()
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

            biometricPreferences.edit {
                remove(BIOMETRIC_LOGIN_ENABLED)
                remove(BIOMETRIC_USER_ID)
            }

            authRepository.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun selectNavItem(selectedId: Int) {
        val rippleBg = TypedValue().also {
            theme.resolveAttribute(android.R.attr.selectableItemBackground, it, true)
        }.resourceId

        for (id in navItemIds) {
            val row = when (id) {
                R.id.nav_dashboard -> binding.navDrawer.navDashboard
                R.id.nav_listeners -> binding.navDrawer.navListeners
                R.id.nav_payloads  -> binding.navDrawer.navPayloads
                R.id.nav_logs      -> binding.navDrawer.navLogs
                R.id.nav_settings  -> binding.navDrawer.navSettings
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

    fun openAgentInterface(agentId: String) {
        Log.d(TAG, "nav -> AgentInterface ($agentId)")
        showScreen(AgentInterfaceFragment.newInstance(agentId), sharedAppBar = false, addToBackStack = true)
    }

    private fun showScreen(
        fragment: Fragment,
        sharedAppBar: Boolean,
        addToBackStack: Boolean = false
    ) {
        val current = currentScreen()

        if (current is SettingsFragment && fragment is SettingsFragment) {
            Log.d(TAG, "nav -> already on Settings")
            return
        }

        if (current is SettingsFragment && current.hasUnsavedChanges()) {
            current.confirmDiscard(
                onDiscard = { replaceScreen(fragment, sharedAppBar, addToBackStack) },
                onKeep = { selectNavItem(R.id.nav_settings) }
            )
            return
        }

        replaceScreen(fragment, sharedAppBar, addToBackStack)
    }

    private fun currentScreen(): Fragment? =
        supportFragmentManager.findFragmentById(R.id.content_container)

    private fun replaceScreen(
        fragment: Fragment,
        sharedAppBar: Boolean,
        addToBackStack: Boolean
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

    fun applyAutoLock(minutes: Int) {
        Log.d(TAG, "auto-lock -> $minutes min")
        autoLockMinutes = minutes
        resetAutoLockTimer()
    }

    @VisibleForTesting
    fun autoLockMinutesForTest(): Int = autoLockMinutes

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
}
