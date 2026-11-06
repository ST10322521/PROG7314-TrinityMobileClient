package Emeris.PROG7314.trinitymobileclient.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import Emeris.PROG7314.trinitymobileclient.data.db.converter.Converters
import Emeris.PROG7314.trinitymobileclient.data.db.dao.PendingCommandDao
import Emeris.PROG7314.trinitymobileclient.data.db.entity.PendingCommand

@Database(
    entities = [PendingCommand::class],
    version  = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CommandDatabase : RoomDatabase() {

    abstract fun commandDao(): PendingCommandDao

    companion object {
        private const val DB_NAME = "trinity_commands"

        @Volatile
        private var instance: CommandDatabase? = null

        fun getInstance(context: Context): CommandDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    CommandDatabase::class.java,
                    DB_NAME
                ).build().also { instance = it }
            }
    }
}
