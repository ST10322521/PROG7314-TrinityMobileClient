package Emeris.PROG7314.trinitymobileclient.settings

import Emeris.PROG7314.trinitymobileclient.auth.AuthProvider
import Emeris.PROG7314.trinitymobileclient.database.DatabaseProvider
import android.content.Context

object SettingsProvider {
    fun repository(context: Context): SettingsRepository {
        return SettingsRepository(
            AuthProvider.repository(),
            DatabaseProvider.getDatabase(context)
        )
    }
}