package Emeris.PROG7314.trinitymobileclient

import Emeris.PROG7314.trinitymobileclient.settings.SettingsDefaults
import Emeris.PROG7314.trinitymobileclient.settings.Themes
import junit.framework.TestCase.assertEquals
import org.junit.Test

class SettingsDefaultsTest {

    @Test
    fun defaultTheme_isAuto() {
        assertEquals(
            Themes.Auto,
            SettingsDefaults.DEFAULT_THEME
        )
    }

    @Test
    fun default_isFiveMinutes(){
        assertEquals(
            5,
            SettingsDefaults.DEFAULT_AUTO_LOCK_MINUTES
        )
    }

    @Test
    fun autoLockText_returnsCorrectLabels() {
        assertEquals("1 min", SettingsDefaults.autoLockText(1))
        assertEquals("5 min", SettingsDefaults.autoLockText(5))
        assertEquals("10 min", SettingsDefaults.autoLockText(10))
        assertEquals("15 min", SettingsDefaults.autoLockText(15))
        assertEquals("30 min", SettingsDefaults.autoLockText(30))
        assertEquals("Never", SettingsDefaults.autoLockText(0))
    }

    @Test
    fun autoLockText_returnsDefaultForUnknownValue(){
        assertEquals(
            "5 min",
            SettingsDefaults.autoLockText(999)
        )
    }

    @Test
    fun autoLockMinutes_returnsCorrectValues() {
        assertEquals(1, SettingsDefaults.autoLockMinutes("1 min"))
        assertEquals(5, SettingsDefaults.autoLockMinutes("5 min"))
        assertEquals(10, SettingsDefaults.autoLockMinutes("10 min"))
        assertEquals(15, SettingsDefaults.autoLockMinutes("15 min"))
        assertEquals(30, SettingsDefaults.autoLockMinutes("30 min"))
        assertEquals(0, SettingsDefaults.autoLockMinutes("Never"))
    }

    @Test
    fun autoLockMinutes_returnsDefaultForUnknownText() {
        assertEquals(
            5,
            SettingsDefaults.autoLockMinutes("Invalid")
        )
    }
}