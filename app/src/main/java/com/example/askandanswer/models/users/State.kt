package com.example.askandanswer.models.users

import com.squareup.moshi.Json

data class User(
    val id: Int,
    val name: String,
    val email: String,
)

data class AuthValidationError(
    val name: String?,
    val email: String?,
    val password: String?,
)

sealed class AuthState {
    data class Success(
        val user: User,
        @Json(name = "access_token") val accessToken: String,
        @Json(name = "refresh_token") val refreshToken: String
    ) : AuthState()

    data class Error(val msg: String) : AuthState()
    data class ValidationError(@Json(name = "msg") val err: AuthValidationError) : AuthState()
    object Loading : AuthState()
}

sealed class GetUserDataState {
    data class Success(val user: User) : GetUserDataState()
    data class Error(val msg: String) : GetUserDataState()
    data class AuthenticationError(val msg: String) : GetUserDataState()
    object Loading : GetUserDataState()
}

sealed class GetPointsAndCreditsState {
    data class Success(val points: Int, val credits: Int) : GetPointsAndCreditsState()
    data class Error(val msg: String) : GetPointsAndCreditsState()
    data class AuthenticationError(val msg: String) : GetPointsAndCreditsState()
    object Loading : GetPointsAndCreditsState()
}

sealed class RefreshTokensState {
    data class Success(
        @Json(name = "access_token") val accessToken: String,
        @Json(name = "refresh_token") val refreshToken: String
    ) : RefreshTokensState()

    data class Error(val msg: String)  : RefreshTokensState()
}

sealed class IsLoggedInState {
    object Success : IsLoggedInState()
    object Error : IsLoggedInState()
    object Loading : IsLoggedInState()
}

sealed class LogoutState {
    object Success : LogoutState()
    data class Error(val msg: String) : LogoutState()
    object Loading : LogoutState()
}