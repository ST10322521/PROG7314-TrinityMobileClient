package Emeris.PROG7314.trinitymobileclient.api.retrofit

import Emeris.PROG7314.trinitymobileclient.api.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.google.gson.annotations.SerializedName


interface TaskApi {
    /**
     * DELETE {agentID}/clearQueue
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param agentID 
     * @return [Unit]
     */
    @DELETE("{agentID}/clearQueue")
    suspend fun agentIDClearQueueDelete(@Path("agentID") agentID: kotlin.Int): Response<Unit>

    /**
     * GET api/v1/tasks/{agentID}/activeDownloads
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param agentID 
     * @return [Unit]
     */
    @GET("api/v1/tasks/{agentID}/activeDownloads")
    suspend fun apiV1TasksAgentIDActiveDownloadsGet(@Path("agentID") agentID: kotlin.Int): Response<Unit>

    /**
     * GET api/v1/tasks/{agentID}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param agentID 
     * @return [Unit]
     */
    @GET("api/v1/tasks/{agentID}")
    suspend fun apiV1TasksAgentIDGet(@Path("agentID") agentID: kotlin.Int): Response<Unit>

    /**
     * GET api/v1/tasks/tasks
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [Unit]
     */
    @GET("api/v1/tasks/tasks")
    suspend fun apiV1TasksTasksGet(): Response<Unit>

    /**
     * GET {taskID}/stop
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param taskID 
     * @return [Unit]
     */
    @GET("{taskID}/stop")
    suspend fun taskIDStopGet(@Path("taskID") taskID: kotlin.Int): Response<Unit>

}
