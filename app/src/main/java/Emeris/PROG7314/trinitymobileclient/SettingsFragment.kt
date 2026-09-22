package Emeris.PROG7314.trinitymobileclient

import Emeris.PROG7314.trinitymobileclient.databinding.FragmentSettingsBinding
import Emeris.PROG7314.trinitymobileclient.settings.SettingsDefaults
import Emeris.PROG7314.trinitymobileclient.settings.SettingsRepository
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import Emeris.PROG7314.trinitymobileclient.settings.SettingsProvider
import Emeris.PROG7314.trinitymobileclient.settings.ThemeManager
import Emeris.PROG7314.trinitymobileclient.settings.Themes
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

/**
 * https://developer.android.com/reference/androidx/appcompat/app/AppCompatDelegate#setDefaultNightMode(int)
 */
class SettingsFragment : Fragment(R.layout.fragment_settings) {
    // Setting up bindings
    private var _bindings: FragmentSettingsBinding? = null
    private val bindings get() = _bindings!!

    private lateinit var settingsRepository: SettingsRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _bindings = FragmentSettingsBinding.bind(view)

        settingsRepository = SettingsProvider.repository(requireContext())

        loadPreferences()

        bindings.tvTheme.setOnClickListener {
            showThemeDialog()
        }

        bindings.tvAutoLock.setOnClickListener {
            showAutoLockDialog()
        }

        bindings.btnSaveSettings.setOnClickListener {
            saveSettings()
        }
    }

    private fun loadPreferences() {
        viewLifecycleOwner.lifecycleScope.launch {
            val preferences = settingsRepository.getPreferences()

            if (preferences != null) {
                bindings.tvTheme.text = preferences.theme
                bindings.tvAutoLock.text = SettingsDefaults.autoLockText(preferences.autoLockMinutes)
            } else {
                bindings.tvTheme.text = SettingsDefaults.DEFAULT_THEME.name
                bindings.tvAutoLock.text = SettingsDefaults.autoLockText(SettingsDefaults.DEFAULT_AUTO_LOCK_MINUTES)
            }
        }
    }

    private fun saveSettings() {
        viewLifecycleOwner.lifecycleScope.launch {
            val theme = bindings.tvTheme.text.toString()
            val autoLockText = bindings.tvAutoLock.text.toString()
            val autoLockMinutes = SettingsDefaults.autoLockMinutes(autoLockText)

            settingsRepository.savePreferences(
                theme = theme,
                autoLockMinutes = autoLockMinutes
            )
            ThemeManager.applyTheme(Themes.valueOf(theme))

            Toast.makeText(requireContext(), "Settings saved", Toast.LENGTH_SHORT).show()
            Log.d("RoomDb", "Settings saved, $theme and $autoLockMinutes")
        }
    }

    private fun showAutoLockDialog() {
        AlertDialog.Builder(requireContext()).setTitle("Auto-lock").setItems(SettingsDefaults.autoLockOptions.keys.toTypedArray()) {
                _, which ->
            bindings.tvAutoLock.text = SettingsDefaults.autoLockOptions.keys.elementAt(which)
        }.show()
    }

    private fun showThemeDialog() {
        val themes = SettingsDefaults.themes

        AlertDialog.Builder(requireContext()).setTitle("Theme").setItems(themes.map { it.name }.toTypedArray()) {
                _, which ->
            bindings.tvTheme.text = themes[which].name
        }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bindings = null
    }
}

