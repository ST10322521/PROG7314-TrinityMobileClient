package Emeris.PROG7314.trinitymobileclient.api.retrofit

import Emeris.PROG7314.trinitymobileclient.api.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.google.gson.annotations.SerializedName

import Emeris.PROG7314.trinitymobileclient.api.model.BofDTO
import Emeris.PROG7314.trinitymobileclient.api.model.CancelDownloadDTO
import Emeris.PROG7314.trinitymobileclient.api.model.DotNetAsmDTO
import Emeris.PROG7314.trinitymobileclient.api.model.DownloadDTO
import Emeris.PROG7314.trinitymobileclient.api.model.EscalateDTO
import Emeris.PROG7314.trinitymobileclient.api.model.KillProcessDTO
import Emeris.PROG7314.trinitymobileclient.api.model.PowerShellDTO
import Emeris.PROG7314.trinitymobileclient.api.model.RunAsDTO
import Emeris.PROG7314.trinitymobileclient.api.model.RunDTO
import Emeris.PROG7314.trinitymobileclient.api.model.RunUDTO
import Emeris.PROG7314.trinitymobileclient.api.model.ShellDTO
import Emeris.PROG7314.trinitymobileclient.api.model.UploadDTO

interface CommandApi {
    /**
     * POST api/v1/commands/{agentId}/execute/bof
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param agentId 
     * @param bofDTO  (optional)
     * @return [Unit]
     */
    @POST("api/v1/commands/{agentId}/execute/bof")
    suspend fun apiV1CommandsAgentIdExecuteBofPost(@Path("agentId") agentId: kotlin.Int, @Body bofDTO: BofDTO? = null): Response<Unit>

    /**
     * POST api/v1/commands/{agentId}/execute/cancelFileDownload
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param agentId 
     * @param cancelDownloadDTO  (optional)
     * @return [Unit]
     */
    @POST("api/v1/commands/{agentId}/execute/cancelFileDownload")
    suspend fun apiV1CommandsAgentIdExecuteCancelFileDownloadPost(@Path("agentId") agentId: kotlin.Int, @Body cancelDownloadDTO: CancelDownloadDTO? = null): Response<Unit>

    /**
     * POST api/v1/commands/{agentId}/execute/filedownload
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param agentId 
     * @param downloadDTO  (optional)
     * @return [Unit]
     */
    @POST("api/v1/commands/{agentId}/execute/filedownload")
    suspend fun apiV1CommandsAgentIdExecuteFiledownloadPost(@Path("agentId") agentId: kotlin.Int, @Body downloadDTO: DownloadDTO? = null): Response<Unit>

    /**
     * POST api/v1/commands/{agentId}/execute/upload
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param agentId 
     * @param uploadDTO  (optional)
     * @return [Unit]
     */
    @POST("api/v1/commands/{agentId}/execute/upload")
    suspend fun apiV1CommandsAgentIdExecuteUploadPost(@Path("agentId") agentId: kotlin.Int, @Body uploadDTO: UploadDTO? = null): Response<Unit>

    /**
     * POST api/v1/commands/{agentId}/spawn/dotnetassembly
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param agentId 
     * @param dotNetAsmDTO  (optional)
     * @return [Unit]
     */
    @POST("api/v1/commands/{agentId}/spawn/dotnetassembly")
    suspend fun apiV1CommandsAgentIdSpawnDotnetassemblyPost(@Path("agentId") agentId: kotlin.Int, @Body dotNetAsmDTO: DotNetAsmDTO? = null): Response<Unit>

    /**
     * POST api/v1/commands/{agentId}/spawn/escalate
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param agentId 
     * @param escalateDTO  (optional)
     * @return [Unit]
     */
    @POST("api/v1/commands/{agentId}/spawn/escalate")
    suspend fun apiV1CommandsAgentIdSpawnEscalatePost(@Path("agentId") agentId: kotlin.Int, @Body escalateDTO: EscalateDTO? = null): Response<Unit>

    /**
     * POST api/v1/commands/{agentId}/spawn/killprocess
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param agentId 
     * @param killProcessDTO  (optional)
     * @return [Unit]
     */
    @POST("api/v1/commands/{agentId}/spawn/killprocess")
    suspend fun apiV1CommandsAgentIdSpawnKillprocessPost(@Path("agentId") agentId: kotlin.Int, @Body killProcessDTO: KillProcessDTO? = null): Response<Unit>

    /**
     * POST api/v1/commands/{agentId}/spawn/powershell
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param agentId 
     * @param powerShellDTO  (optional)
     * @return [Unit]
     */
    @POST("api/v1/commands/{agentId}/spawn/powershell")
    suspend fun apiV1CommandsAgentIdSpawnPowershellPost(@Path("agentId") agentId: kotlin.Int, @Body powerShellDTO: PowerShellDTO? = null): Response<Unit>

    /**
     * POST api/v1/commands/{agentId}/spawn/run
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param agentId 
     * @param runDTO  (optional)
     * @return [Unit]
     */
    @POST("api/v1/commands/{agentId}/spawn/run")
    suspend fun apiV1CommandsAgentIdSpawnRunPost(@Path("agentId") agentId: kotlin.Int, @Body runDTO: RunDTO? = null): Response<Unit>

    /**
     * POST api/v1/commands/{agentId}/spawn/runas
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param agentId 
     * @param runAsDTO  (optional)
     * @return [Unit]
     */
    @POST("api/v1/commands/{agentId}/spawn/runas")
    suspend fun apiV1CommandsAgentIdSpawnRunasPost(@Path("agentId") agentId: kotlin.Int, @Body runAsDTO: RunAsDTO? = null): Response<Unit>

    /**
     * POST api/v1/commands/{agentId}/spawn/runu
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param agentId 
     * @param runUDTO  (optional)
     * @return [Unit]
     */
    @POST("api/v1/commands/{agentId}/spawn/runu")
    suspend fun apiV1CommandsAgentIdSpawnRunuPost(@Path("agentId") agentId: kotlin.Int, @Body runUDTO: RunUDTO? = null): Response<Unit>

    /**
     * POST api/v1/commands/{agentId}/spawn/shell
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param agentId 
     * @param shellDTO  (optional)
     * @return [Unit]
     */
    @POST("api/v1/commands/{agentId}/spawn/shell")
    suspend fun apiV1CommandsAgentIdSpawnShellPost(@Path("agentId") agentId: kotlin.Int, @Body shellDTO: ShellDTO? = null): Response<Unit>

}
