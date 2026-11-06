package Emeris.PROG7314.trinitymobileclient.data.command.parser

import Emeris.PROG7314.trinitymobileclient.data.command.dto.KillProcessDto
import Emeris.PROG7314.trinitymobileclient.data.db.entity.CommandType

object KillProcessParser : CommandParser {
    override val keyword: String = CommandType.KILL_PROCESS
    override fun parse(args: List<String>): KillProcessDto {
        require(args.isNotEmpty()) { "killprocess requires a PID" }
        val pid = requireNotNull(args[0].toIntOrNull()) { "killprocess: PID must be an integer, got '${args[0]}'" }
        return KillProcessDto(pid = pid)
    }
}
