package Emeris.PROG7314.trinitymobileclient.data.command.parser

import Emeris.PROG7314.trinitymobileclient.data.command.dto.RunDto
import Emeris.PROG7314.trinitymobileclient.data.db.entity.CommandType

object RunParser : CommandParser {
    override val keyword: String = CommandType.RUN
    override fun parse(args: List<String>): RunDto {
        require(args.isNotEmpty()) { "run requires a program" }
        return RunDto(
            program    = args[0],
            arguements = if (args.size > 1) args.drop(1).joinToString(" ") else null
        )
    }
}
