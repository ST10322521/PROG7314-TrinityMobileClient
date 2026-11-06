package Emeris.PROG7314.trinitymobileclient.settings

import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {
     fun applyTheme(theme: Themes) {
        when(theme) {
            Themes.Auto -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            )
            Themes.Dark -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES
            )
            Themes.Light -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }
}