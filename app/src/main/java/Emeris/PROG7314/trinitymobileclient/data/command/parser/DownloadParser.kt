package Emeris.PROG7314.trinitymobileclient.data.command.parser

import Emeris.PROG7314.trinitymobileclient.data.command.dto.DownloadDTO
import Emeris.PROG7314.trinitymobileclient.data.db.entity.CommandType

object DownloadParser : CommandParser {
    override val keyword: String = CommandType.DOWNLOAD
    override fun parse(args: List<String>): DownloadDTO {
        require(args.isNotEmpty()) { "download requires a source path" }
        return DownloadDTO(
            path            = args[0],
            destinationPath = if (args.size > 1) args[1] else "",
            data            = "",
            offset          = 0L,
            final           = false
        )
    }
}
