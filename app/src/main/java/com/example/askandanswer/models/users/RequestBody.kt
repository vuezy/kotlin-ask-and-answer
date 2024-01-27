package com.example.askandanswer.models.users

import com.squareup.moshi.Json

data class LoginRequestBody(
    val email: String,
    val password: String
)

data class RegisterRequestBody(
    val name: String,
    val email: String,
    val password: String
)

data class RefreshTokensRequestBody(
    @Json(name = "access_token") val accessToken: String
)