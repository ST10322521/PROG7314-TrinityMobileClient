package Emeris.PROG7314.trinitymobileclient.data.command.parser

import Emeris.PROG7314.trinitymobileclient.data.command.dto.SleepDto
import Emeris.PROG7314.trinitymobileclient.data.db.entity.CommandType

object SleepParser : CommandParser {
    override val keyword: String = CommandType.SLEEP
    override fun parse(args: List<String>): SleepDto {
        require(args.size >= 2) { "sleep requires seconds and jitter_percent" }
        val sleep  = requireNotNull(args[0].toIntOrNull()) { "sleep: seconds must be an integer, got '${args[0]}'" }
        val jitter = requireNotNull(args[1].toIntOrNull()) { "sleep: jitter_percent must be an integer, got '${args[1]}'" }
        return SleepDto(sleep = sleep, jitter = jitter)
    }
}
