package Emeris.PROG7314.trinitymobileclient.data.command.parser

import Emeris.PROG7314.trinitymobileclient.data.command.dto.RunUDto
import Emeris.PROG7314.trinitymobileclient.data.db.entity.CommandType

object RunUParser : CommandParser {
    override val keyword: String = CommandType.RUNU
    override fun parse(args: List<String>): RunUDto {
        require(args.size >= 2) { "runu requires a PID and command" }
        val pid = requireNotNull(args[0].toIntOrNull()) { "runu: PID must be an integer, got '${args[0]}'" }
        return RunUDto(
            pid        = pid,
            command    = args[1],
            arguements = if (args.size > 2) args.drop(2).joinToString(" ") else null
        )
    }
}
