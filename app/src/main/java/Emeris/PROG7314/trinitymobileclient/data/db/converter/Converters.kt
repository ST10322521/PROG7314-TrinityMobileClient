package Emeris.PROG7314.trinitymobileclient.data.db.converter

import androidx.room.TypeConverter
import Emeris.PROG7314.trinitymobileclient.data.db.entity.CommandStatus

class Converters {

    @TypeConverter
    fun fromCommandStatus(status: CommandStatus): String = status.name

    @TypeConverter
    fun toCommandStatus(value: String): CommandStatus = CommandStatus.valueOf(value)
}
