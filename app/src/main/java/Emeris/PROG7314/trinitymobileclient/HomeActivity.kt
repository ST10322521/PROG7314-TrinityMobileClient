package Emeris.PROG7314.trinitymobileclient

import Emeris.PROG7314.trinitymobileclient.auth.AuthProvider
import Emeris.PROG7314.trinitymobileclient.auth.AuthRepository
import Emeris.PROG7314.trinitymobileclient.databinding.ActivityHomeBinding
import Emeris.PROG7314.trinitymobileclient.model.NavList
import Emeris.PROG7314.trinitymobileclient.data.sync.CommandSyncScheduler
import Emeris.PROG7314.trinitymobileclient.model.EventLog
import Emeris.PROG7314.trinitymobileclient.notifications.NotificationHelper
import Emeris.PROG7314.trinitymobileclient.BuildConfig
import Emeris.PROG7314.trinitymobileclient.signalr.SignalRManager
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment

class HomeActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "HomeActivity"
        private const val REQUEST_NOTIFICATION_PERMISSION = 1001
    }

    private lateinit var binding: ActivityHomeBinding
    private lateinit var authRepository: AuthRepository
    private val navItemIds = NavList.navItemIds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authRepository = AuthProvider.repository()

        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        wireDrawer()
        handleBackNavigation()

        NotificationHelper.initialize(applicationContext)
        requestNotificationPermission()
        connectHub()
        CommandSyncScheduler.schedule(this)

        if (savedInstanceState == null) {
            selectNavItem(R.id.nav_dashboard)
            showScreen(DashboardFragment(), sharedAppBar = true)
        }
    }

    private fun handleBackNavigation() {
        onBackPressedDispatcher.addCallback(this) {
            when {
                binding.drawerLayout.isDrawerOpen(GravityCompat.START) -> {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }

                supportFragmentManager.backStackEntryCount > 0 -> {
                    supportFragmentManager.popBackStack()
                    binding.appBar.visibility = View.VISIBLE
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                }

                else -> {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
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

    override fun onDestroy() {
        super.onDestroy()
        SignalRManager.getInstance().disconnect()
        Log.d(TAG, "Hub disconnected")
    }

    // ── SignalR + Notifications ───────────────────────────────────────────────

    private fun connectHub() {
        SignalRManager.getInstance().connect(
            hubUrl      = BuildConfig.HUB_URL,
            onConnected = {
                Log.d(TAG, "Hub connected")
                registerGlobalHubHandlers()
            },
            onError = { e ->
                Log.e(TAG, "Hub connection failed", e)
            }
        )
    }

    private fun registerGlobalHubHandlers() {
        // EventLog → push notification (shown app-wide)
        SignalRManager.getInstance().on("EventLogNotification", EventLog::class.java) { log ->
            NotificationHelper.showEvent(
                context = applicationContext,
                title   = "Event Log",
                message = "[${log.type}] ${log.message}"
            )
        }
        // TaskNotification is handled per-agent inside AgentInterfaceFragment
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_NOTIFICATION_PERMISSION
                )
            }
        }
    }

    fun openAgentInterface(agentId: String) {
        showScreen(AgentInterfaceFragment.newInstance(agentId), sharedAppBar = false, addToBackStack = true)
    }

    private fun showScreen(
        fragment: Fragment,
        sharedAppBar: Boolean,
        addToBackStack: Boolean = false
    ) {
        binding.appBar.visibility = if (sharedAppBar) View.VISIBLE else View.GONE
        binding.drawerLayout.setDrawerLockMode(
            if (sharedAppBar) DrawerLayout.LOCK_MODE_UNLOCKED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        )

        supportFragmentManager.beginTransaction()
            .replace(R.id.content_container, fragment)
            .also { if (addToBackStack) it.addToBackStack(null) }
            .commit()
    }

    private fun closeDrawer() = binding.drawerLayout.closeDrawer(GravityCompat.START)
}
