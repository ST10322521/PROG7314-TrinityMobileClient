package Emeris.PROG7314.trinitymobileclient.settings

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

/**
 * developer.android.com/training/data-storage/room
 */
@Dao
interface UserPreferencesDao {

    @Query("SELECT * FROM user_preferences WHERE userId = :userId")
    suspend fun getPreferences(userId: String): UserPreferences?

    @Upsert
    suspend fun savePreferences(preferences: UserPreferences)
}