package Emeris.PROG7314.trinitymobileclient.api.service

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface ServerApiService {

    @GET("api/v1/server/status")
    suspend fun getStatus(): Response<ResponseBody>

    @GET("api/v1/server/teamserverip")
    suspend fun getTeamServerIp(): Response<ResponseBody>
}
