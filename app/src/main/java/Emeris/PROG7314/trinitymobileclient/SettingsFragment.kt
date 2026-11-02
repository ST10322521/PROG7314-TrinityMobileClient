package Emeris.PROG7314.trinitymobileclient

import Emeris.PROG7314.trinitymobileclient.auth.AuthProvider
import Emeris.PROG7314.trinitymobileclient.auth.AuthRepository
import Emeris.PROG7314.trinitymobileclient.databinding.FragmentSettingsBinding
import Emeris.PROG7314.trinitymobileclient.settings.SettingsDefaults
import Emeris.PROG7314.trinitymobileclient.settings.SettingsProvider
import Emeris.PROG7314.trinitymobileclient.settings.SettingsRepository
import Emeris.PROG7314.trinitymobileclient.settings.ThemeManager
import Emeris.PROG7314.trinitymobileclient.settings.Themes
import Emeris.PROG7314.trinitymobileclient.ui.initialsOf
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

//User settings: account card, general preferences (language, theme, biometric,
//notifications, auto-lock) saved to Room, and category rows that jump to their section.
//https://developer.android.com/develop/ui/views/components/dialogs
//https://developer.android.com/reference/androidx/appcompat/app/AppCompatDelegate#setDefaultNightMode(int)
//https://developer.android.com/reference/androidx/core/widget/NestedScrollView
class SettingsFragment : Fragment(R.layout.fragment_settings) {
    //Setting up bindings
    private var _bindings: FragmentSettingsBinding? = null
    private val bindings get() = _bindings!!

    private lateinit var settingsRepository: SettingsRepository
    private lateinit var authRepository: AuthRepository

    //Current picker values. Kept here instead of read back from the labels,
    //so saving still works once the labels are translated.
    private var selectedTheme = SettingsDefaults.DEFAULT_THEME
    private var selectedAutoLockMinutes = SettingsDefaults.DEFAULT_AUTO_LOCK_MINUTES
    private var selectedLanguage = SettingsDefaults.DEFAULT_LANGUAGE

    //What is currently saved, so leaving with different values can ask first.
    //Null until Room has loaded.
    private var savedValues: SettingsValues? = null

    //Unsaved picks kept over a rebuild (rotation, dark mode switch).
    //Applied once Room has loaded so they win over the saved values.
    private var restoredValues: SettingsValues? = null

    private var selectedCategoryId = R.id.cat_general

    //One snapshot of everything the Save button writes.
    private data class SettingsValues(
        val theme: Themes,
        val autoLockMinutes: Int,
        val language: String,
        val biometricEnabled: Boolean,
        val notificationsEnabled: Boolean
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

        _bindings = FragmentSettingsBinding.bind(view)

        savedInstanceState?.let { restoreState(it) }

        settingsRepository = SettingsProvider.repository(requireContext())
        authRepository = AuthProvider.repository()

        showAccount()
        showAppVersion()
        wireCategories()

        //defaults first so the screen is never blank while Room loads
        showPreferenceLabels()
        loadPreferences()

        bindings.tvLanguage.setOnClickListener { showLanguageDialog() }
        bindings.tvTheme.setOnClickListener { showThemeDialog() }
        bindings.tvAutoLock.setOnClickListener { showAutoLockDialog() }

        bindings.switchBiometric.setOnCheckedChangeListener { _, checked ->
            Log.d(TAG, "biometric toggled -> $checked")
        }
        bindings.switchNotifications.setOnCheckedChangeListener { _, checked ->
            Log.d(TAG, "notifications toggled -> $checked")
        }

        bindings.btnSaveSettings.setOnClickListener { saveSettings() }
        bindings.btnSignOut.setOnClickListener { confirmSignOut() }
        bindings.btnResetPassword.setOnClickListener { confirmPasswordReset() }
    }

    //Account card

    //Fills the account card from Firebase. Reset password only shows for
    //email + password accounts, Google-only accounts have no password.
    private fun showAccount() {
        val username = authRepository.currentUserUsername()
        val email = authRepository.currentUserEmail()
        Log.d(TAG, "account card user = ${username ?: "none"}")

        bindings.tvAccountName.text = username ?: getString(R.string.settings_account_signed_out)
        bindings.tvAccountEmail.text = when {
            username == null -> getString(R.string.settings_account_signed_out_hint)
            email == null -> getString(R.string.settings_account_no_email)
            else -> email
        }
        bindings.tvAccountInitials.text =
            initialsOf(username) ?: getString(R.string.drawer_default_initials)

        val canReset = email != null && authRepository.hasPasswordSignIn()
        bindings.btnResetPassword.visibility = if (canReset) View.VISIBLE else View.GONE
        //nothing to sign out of when nobody is signed in
        bindings.btnSignOut.visibility = if (username != null) View.VISIBLE else View.GONE
        //drop the empty button row so the card doesn't keep a gap
        val anyAction = canReset || username != null
        bindings.accountActions.visibility = if (anyAction) View.VISIBLE else View.GONE
    }

    private fun confirmSignOut() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.settings_sign_out_confirm_title)
            .setMessage(R.string.settings_sign_out_confirm_message)
            .setPositiveButton(R.string.settings_sign_out) { _, _ -> signOut() }
            .setNegativeButton(R.string.settings_cancel, null)
            .show()
    }

    //Signs out and returns to login with the back stack cleared,
    //so back can't reopen the app signed out.
    private fun signOut() {
        Log.d(TAG, "sign out confirmed")
        authRepository.logout()

        val intent = Intent(requireContext(), LoginActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun confirmPasswordReset() {
        val email = authRepository.currentUserEmail() ?: return

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.settings_reset_password_confirm_title)
            .setMessage(getString(R.string.settings_reset_password_confirm_message, email))
            .setPositiveButton(R.string.settings_send) { _, _ -> sendPasswordReset(email) }
            .setNegativeButton(R.string.settings_cancel, null)
            .show()
    }

    //Button stays disabled until Firebase answers, so it can't be spammed.
    private fun sendPasswordReset(email: String) {
        Log.d(TAG, "password reset requested")
        bindings.btnResetPassword.isEnabled = false

        authRepository.sendPasswordReset(email) { result ->
            //screen may have been closed while waiting
            val b = _bindings ?: return@sendPasswordReset
            b.btnResetPassword.isEnabled = true

            val message = if (result.isSuccess) {
                getString(R.string.settings_reset_password_sent, email)
            } else {
                getString(R.string.settings_reset_password_failed)
            }
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }

    //About card

    private fun showAppVersion() {
        val version = try {
            requireContext().packageManager
                .getPackageInfo(requireContext().packageName, 0).versionName
        } catch (e: Exception) {
            Log.e(TAG, "could not read app version", e)
            null
        }
        bindings.tvAppVersion.text = getString(R.string.settings_about_version, version ?: getString(R.string.settings_value_none))
    }

    //Categories

    //Each category row highlights itself and scrolls to its section.
    //Integrations has no section yet, so it is left as is.
    private fun wireCategories() {
        bindings.catGeneral.setOnClickListener {
            openCategory(bindings.catGeneral, bindings.sectionGeneral)
        }
        bindings.catServer.setOnClickListener {
            openCategory(bindings.catServer, bindings.cardServerHealth)
        }
        bindings.catNotifications.setOnClickListener {
            openCategory(bindings.catNotifications, bindings.sectionNotifications)
        }
        bindings.catAbout.setOnClickListener {
            openCategory(bindings.catAbout, bindings.sectionAbout)
        }
        //restored after a rebuild, General otherwise
        val restored = view?.findViewById<LinearLayout>(selectedCategoryId) ?: bindings.catGeneral
        selectCategory(restored)
    }

    private fun openCategory(row: LinearLayout, section: View) {
        Log.d(TAG, "category -> ${resources.getResourceEntryName(row.id)}")
        selectCategory(row)
        bindings.settingsScroll.smoothScrollTo(0, topInContent(section))
    }

    //Section views can be nested inside cards, so add up offsets until the scroll content.
    private fun topInContent(view: View): Int {
        var top = 0
        var current: View? = view
        while (current != null && current != bindings.settingsContent) {
            top += current.top
            current = current.parent as? View
        }
        return top
    }

    //Same highlight as the nav drawer: orange + bold for the selected row.
    private fun selectCategory(selected: LinearLayout) {
        val rippleBg = TypedValue().also {
            requireContext().theme.resolveAttribute(android.R.attr.selectableItemBackground, it, true)
        }.resourceId

        val rows = listOf(
            bindings.catGeneral, bindings.catServer, bindings.catNotifications,
            bindings.catIntegrations, bindings.catAbout
        )
        selectedCategoryId = selected.id
        for (row in rows) {
            val isSelected = row == selected
            row.isSelected = isSelected
            row.setBackgroundResource(if (isSelected) R.drawable.bg_drawer_selected else rippleBg)

            val tint = ContextCompat.getColor(
                requireContext(), if (isSelected) R.color.brand_orange else R.color.text_secondary
            )
            (row.getChildAt(0) as ImageView).setColorFilter(tint)
            (row.getChildAt(1) as TextView).apply {
                setTextColor(tint)
                setTypeface(null, if (isSelected) Typeface.BOLD else Typeface.NORMAL)
            }
        }
    }

    //Preferences

    private fun loadPreferences() {
        viewLifecycleOwner.lifecycleScope.launch {
            val preferences = try {
                settingsRepository.getPreferences()
            } catch (e: Exception) {
                Log.e(TAG, "could not load preferences", e)
                null
            }
            val b = _bindings ?: return@launch

            if (preferences != null) {
                Log.d(TAG, "loaded preferences: $preferences")
                selectedTheme = Themes.entries.firstOrNull { it.name == preferences.theme }
                    ?: SettingsDefaults.DEFAULT_THEME
                selectedAutoLockMinutes = preferences.autoLockMinutes
                selectedLanguage = preferences.language
                b.switchBiometric.isChecked = preferences.biometricEnabled
                b.switchNotifications.isChecked = preferences.notificationsEnabled
            } else {
                Log.d(TAG, "no saved preferences, showing defaults")
                b.switchBiometric.isChecked = SettingsDefaults.DEFAULT_BIOMETRIC_ENABLED
                b.switchNotifications.isChecked = SettingsDefaults.DEFAULT_NOTIFICATIONS_ENABLED
            }
            savedValues = currentValues()

            //unsaved picks from before a rebuild win over what Room has
            restoredValues?.let {
                Log.d(TAG, "restoring unsaved picks: $it")
                applyValues(it)
                restoredValues = null
            }
            showPreferenceLabels()
        }
    }

    private fun currentValues() = SettingsValues(
        theme = selectedTheme,
        autoLockMinutes = selectedAutoLockMinutes,
        language = selectedLanguage,
        biometricEnabled = bindings.switchBiometric.isChecked,
        notificationsEnabled = bindings.switchNotifications.isChecked
    )

    private fun applyValues(values: SettingsValues) {
        selectedTheme = values.theme
        selectedAutoLockMinutes = values.autoLockMinutes
        selectedLanguage = values.language
        bindings.switchBiometric.isChecked = values.biometricEnabled
        bindings.switchNotifications.isChecked = values.notificationsEnabled
    }

    //Unsaved changes

    //True when something on screen differs from what is saved.
    //HomeActivity asks this before navigating away.
    fun hasUnsavedChanges(): Boolean {
        if (_bindings == null) return false
        val saved = savedValues ?: return false
        return currentValues() != saved
    }

    //Asks before throwing away unsaved settings. onKeep lets the caller undo
    //anything it already changed, like the drawer highlight.
    fun confirmDiscard(onDiscard: () -> Unit, onKeep: () -> Unit = {}) {
        Log.d(TAG, "unsaved changes, asking before leaving")
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.settings_discard_title)
            .setMessage(R.string.settings_discard_message)
            .setPositiveButton(R.string.settings_discard) { _, _ ->
                Log.d(TAG, "discarded unsaved changes")
                onDiscard()
            }
            .setNegativeButton(R.string.settings_keep_editing) { _, _ -> onKeep() }
            .setOnCancelListener { onKeep() }
            .show()
    }

    //Rebuild state

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_CATEGORY, selectedCategoryId)
        if (_bindings == null) return

        val values = currentValues()
        outState.putString(KEY_THEME, values.theme.name)
        outState.putInt(KEY_AUTO_LOCK, values.autoLockMinutes)
        outState.putString(KEY_LANGUAGE, values.language)
        outState.putBoolean(KEY_BIOMETRIC, values.biometricEnabled)
        outState.putBoolean(KEY_NOTIFICATIONS, values.notificationsEnabled)
    }

    private fun restoreState(state: Bundle) {
        selectedCategoryId = state.getInt(KEY_CATEGORY, R.id.cat_general)
        val theme = state.getString(KEY_THEME) ?: return

        restoredValues = SettingsValues(
            theme = Themes.entries.firstOrNull { it.name == theme } ?: SettingsDefaults.DEFAULT_THEME,
            autoLockMinutes = state.getInt(KEY_AUTO_LOCK, SettingsDefaults.DEFAULT_AUTO_LOCK_MINUTES),
            language = state.getString(KEY_LANGUAGE) ?: SettingsDefaults.DEFAULT_LANGUAGE,
            biometricEnabled = state.getBoolean(KEY_BIOMETRIC, SettingsDefaults.DEFAULT_BIOMETRIC_ENABLED),
            notificationsEnabled = state.getBoolean(KEY_NOTIFICATIONS, SettingsDefaults.DEFAULT_NOTIFICATIONS_ENABLED)
        )
        Log.d(TAG, "rebuild: category=${resources.getResourceEntryName(selectedCategoryId)}")
    }

    private fun showPreferenceLabels() {
        bindings.tvTheme.text = selectedTheme.name
        bindings.tvAutoLock.text = SettingsDefaults.autoLockText(selectedAutoLockMinutes)
        bindings.tvLanguage.text = SettingsDefaults.languageText(selectedLanguage)
    }

    private fun saveSettings() {
        val language = selectedLanguage
        val theme = selectedTheme
        val autoLockMinutes = selectedAutoLockMinutes
        val biometricEnabled = bindings.switchBiometric.isChecked
        val notificationsEnabled = bindings.switchNotifications.isChecked

        //no double saves while Room is writing
        bindings.btnSaveSettings.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            val message = try {
                val saved = settingsRepository.savePreferences(
                    theme = theme.name,
                    autoLockMinutes = autoLockMinutes,
                    language = language,
                    biometricEnabled = biometricEnabled,
                    notificationsEnabled = notificationsEnabled
                )
                if (saved) {
                    Log.d(
                        "RoomDb",
                        "Settings saved, theme=$theme autoLock=$autoLockMinutes language=$language " +
                            "biometric=$biometricEnabled notifications=$notificationsEnabled"
                    )
                    savedValues = SettingsValues(
                        theme, autoLockMinutes, language, biometricEnabled, notificationsEnabled
                    )
                    ThemeManager.applyTheme(theme)
                    R.string.settings_saved
                } else {
                    Log.w(TAG, "save skipped, no signed-in user")
                    R.string.settings_save_signed_out
                }
            } catch (e: Exception) {
                Log.e(TAG, "save failed", e)
                R.string.settings_save_failed
            }

            //applyTheme can recreate the activity, so the view may be gone by now
            val b = _bindings ?: return@launch
            b.btnSaveSettings.isEnabled = true
            Toast.makeText(b.root.context, message, Toast.LENGTH_SHORT).show()
        }
    }

    //Picker dialogs: each just updates the selection, Save writes it.

    private fun showLanguageDialog() {
        val labels = SettingsDefaults.languageOptions.keys.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.settings_language)
            .setItems(labels) { _, which ->
                selectedLanguage = SettingsDefaults.languageCode(labels[which])
                Log.d(TAG, "language picked -> $selectedLanguage")
                showPreferenceLabels()
            }.show()
    }

    private fun showAutoLockDialog() {
        val labels = SettingsDefaults.autoLockOptions.keys.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.settings_auto_lock)
            .setItems(labels) { _, which ->
                selectedAutoLockMinutes = SettingsDefaults.autoLockMinutes(labels[which])
                Log.d(TAG, "auto-lock picked -> $selectedAutoLockMinutes min")
                showPreferenceLabels()
            }.show()
    }

    private fun showThemeDialog() {
        val themes = SettingsDefaults.themes

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.settings_theme)
            .setItems(themes.map { it.name }.toTypedArray()) { _, which ->
                selectedTheme = themes[which]
                Log.d(TAG, "theme picked -> $selectedTheme")
                showPreferenceLabels()
            }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView")
        _bindings = null
    }

    private companion object {
        const val TAG = "SettingsFragment"
        const val KEY_CATEGORY = "settings_category"
        const val KEY_THEME = "settings_theme"
        const val KEY_AUTO_LOCK = "settings_auto_lock"
        const val KEY_LANGUAGE = "settings_language"
        const val KEY_BIOMETRIC = "settings_biometric"
        const val KEY_NOTIFICATIONS = "settings_notifications"
    }
}
