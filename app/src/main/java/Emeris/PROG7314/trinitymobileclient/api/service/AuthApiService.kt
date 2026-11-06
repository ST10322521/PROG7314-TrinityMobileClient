package Emeris.PROG7314.trinitymobileclient.api.service

import Emeris.PROG7314.trinitymobileclient.api.model.LoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/v1/auth/login")
    suspend fun login(@Body body: LoginRequest): Response<Void>
}
