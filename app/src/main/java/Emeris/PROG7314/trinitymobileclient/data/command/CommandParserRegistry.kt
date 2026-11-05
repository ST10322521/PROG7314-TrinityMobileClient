package Emeris.PROG7314.trinitymobileclient.data.command

import Emeris.PROG7314.trinitymobileclient.data.command.parser.CommandParser
import Emeris.PROG7314.trinitymobileclient.data.command.parser.ParseResult
import Emeris.PROG7314.trinitymobileclient.data.command.parser.ShellCommandParser

/**
 * Static registry that maps console input to the correct [CommandParser] strategy.
 */
object CommandParserRegistry {

    /**
     * All registered parsers keyed by command keyword.
     */
    private val parsers: Map<String, CommandParser> = buildMap {
        register(ShellCommandParser)
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Parse a raw console input string into a [ParseResult].
     *
     * The first whitespace-delimited token is treated as the keyword. The
     * remaining tokens are passed to the matching [CommandParser] as args.
     *
     * @param rawInput The exact string the user typed in the terminal
     */
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

    /**
     * Returns all registered keywords.
     * Command help list.
     */
    fun registeredKeywords(): Set<String> = parsers.keys
    private fun MutableMap<String, CommandParser>.register(parser: CommandParser) {
        put(parser.keyword, parser)
    }
}
