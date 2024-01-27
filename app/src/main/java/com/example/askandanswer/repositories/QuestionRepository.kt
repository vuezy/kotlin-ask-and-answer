package com.example.askandanswer.repositories

import com.example.askandanswer.DataStoreManager
import com.example.askandanswer.api.RetrofitInstance
import com.example.askandanswer.models.InvalidTokenException
import com.example.askandanswer.models.questions.GetQuestionByIdState
import com.example.askandanswer.models.questions.GetQuestionsState
import com.example.askandanswer.models.questions.QuestionOperationState
import com.example.askandanswer.models.questions.QuestionRequestBody
import com.example.askandanswer.models.questions.SaveQuestionState
import com.example.askandanswer.models.users.RefreshTokensState
import kotlinx.coroutines.flow.firstOrNull

object QuestionRepository {

    suspend fun getQuestions(title: String? = null, allowRefreshingTokens: Boolean = true): GetQuestionsState {
        val service = RetrofitInstance.getQuestionsService

        try {
            val accessToken = DataStoreManager.accessToken.firstOrNull() ?: throw InvalidTokenException
            val response = service.getQuestions("BEARER $accessToken", title)
            if (response is GetQuestionsState.AuthenticationError) throw InvalidTokenException
            return response
        } catch (e: InvalidTokenException) {
            if (allowRefreshingTokens) {
                return when (val response = UserRepository.refreshTokens()) {
                    is RefreshTokensState.Success -> getQuestions(title, false)
                    is RefreshTokensState.Error -> GetQuestionsState.AuthenticationError(response.msg)
                }
            }
            throw e
        }
    }

    suspend fun getQuestionsByUserId(allowRefreshingTokens: Boolean = true): GetQuestionsState {
        val service = RetrofitInstance.getQuestionsService

        try {
            val userId = DataStoreManager.id.firstOrNull() ?: throw InvalidTokenException
            val accessToken = DataStoreManager.accessToken.firstOrNull() ?: throw InvalidTokenException
            val response = service.getQuestions("BEARER $accessToken", userId = userId)
            if (response is GetQuestionsState.AuthenticationError) throw InvalidTokenException
            return response
        } catch (e: InvalidTokenException) {
            if (allowRefreshingTokens) {
                return when (val response = UserRepository.refreshTokens()) {
                    is RefreshTokensState.Success -> getQuestionsByUserId(false)
                    is RefreshTokensState.Error -> GetQuestionsState.AuthenticationError(response.msg)
                }
            }
            throw e
        }
    }

    suspend fun getQuestionById(id: Int, allowRefreshingTokens: Boolean = true): GetQuestionByIdState {
        val service = RetrofitInstance.getQuestionByIdService

        try {
            val accessToken = DataStoreManager.accessToken.firstOrNull() ?: throw InvalidTokenException
            val response = service.getQuestionById("BEARER $accessToken", id)
            if (response is GetQuestionByIdState.AuthenticationError) throw InvalidTokenException
            return response
        } catch (e: InvalidTokenException) {
            if (allowRefreshingTokens) {
                return when (val response = UserRepository.refreshTokens()) {
                    is RefreshTokensState.Success -> getQuestionById(id, false)
                    is RefreshTokensState.Error -> GetQuestionByIdState.AuthenticationError(response.msg)
                }
            }
            throw e
        }
    }

    suspend fun createQuestion(body: QuestionRequestBody, allowRefreshingTokens: Boolean = true): SaveQuestionState {
        val service = RetrofitInstance.saveQuestionService

        try {
            val accessToken = DataStoreManager.accessToken.firstOrNull() ?: throw InvalidTokenException
            val response = service.createQuestion("BEARER $accessToken", body)
            if (response is SaveQuestionState.AuthenticationError) throw InvalidTokenException
            return response
        } catch (e: InvalidTokenException) {
            if (allowRefreshingTokens) {
                return when (val response = UserRepository.refreshTokens()) {
                    is RefreshTokensState.Success -> createQuestion(body, false)
                    is RefreshTokensState.Error -> SaveQuestionState.AuthenticationError(response.msg)
                }
            }
            throw e
        }
    }

    suspend fun updateQuestion(id: Int, body: QuestionRequestBody, allowRefreshingTokens: Boolean = true): SaveQuestionState {
        val service = RetrofitInstance.saveQuestionService

        try {
            val accessToken = DataStoreManager.accessToken.firstOrNull() ?: throw InvalidTokenException
            val response = service.updateQuestion("BEARER $accessToken", id, body)
            if (response is SaveQuestionState.AuthenticationError) throw InvalidTokenException
            return response
        } catch (e: InvalidTokenException) {
            if (allowRefreshingTokens) {
                return when (val response = UserRepository.refreshTokens()) {
                    is RefreshTokensState.Success -> updateQuestion(id, body, false)
                    is RefreshTokensState.Error -> SaveQuestionState.AuthenticationError(response.msg)
                }
            }
            throw e
        }
    }

    suspend fun deleteQuestion(id: Int, allowRefreshingTokens: Boolean = true): QuestionOperationState {
        val service = RetrofitInstance.questionOperationService

        try {
            val accessToken = DataStoreManager.accessToken.firstOrNull() ?: throw InvalidTokenException
            val response = service.deleteQuestion("BEARER $accessToken", id)
            if (response is QuestionOperationState.AuthenticationError) throw InvalidTokenException
            return response
        } catch (e: InvalidTokenException) {
            if (allowRefreshingTokens) {
                return when (val response = UserRepository.refreshTokens()) {
                    is RefreshTokensState.Success -> deleteQuestion(id, false)
                    is RefreshTokensState.Error -> QuestionOperationState.AuthenticationError(response.msg)
                }
            }
            throw e
        }
    }

    suspend fun closeQuestion(id: Int, allowRefreshingTokens: Boolean = true): QuestionOperationState {
        val service = RetrofitInstance.questionOperationService

        try {
            val accessToken = DataStoreManager.accessToken.firstOrNull() ?: throw InvalidTokenException
            val response = service.closeQuestion("BEARER $accessToken", id)
            if (response is QuestionOperationState.AuthenticationError) throw InvalidTokenException
            return response
        } catch (e: InvalidTokenException) {
            if (allowRefreshingTokens) {
                return when (val response = UserRepository.refreshTokens()) {
                    is RefreshTokensState.Success -> closeQuestion(id, false)
                    is RefreshTokensState.Error -> QuestionOperationState.AuthenticationError(response.msg)
                }
            }
            throw e
        }
    }
}