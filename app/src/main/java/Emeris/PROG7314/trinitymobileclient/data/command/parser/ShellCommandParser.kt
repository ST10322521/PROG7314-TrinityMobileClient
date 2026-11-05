package Emeris.PROG7314.trinitymobileclient.data.command.parser

import Emeris.PROG7314.trinitymobileclient.data.command.dto.ShellCommandDto
import Emeris.PROG7314.trinitymobileclient.data.db.entity.CommandType

/**
 * Parses a shell command from the agent terminal.
 */
object ShellCommandParser : CommandParser {

    override val keyword: String = CommandType.SHELL

    override fun parse(args: List<String>): ShellCommandDto {
        require(args.isNotEmpty()) {
            "shell requires a commandlet — e.g. 'shell whoami'"
        }
        return ShellCommandDto(commandlet = args.joinToString(" "))
    }
}
