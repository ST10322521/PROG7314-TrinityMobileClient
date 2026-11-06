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
    val autoLockMinutes: Int
)