package Emeris.PROG7314.trinitymobileclient.settings

object SettingsDefaults {
    val DEFAULT_THEME = Themes.Dark
    const val DEFAULT_AUTO_LOCK_MINUTES = 5

    val autoLockOptions = AutoLockManager.autoLockOptions
    val themes = Themes.entries.toTypedArray()

    fun autoLockText(minutes: Int): String {
        return autoLockOptions.entries.firstOrNull {
            it.value == minutes
        }?.key ?: "5 min"
    }

    fun autoLockMinutes(text: String): Int {
        return  autoLockOptions[text] ?: DEFAULT_AUTO_LOCK_MINUTES
    }
}