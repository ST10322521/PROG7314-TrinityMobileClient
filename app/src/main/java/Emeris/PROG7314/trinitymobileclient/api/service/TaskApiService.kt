package Emeris.PROG7314.trinitymobileclient.api.service

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface TaskApiService {

    @GET("api/v1/tasks/tasks")
    suspend fun getAllTasks(): Response<ResponseBody>

    @GET("api/v1/tasks/tasks/{ID}")
    suspend fun getTask(@Path("ID") id: Int): Response<ResponseBody>

    @GET("api/v1/tasks/{agentID}")
    suspend fun getTasksForAgent(@Path("agentID") agentId: Int): Response<ResponseBody>

    @GET("api/v1/tasks/{agentID}/activeDownloads")
    suspend fun getActiveDownloads(@Path("agentID") agentId: Int): Response<ResponseBody>

    @DELETE("{taskID}/stop")
    suspend fun stopTask(@Path("taskID") taskId: Int): Response<ResponseBody>

    @DELETE("{agentID}/clearQueue")
    suspend fun clearQueue(@Path("agentID") agentId: Int): Response<ResponseBody>
}
