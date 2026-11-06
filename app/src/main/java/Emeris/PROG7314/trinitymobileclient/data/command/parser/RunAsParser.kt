package Emeris.PROG7314.trinitymobileclient.data.command.parser

import Emeris.PROG7314.trinitymobileclient.data.command.dto.RunAsDto
import Emeris.PROG7314.trinitymobileclient.data.db.entity.CommandType

object RunAsParser : CommandParser {
    override val keyword: String = CommandType.RUNAS
    override fun parse(args: List<String>): RunAsDto {
        require(args.size >= 3) { "runas requires DOMAIN\\user, password, and command" }
        return RunAsDto(
            domainUser = args[0],
            password   = args[1],
            command    = args[2],
            arguements = if (args.size > 3) args.drop(3).joinToString(" ") else null
        )
    }
}
