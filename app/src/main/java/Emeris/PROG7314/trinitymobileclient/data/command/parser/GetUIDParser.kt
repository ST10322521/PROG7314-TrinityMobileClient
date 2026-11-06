package Emeris.PROG7314.trinitymobileclient.data.command.parser

import Emeris.PROG7314.trinitymobileclient.data.command.dto.GetUIDDto
import Emeris.PROG7314.trinitymobileclient.data.db.entity.CommandType

object GetUIDParser : CommandParser {
    override val keyword: String = CommandType.GETUID
    override fun parse(args: List<String>): GetUIDDto = GetUIDDto()
}
