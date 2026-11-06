package Emeris.PROG7314.trinitymobileclient.data.command.parser

import Emeris.PROG7314.trinitymobileclient.data.command.dto.InlineExecuteDto
import Emeris.PROG7314.trinitymobileclient.data.db.entity.CommandType

object InlineExecuteParser : CommandParser {
    override val keyword: String = CommandType.EXECUTE_ASSEMBLY
    override fun parse(args: List<String>): InlineExecuteDto {
        require(args.isNotEmpty()) { "inlineexecute requires an assembly path" }
        return InlineExecuteDto(
            assemblyPath = args[0],
            arguments    = if (args.size > 1) args.drop(1).joinToString(" ") else null
        )
    }
}
