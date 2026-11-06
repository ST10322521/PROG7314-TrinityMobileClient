package Emeris.PROG7314.trinitymobileclient.data.command.parser

import Emeris.PROG7314.trinitymobileclient.data.command.dto.UpdateHostsDto
import Emeris.PROG7314.trinitymobileclient.data.db.entity.CommandType

object UpdateHostsParser : CommandParser {
    override val keyword: String = CommandType.UPDATE_HOSTS
    override fun parse(args: List<String>): UpdateHostsDto {
        require(args.isNotEmpty()) { "updatehosts requires a comma-separated host list" }
        val hosts = args[0].split(",").map { it.trim() }.filter { it.isNotEmpty() }
        require(hosts.isNotEmpty()) { "updatehosts requires at least one host" }
        return UpdateHostsDto(hosts = hosts)
    }
}
