package Emeris.PROG7314.trinitymobileclient.settings

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * developer.android.com/training/data-storage/room
 */
@Entity(tableName = "user_preferences")
data class UserPreferences(
    @PrimaryKey
    val userId: String,
    val theme: String,
    val autoLockMinutes: Int,
    //language code read by multi-language support (en / af / zu)
    val language: String = SettingsDefaults.DEFAULT_LANGUAGE,
    //read by biometric login
    val biometricEnabled: Boolean = SettingsDefaults.DEFAULT_BIOMETRIC_ENABLED,
    //read by push notifications
    val notificationsEnabled: Boolean = SettingsDefaults.DEFAULT_NOTIFICATIONS_ENABLED
)
