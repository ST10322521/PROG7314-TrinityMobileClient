package Emeris.PROG7314.trinitymobileclient.data.command.parser

import Emeris.PROG7314.trinitymobileclient.data.command.dto.EscalateDto
import Emeris.PROG7314.trinitymobileclient.data.db.entity.CommandType

object EscalateParser : CommandParser {
    override val keyword: String = CommandType.ESCALATE
    override fun parse(args: List<String>): EscalateDto {
        require(args.size >= 2) { "escalate requires a registry key and target binary" }
        return EscalateDto(regKey = args[0], targetBinary = args[1])
    }
}
