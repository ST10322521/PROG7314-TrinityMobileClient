package Emeris.PROG7314.trinitymobileclient.api.service

import Emeris.PROG7314.trinitymobileclient.api.model.BofRequest
import Emeris.PROG7314.trinitymobileclient.api.model.CancelDownloadRequest
import Emeris.PROG7314.trinitymobileclient.api.model.UploadRequest
import Emeris.PROG7314.trinitymobileclient.data.command.dto.CmdDto
import Emeris.PROG7314.trinitymobileclient.data.command.dto.DownloadDTO
import Emeris.PROG7314.trinitymobileclient.data.command.dto.EscalateDto
import Emeris.PROG7314.trinitymobileclient.data.command.dto.GetUIDDto
import Emeris.PROG7314.trinitymobileclient.data.command.dto.InlineExecuteDto
import Emeris.PROG7314.trinitymobileclient.data.command.dto.KillDto
import Emeris.PROG7314.trinitymobileclient.data.command.dto.KillProcessDto
import Emeris.PROG7314.trinitymobileclient.data.command.dto.PersistDto
import Emeris.PROG7314.trinitymobileclient.data.command.dto.PowerShellDto
import Emeris.PROG7314.trinitymobileclient.data.command.dto.RunAsDto
import Emeris.PROG7314.trinitymobileclient.data.command.dto.RunDto
import Emeris.PROG7314.trinitymobileclient.data.command.dto.RunUDto
import Emeris.PROG7314.trinitymobileclient.data.command.dto.SleepDto
import Emeris.PROG7314.trinitymobileclient.data.command.dto.SpawnToDto
import Emeris.PROG7314.trinitymobileclient.data.command.dto.UpdateHostsDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface CommandApiService {

    @POST("api/v1/commands/{agentID}/spawn/powershell")
    suspend fun powershell(@Path("agentID") agentId: Int, @Body body: PowerShellDto): Response<ResponseBody>

    @POST("api/v1/commands/{agentID}/spawn/shell")
    suspend fun shell(@Path("agentID") agentId: Int, @Body body: CmdDto): Response<ResponseBody>

    @POST("api/v1/commands/{agentID}/spawn/runas")
    suspend fun runAs(@Path("agentID") agentId: Int, @Body body: RunAsDto): Response<ResponseBody>

    @POST("api/v1/commands/{agentID}/spawn/run")
    suspend fun run(@Path("agentID") agentId: Int, @Body body: RunDto): Response<ResponseBody>

    @POST("api/v1/commands/{agentID}/spawn/runu")
    suspend fun runU(@Path("agentID") agentId: Int, @Body body: RunUDto): Response<ResponseBody>

    @POST("api/v1/commands/{agentID}/execute/persist")
    suspend fun persist(@Path("agentID") agentId: Int, @Body body: PersistDto): Response<ResponseBody>

    @POST("api/v1/commands/{agentID}/execute/killprocess")
    suspend fun killProcess(@Path("agentID") agentId: Int, @Body body: KillProcessDto): Response<ResponseBody>

    @POST("api/v1/commands/{agentID}/execute/escalate")
    suspend fun escalate(@Path("agentID") agentId: Int, @Body body: EscalateDto): Response<ResponseBody>

    @POST("api/v1/commands/{agentID}/execute/dotnetassembly")
    suspend fun dotNetAssembly(@Path("agentID") agentId: Int, @Body body: InlineExecuteDto): Response<ResponseBody>

    @POST("api/v1/commands/{agentID}/execute/bof")
    suspend fun bof(@Path("agentID") agentId: Int, @Body body: BofRequest): Response<ResponseBody>

    @POST("api/v1/commands/{agentID}/execute/filedownload")
    suspend fun fileDownload(@Path("agentID") agentId: Int, @Body body: DownloadDTO): Response<ResponseBody>

    @POST("api/v1/commands/{agentID}/execute/cancelfiledownload")
    suspend fun cancelFileDownload(@Path("agentID") agentId: Int, @Body body: CancelDownloadRequest): Response<ResponseBody>

    @POST("api/v1/commands/{agentID}/execute/upload")
    suspend fun upload(@Path("agentID") agentId: Int, @Body body: UploadRequest): Response<ResponseBody>

    @POST("api/v1/commands/{agentID}/set/spawnto")
    suspend fun spawnTo(@Path("agentID") agentId: Int, @Body body: SpawnToDto): Response<ResponseBody>

    @POST("api/v1/commands/{agentID}/set/updatehosts")
    suspend fun updateHosts(@Path("agentID") agentId: Int, @Body body: UpdateHostsDto): Response<ResponseBody>

    @POST("api/v1/commands/{agentID}/set/sleep")
    suspend fun sleep(@Path("agentID") agentId: Int, @Body body: SleepDto): Response<ResponseBody>

    @POST("api/v1/commands/{agentID}/execute/kill")
    suspend fun kill(@Path("agentID") agentId: Int, @Body body: KillDto): Response<ResponseBody>

    @POST("api/v1/commands/{agentID}/execute/getuid")
    suspend fun getUid(@Path("agentID") agentId: Int, @Body body: GetUIDDto): Response<ResponseBody>

    @POST("api/v1/commands/{agentID}/execute/startkeylogger")
    suspend fun startKeylogger(@Path("agentID") agentId: Int): Response<ResponseBody>

    @POST("api/v1/commands/{agentID}/execute/stopkeylogger")
    suspend fun stopKeylogger(@Path("agentID") agentId: Int): Response<ResponseBody>
}
