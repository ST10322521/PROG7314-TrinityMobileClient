package Emeris.PROG7314.trinitymobileclient.settings

import Emeris.PROG7314.trinitymobileclient.auth.AuthRepository
import Emeris.PROG7314.trinitymobileclient.database.AppDatabase

class SettingsRepository(
    private val authRepository: AuthRepository,
    private val database: AppDatabase
) {
    private val userPreferencesDao = database.userPreferencesDao()

    suspend fun getPreferences(): UserPreferences? {
        val userId = authRepository.currentUserId()?: return null

        return userPreferencesDao.getPreferences(userId)
    }

    // false when nobody is signed in. the screen says why nothing saved
    suspend fun savePreferences(
        theme: String,
        autoLockMinutes: Int,
        language: String = SettingsDefaults.DEFAULT_LANGUAGE,
        biometricEnabled: Boolean = SettingsDefaults.DEFAULT_BIOMETRIC_ENABLED,
        notificationsEnabled: Boolean = SettingsDefaults.DEFAULT_NOTIFICATIONS_ENABLED
    ): Boolean {
        val userId = authRepository.currentUserId()?: return false

        val preferences = UserPreferences(
            userId = userId,
            theme = theme,
            autoLockMinutes = autoLockMinutes,
            language = language,
            biometricEnabled = biometricEnabled,
            notificationsEnabled = notificationsEnabled
        )

        userPreferencesDao.savePreferences(preferences)
        return true
    }
}
