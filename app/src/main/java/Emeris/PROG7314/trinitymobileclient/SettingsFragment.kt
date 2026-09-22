package Emeris.PROG7314.trinitymobileclient

import Emeris.PROG7314.trinitymobileclient.databinding.FragmentSettingsBinding
import Emeris.PROG7314.trinitymobileclient.settings.SettingsRepository
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import Emeris.PROG7314.trinitymobileclient.settings.SettingsProvider
import Emeris.PROG7314.trinitymobileclient.settings.ThemeManager
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

    private val autoLockOptions = mapOf(
        "1 min" to 1,
        "5 min" to 5,
        "10 min" to 10,
        "15 min" to 15,
        "30 min" to 30,
        "Never" to 0
    )

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

            preferences?.let {
                bindings.tvTheme.text = it.theme
                bindings.tvAutoLock.text = autoLockOptions.entries.firstOrNull { entry -> entry.value == it.autoLockMinutes } ?.key ?: "5 min"
            }
        }
    }

    private fun saveSettings() {
        viewLifecycleOwner.lifecycleScope.launch {
            val theme = bindings.tvTheme.text.toString()
            val autoLockText = bindings.tvAutoLock.text.toString()
            val autoLockMinutes = autoLockOptions[autoLockText] ?: 5

            settingsRepository.savePreferences(
                theme = theme,
                autoLockMinutes = autoLockMinutes
            )
            ThemeManager.applyTheme(theme)

            Toast.makeText(requireContext(), "Settings saved", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAutoLockDialog() {
        AlertDialog.Builder(requireContext()).setTitle("Auto-lock").setItems(autoLockOptions.keys.toTypedArray()) {
                _, which ->
            bindings.tvAutoLock.text = autoLockOptions.keys.elementAt(which)
        }.show()
    }

    private fun showThemeDialog() {
        val themes = arrayOf("Auto", "Dark", "Light")

        AlertDialog.Builder(requireContext()).setTitle("Theme").setItems(themes) {
                _, which ->
            val selectedTheme = themes[which]
            bindings.tvTheme.text = selectedTheme
        }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bindings = null
    }
}

