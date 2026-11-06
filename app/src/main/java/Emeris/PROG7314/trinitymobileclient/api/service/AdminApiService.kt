package Emeris.PROG7314.trinitymobileclient.api.service

import Emeris.PROG7314.trinitymobileclient.api.model.AdminRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AdminApiService {

    @GET("api/v1/admins")
    suspend fun getAdmins(): Response<ResponseBody>

    @POST("api/v1/admins")
    suspend fun createAdmin(@Body body: AdminRequest): Response<ResponseBody>

    @GET("api/v1/admins/{adminID}")
    suspend fun getAdmin(@Path("adminID") adminId: Int): Response<ResponseBody>

    @PUT("api/v1/admins/{adminID}")
    suspend fun updateAdmin(@Path("adminID") adminId: Int, @Body body: AdminRequest): Response<ResponseBody>

    @DELETE("api/v1/admins/{adminID}")
    suspend fun deleteAdmin(@Path("adminID") adminId: Int): Response<ResponseBody>
}
