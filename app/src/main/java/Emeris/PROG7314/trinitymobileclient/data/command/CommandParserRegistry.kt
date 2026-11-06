package Emeris.PROG7314.trinitymobileclient.data.command

import Emeris.PROG7314.trinitymobileclient.data.command.parser.CmdParser
import Emeris.PROG7314.trinitymobileclient.data.command.parser.CommandParser
import Emeris.PROG7314.trinitymobileclient.data.command.parser.DownloadParser
import Emeris.PROG7314.trinitymobileclient.data.command.parser.EscalateParser
import Emeris.PROG7314.trinitymobileclient.data.command.parser.GetUIDParser
import Emeris.PROG7314.trinitymobileclient.data.command.parser.InlineExecuteParser
import Emeris.PROG7314.trinitymobileclient.data.command.parser.KillParser
import Emeris.PROG7314.trinitymobileclient.data.command.parser.KillProcessParser
import Emeris.PROG7314.trinitymobileclient.data.command.parser.ParseResult
import Emeris.PROG7314.trinitymobileclient.data.command.parser.PersistParser
import Emeris.PROG7314.trinitymobileclient.data.command.parser.PowerShellParser
import Emeris.PROG7314.trinitymobileclient.data.command.parser.RunAsParser
import Emeris.PROG7314.trinitymobileclient.data.command.parser.RunParser
import Emeris.PROG7314.trinitymobileclient.data.command.parser.RunUParser
import Emeris.PROG7314.trinitymobileclient.data.command.parser.SleepParser
import Emeris.PROG7314.trinitymobileclient.data.command.parser.SpawnToParser
import Emeris.PROG7314.trinitymobileclient.data.command.parser.UpdateHostsParser

object CommandParserRegistry {

    private val parsers: Map<String, CommandParser> = buildMap {
        register(PowerShellParser)
        register(CmdParser)
        register(DownloadParser)
        register(KillProcessParser)
        register(RunAsParser)
        register(RunParser)
        register(RunUParser)
        register(EscalateParser)
        register(PersistParser)
        register(InlineExecuteParser)
        register(SpawnToParser)
        register(UpdateHostsParser)
        register(SleepParser)
        register(KillParser)
        register(GetUIDParser)
    }

    fun parse(rawInput: String): ParseResult {
        if (rawInput.isBlank()) {
            return ParseResult.ParseError("Input is empty")
        }

        val tokens  = rawInput.trim().split("\\s+".toRegex())
        val keyword = tokens.first().lowercase()
        val args    = tokens.drop(1)

        val parser = parsers[keyword]
            ?: return ParseResult.UnknownCommand(keyword)

        return try {
            ParseResult.Success(
                commandType = keyword,
                dto         = parser.parse(args)
            )
        } catch (e: IllegalArgumentException) {
            ParseResult.ParseError(e.message ?: "Failed to parse '$keyword' command")
        }
    }

    fun registeredKeywords(): Set<String> = parsers.keys

    private fun MutableMap<String, CommandParser>.register(parser: CommandParser) {
        put(parser.keyword, parser)
    }
}
