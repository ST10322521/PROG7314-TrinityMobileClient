package Emeris.PROG7314.trinitymobileclient

import Emeris.PROG7314.trinitymobileclient.auth.AuthProvider
import Emeris.PROG7314.trinitymobileclient.auth.AuthRepository
import Emeris.PROG7314.trinitymobileclient.databinding.ActivityHomeBinding
import Emeris.PROG7314.trinitymobileclient.model.NavList
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment

// Shell activity: nav drawer + shared app bar, swaps fragments into content_container.
class HomeActivity : AppCompatActivity() {

    // View bindings
    private lateinit var binding: ActivityHomeBinding

    // auth repo for signing out
    private lateinit var authRepository: AuthRepository

    // Nav list Ids
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

        // start on Dashboard
        if (savedInstanceState == null) {
            selectNavItem(R.id.nav_dashboard)
            showScreen(DashboardFragment(), sharedAppBar = true)
        }
    }

    // Back: close drawer, else pop pushed screen, else default.
    private fun handleBackNavigation() {
        onBackPressedDispatcher.addCallback(this) {
            when {
                binding.drawerLayout.isDrawerOpen(GravityCompat.START) -> {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }

                supportFragmentManager.backStackEntryCount > 0 -> {
                    supportFragmentManager.popBackStack()
                    // restore shared app bar + drawer
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

            Log.d("firebaseAuth", "Logout Selected")
            authRepository.logout()

            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    // Highlight the selected drawer row, reset the rest.
    private fun selectNavItem(selectedId: Int) {
        val rippleBg = TypedValue().also {
            theme.resolveAttribute(android.R.attr.selectableItemBackground, it, true)
        }.resourceId

        for (id in navItemIds) {
            val row = when (id) {
                R.id.nav_dashboard -> binding.navDrawer.navDashboard
                R.id.nav_listeners -> binding.navDrawer.navListeners
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
    fun openAgentInterface() {
        showScreen(AgentInterfaceFragment(), sharedAppBar = false, addToBackStack = true)
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
