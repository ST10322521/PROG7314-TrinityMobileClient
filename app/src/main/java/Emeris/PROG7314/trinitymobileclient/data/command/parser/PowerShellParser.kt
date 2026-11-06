package Emeris.PROG7314.trinitymobileclient.data.command.parser

import Emeris.PROG7314.trinitymobileclient.data.command.dto.PowerShellDto
import Emeris.PROG7314.trinitymobileclient.data.db.entity.CommandType

object PowerShellParser : CommandParser {
    override val keyword: String = CommandType.POWERSHELL
    override fun parse(args: List<String>): PowerShellDto {
        require(args.isNotEmpty()) { "powershell requires a commandlet" }
        return PowerShellDto(
            commandlet = args[0],
            arguements = if (args.size > 1) args.drop(1).joinToString(" ") else null
        )
    }
}
