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
    // language, biometric and notifications came in with db version 2
    val language: String = SettingsDefaults.DEFAULT_LANGUAGE,
    val biometricEnabled: Boolean = SettingsDefaults.DEFAULT_BIOMETRIC_ENABLED,
    val notificationsEnabled: Boolean = SettingsDefaults.DEFAULT_NOTIFICATIONS_ENABLED
)
