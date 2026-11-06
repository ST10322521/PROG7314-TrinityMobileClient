package Emeris.PROG7314.trinitymobileclient.settings

object SettingsDefaults {
    val DEFAULT_THEME = Themes.Auto
    const val DEFAULT_AUTO_LOCK_MINUTES = 5
    const val DEFAULT_LANGUAGE = "en"
    const val DEFAULT_BIOMETRIC_ENABLED = false
    const val DEFAULT_NOTIFICATIONS_ENABLED = true

    val autoLockOptions = AutoLockManager.autoLockOptions
    val themes = Themes.entries.toTypedArray()
    val languageOptions = LanguageManager.languageOptions

    fun autoLockText(minutes: Int): String {
        return autoLockOptions.entries.firstOrNull {
            it.value == minutes
        }?.key ?: "5 min"
    }

    fun autoLockMinutes(text: String): Int {
        return  autoLockOptions[text] ?: DEFAULT_AUTO_LOCK_MINUTES
    }

    // unknown code falls back to English
    fun languageText(code: String): String {
        return languageOptions.entries.firstOrNull {
            it.value == code
        }?.key ?: "English"
    }

    fun languageCode(text: String): String {
        return languageOptions[text] ?: DEFAULT_LANGUAGE
    }
}
