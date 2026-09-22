package Emeris.PROG7314.trinitymobileclient.model

import Emeris.PROG7314.trinitymobileclient.api.model.AgentDTO
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * UI model for a Trinity agent. Maps the generated [AgentDTO] from the OpenAPI client
 * (never hand-built API responses — the DTO comes from scripts/gen-openapi.sh).
 */
data class Agent(
    val id: Int,
    val name: String,
    val meta: String,
    val time: String,
    val online: Boolean,
    val uuid: String = "",
    val processName: String = "",
    val integrity: Int? = null,
    val status: String = "",
    val lastSeen: OffsetDateTime? = null,
    val campaignId: Int? = null,
    val listenerId: Int? = null,
    val payloadId: Int? = null,
)

private val lastSeenFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("HH:mm · dd MMM").withZone(ZoneOffset.UTC)

fun AgentDTO.toUiAgent(): Agent {
    val id = this.id ?: 0
    val status = this.status.orEmpty()
    return Agent(
        id = id,
        name = this.username ?: "agent-$id",
        meta = listOfNotNull(
            this.processName,
            this.integrity?.let { "integrity $it" },
        ).joinToString(" · ").ifEmpty { "no process" },
        time = this.lastSeen?.let { lastSeenFormatter.format(it) } ?: "—",
        online = status.equals("Online", true) || status.equals("Active", true),
        uuid = this.uuid.orEmpty(),
        processName = this.processName.orEmpty(),
        integrity = this.integrity,
        status = status,
        lastSeen = this.lastSeen,
        campaignId = this.campaignId,
        listenerId = this.listenerId,
        payloadId = this.payloadId,
    )
}
