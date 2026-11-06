package Emeris.PROG7314.trinitymobileclient.data.command.parser

/**
 * Strategy interface
 */
interface CommandParser {

    /** The keyword this parser handles */
    val keyword: String

    /**
     * Parse the tokens that follow the keyword into a command DTO.
     *
     * @param args  Whitespace-split tokens after the keyword
     * @return      A populated DTO
     * @throws IllegalArgumentException if [args] don't satisfy the command's requirements
     */
    fun parse(args: List<String>): Any
}

// Parse result
sealed class ParseResult {

    /**
     * The input was recognised and parsed successfully.
     *
     * @param commandType The keyword string — use to key the DTO in the DB
     * @param dto         The parsed DTO — pass directly to [CommandRepository.queueCommand]
     */
    data class Success(
        val commandType: String,
        val dto: Any
    ) : ParseResult()

    data class UnknownCommand(val keyword: String) : ParseResult()
    data class ParseError(val message: String) : ParseResult()
}
