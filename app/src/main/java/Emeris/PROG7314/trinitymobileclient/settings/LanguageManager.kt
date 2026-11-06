package Emeris.PROG7314.trinitymobileclient.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

// picker label -> language code that gets saved
// labels are in there own language. they stay the same when translated
object LanguageManager {
    val languageOptions = mapOf(
        "English" to "en",
        "Afrikaans" to "af",
        "isiZulu" to "zu",
        "Sesotho" to "st"
    )

    fun applyLanguage(code: String) {
        val wanted = LocaleListCompat.forLanguageTags(code)
        if (AppCompatDelegate.getApplicationLocales().toLanguageTags() != wanted.toLanguageTags()) {
            AppCompatDelegate.setApplicationLocales(wanted)
        }
    }

    fun resetLanguage() {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
    }
}
