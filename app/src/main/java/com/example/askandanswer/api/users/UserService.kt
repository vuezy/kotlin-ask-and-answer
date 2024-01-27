package com.example.askandanswer.api.users

import com.example.askandanswer.models.users.GetPointsAndCreditsState
import com.example.askandanswer.models.users.RefreshTokensRequestBody
import com.example.askandanswer.models.users.RefreshTokensState
import com.example.askandanswer.models.users.RegisterRequestBody
import com.example.askandanswer.models.users.AuthState
import com.example.askandanswer.models.users.LoginRequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

object UserService {

    interface Auth {
        @POST("login")
        suspend fun login(@Body body: LoginRequestBody): AuthState

        @POST("register")
        suspend fun register(@Body body: RegisterRequestBody): AuthState
    }

    interface RefreshTokens {
        @POST("refresh")
        suspend fun refreshTokens(
            @Header("Authorization") refreshToken: String,
            @Body body: RefreshTokensRequestBody
        ): RefreshTokensState
    }

    interface GetPointsAndCredits {
        @GET("credits")
        suspend fun getPointsAndCredits(@Header("Authorization") token: String): GetPointsAndCreditsState
    }
}