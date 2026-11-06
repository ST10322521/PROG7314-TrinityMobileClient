package Emeris.PROG7314.trinitymobileclient.data.command.parser

import Emeris.PROG7314.trinitymobileclient.data.command.dto.KillDto
import Emeris.PROG7314.trinitymobileclient.data.db.entity.CommandType

object KillParser : CommandParser {
    override val keyword: String = CommandType.KILL
    override fun parse(args: List<String>): KillDto = KillDto()
}
