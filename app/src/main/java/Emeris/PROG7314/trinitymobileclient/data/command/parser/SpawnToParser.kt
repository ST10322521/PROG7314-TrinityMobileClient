package Emeris.PROG7314.trinitymobileclient.data.command.parser

import Emeris.PROG7314.trinitymobileclient.data.command.dto.SpawnToDto
import Emeris.PROG7314.trinitymobileclient.data.db.entity.CommandType

object SpawnToParser : CommandParser {
    override val keyword: String = CommandType.SPAWNTO
    override fun parse(args: List<String>): SpawnToDto {
        require(args.isNotEmpty()) { "spawnto requires a binary path" }
        return SpawnToDto(path = args[0])
    }
}
