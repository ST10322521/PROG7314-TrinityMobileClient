package Emeris.PROG7314.trinitymobileclient

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment

// Shell activity: nav drawer + shared app bar, swaps fragments into content_container.
class HomeActivity : AppCompatActivity() {

    private lateinit var drawer: DrawerLayout
    private lateinit var appBar: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        drawer = findViewById(R.id.drawer_layout)
        appBar = findViewById(R.id.app_bar)

        findViewById<ImageButton>(R.id.btn_menu).setOnClickListener {
            drawer.openDrawer(GravityCompat.START)
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
                drawer.isDrawerOpen(GravityCompat.START) ->
                    drawer.closeDrawer(GravityCompat.START)

                supportFragmentManager.backStackEntryCount > 0 -> {
                    supportFragmentManager.popBackStack()
                    // restore shared app bar + drawer
                    appBar.visibility = View.VISIBLE
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                }

                else -> {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }

    private val navItemIds = listOf(
        R.id.nav_dashboard, R.id.nav_listeners, R.id.nav_payloads, R.id.nav_logs, R.id.nav_settings
    )

    private fun wireDrawer() {
        findViewById<View>(R.id.nav_dashboard).setOnClickListener {
            selectNavItem(R.id.nav_dashboard)
            showScreen(DashboardFragment(), sharedAppBar = true); closeDrawer()
        }
        findViewById<View>(R.id.nav_listeners).setOnClickListener {
            selectNavItem(R.id.nav_listeners)
            showScreen(ListenersFragment(), sharedAppBar = true); closeDrawer()
        }
        findViewById<View>(R.id.nav_payloads).setOnClickListener {
            selectNavItem(R.id.nav_payloads)
            showScreen(PayloadsFragment(), sharedAppBar = true); closeDrawer()
        }
        findViewById<View>(R.id.nav_logs).setOnClickListener {
            selectNavItem(R.id.nav_logs)
            showScreen(LogsFragment(), sharedAppBar = true); closeDrawer()
        }
        findViewById<View>(R.id.nav_settings).setOnClickListener {
            selectNavItem(R.id.nav_settings)
            showScreen(SettingsFragment(), sharedAppBar = true); closeDrawer()
        }
        findViewById<View>(R.id.nav_logout).setOnClickListener {
            closeDrawer()
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
            val row = findViewById<LinearLayout>(id)
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
        appBar.visibility = if (sharedAppBar) View.VISIBLE else View.GONE
        drawer.setDrawerLockMode(
            if (sharedAppBar) DrawerLayout.LOCK_MODE_UNLOCKED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        )

        supportFragmentManager.beginTransaction()
            .replace(R.id.content_container, fragment)
            .also { if (addToBackStack) it.addToBackStack(null) }
            .commit()
    }

    private fun closeDrawer() = drawer.closeDrawer(GravityCompat.START)
}
