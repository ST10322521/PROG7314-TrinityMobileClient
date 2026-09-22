package Emeris.PROG7314.trinitymobileclient.settings

import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {
     fun applyTheme(theme: String) {
        when(theme) {
            "Auto" -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            )
            "Dark" -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES
            )
            "Light" -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }
}