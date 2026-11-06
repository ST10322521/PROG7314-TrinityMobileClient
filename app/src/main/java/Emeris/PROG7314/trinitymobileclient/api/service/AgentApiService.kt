package Emeris.PROG7314.trinitymobileclient.api.service

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST

interface AgentApiService {
    @GET("api/v1/agents")
    suspend fun getAgents(): Response<ResponseBody>

    @POST("api/v1/agents/checkin")
    suspend fun checkIn(): Response<ResponseBody>
}
