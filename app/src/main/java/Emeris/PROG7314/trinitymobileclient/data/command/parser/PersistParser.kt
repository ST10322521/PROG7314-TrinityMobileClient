package Emeris.PROG7314.trinitymobileclient.data.command.parser

import Emeris.PROG7314.trinitymobileclient.data.command.dto.PersistDto
import Emeris.PROG7314.trinitymobileclient.data.db.entity.CommandType

object PersistParser : CommandParser {
    override val keyword: String = CommandType.PERSIST
    override fun parse(args: List<String>): PersistDto {
        require(args.size >= 3) { "persist requires a binary path, username, and task name" }
        return PersistDto(path = args[0], username = args[1], taskName = args[2])
    }
}
