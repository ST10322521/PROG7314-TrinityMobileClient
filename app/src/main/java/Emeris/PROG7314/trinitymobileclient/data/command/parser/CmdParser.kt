package Emeris.PROG7314.trinitymobileclient.data.command.parser

import Emeris.PROG7314.trinitymobileclient.data.command.dto.CmdDto
import Emeris.PROG7314.trinitymobileclient.data.db.entity.CommandType

object CmdParser : CommandParser {
    override val keyword: String = CommandType.SHELL
    override fun parse(args: List<String>): CmdDto {
        require(args.isNotEmpty()) { "cmd requires a command" }
        return CmdDto(command = args.joinToString(" "))
    }
}
