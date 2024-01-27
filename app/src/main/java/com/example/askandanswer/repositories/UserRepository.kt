package com.example.askandanswer.repositories

import com.example.askandanswer.DataStoreManager
import com.example.askandanswer.api.RetrofitInstance
import com.example.askandanswer.models.InvalidTokenException
import com.example.askandanswer.models.users.AuthState
import com.example.askandanswer.models.users.GetPointsAndCreditsState
import com.example.askandanswer.models.users.GetUserDataState
import com.example.askandanswer.models.users.IsLoggedInState
import com.example.askandanswer.models.users.LoginRequestBody
import com.example.askandanswer.models.users.RefreshTokensRequestBody
import com.example.askandanswer.models.users.RefreshTokensState
import com.example.askandanswer.models.users.RegisterRequestBody
import com.example.askandanswer.models.users.User
import kotlinx.coroutines.flow.firstOrNull

object UserRepository {

    suspend fun login(body: LoginRequestBody): AuthState {
        val service = RetrofitInstance.authService
        val response = service.login(body)
        if (response is AuthState.Success) {
            val user = response.user
            DataStoreManager.saveUserData(
                user.id,
                user.name,
                user.email,
                response.accessToken,
                response.refreshToken
            )
        }
        return response
    }

    suspend fun register(body: RegisterRequestBody): AuthState {
        val service = RetrofitInstance.authService
        val response = service.register(body)
        if (response is AuthState.Success) {
            val user = response.user
            DataStoreManager.saveUserData(
                user.id,
                user.name,
                user.email,
                response.accessToken,
                response.refreshToken
            )
        }
        return response
    }

    suspend fun getUserData(): GetUserDataState {
        val user = User(
            DataStoreManager.id.firstOrNull() ?: throw InvalidTokenException,
            DataStoreManager.name.firstOrNull() ?: throw InvalidTokenException,
            DataStoreManager.email.firstOrNull() ?: throw InvalidTokenException,
        )
        return GetUserDataState.Success(user)
    }

    suspend fun getPointsAndCredits(allowRefreshingTokens: Boolean = true): GetPointsAndCreditsState {
        val service = RetrofitInstance.getPointsAndCreditsService

        try {
            val accessToken = DataStoreManager.accessToken.firstOrNull() ?: throw InvalidTokenException
            val response = service.getPointsAndCredits("BEARER $accessToken")
            if (response is GetPointsAndCreditsState.AuthenticationError) throw InvalidTokenException
            return response
        } catch (e: InvalidTokenException) {
            if (allowRefreshingTokens) {
                return when (val response = refreshTokens()) {
                    is RefreshTokensState.Success -> getPointsAndCredits(false)
                    is RefreshTokensState.Error -> GetPointsAndCreditsState.AuthenticationError(response.msg)
                }
            }
            throw e
        }
    }

    suspend fun refreshTokens(): RefreshTokensState {
        val service = RetrofitInstance.refreshTokensService

        try {
            val refreshToken = DataStoreManager.refreshToken.firstOrNull() ?: throw InvalidTokenException
            val accessToken = DataStoreManager.accessToken.firstOrNull() ?: throw InvalidTokenException

            val response = service.refreshTokens("BEARER $refreshToken", RefreshTokensRequestBody(accessToken))
            if (response is RefreshTokensState.Success) {
                DataStoreManager.saveTokens(response.accessToken, response.refreshToken)
            }
            return response
        } catch (e: Exception) {
            throw InvalidTokenException
        }
    }

    suspend fun isLoggedIn(): IsLoggedInState {
        DataStoreManager.id.firstOrNull() ?: return IsLoggedInState.Error
        DataStoreManager.name.firstOrNull() ?: return IsLoggedInState.Error
        DataStoreManager.email.firstOrNull() ?: return IsLoggedInState.Error
        DataStoreManager.accessToken.firstOrNull() ?: return IsLoggedInState.Error
        DataStoreManager.refreshToken.firstOrNull() ?: return IsLoggedInState.Error
        return IsLoggedInState.Success
    }

    suspend fun logout() {
        DataStoreManager.clear()
    }
}