package Emeris.PROG7314.trinitymobileclient.api.service

import Emeris.PROG7314.trinitymobileclient.api.model.AgentDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST

interface AgentApiService {
    @GET("api/v1/agents")
    suspend fun getAgents(): Response<List<AgentDto>>

    @POST("api/v1/agents/checkin")
    suspend fun checkIn(): Response<Void>
}
