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

    suspend fun savePreferences(
        theme: String,
        autoLockMinutes: Int
    ){
        val userId = authRepository.currentUserId()?: return

        val preferences = UserPreferences(
            userId = userId,
            theme = theme,
            autoLockMinutes = autoLockMinutes
        )

        userPreferencesDao.savePreferences(preferences)
    }
}