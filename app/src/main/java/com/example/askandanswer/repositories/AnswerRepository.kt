package com.example.askandanswer.repositories

import com.example.askandanswer.DataStoreManager
import com.example.askandanswer.api.RetrofitInstance
import com.example.askandanswer.models.InvalidTokenException
import com.example.askandanswer.models.answers.AnswerRequestBody
import com.example.askandanswer.models.answers.CreateAnswerState
import com.example.askandanswer.models.answers.DeleteAnswerState
import com.example.askandanswer.models.answers.GetAnswersState
import com.example.askandanswer.models.answers.UpdateAnswerState
import com.example.askandanswer.models.answers.VoteAnswerState
import com.example.askandanswer.models.users.RefreshTokensState
import kotlinx.coroutines.flow.firstOrNull

object AnswerRepository {

    suspend fun getAnswersByQuestionId(questionId: Int, allowRefreshingTokens: Boolean = true): GetAnswersState {
        val service = RetrofitInstance.getAnswersService

        try {
            val accessToken = DataStoreManager.accessToken.firstOrNull() ?: throw InvalidTokenException
            val response = service.getAnswersByQuestionId("BEARER $accessToken", questionId)
            if (response is GetAnswersState.AuthenticationError) throw InvalidTokenException
            return response
        } catch (e: InvalidTokenException) {
            if (allowRefreshingTokens) {
                return when (val response = UserRepository.refreshTokens()) {
                    is RefreshTokensState.Success -> getAnswersByQuestionId(questionId, false)
                    is RefreshTokensState.Error -> GetAnswersState.AuthenticationError(response.msg)
                }
            }
            throw e
        }
    }

    suspend fun getAnswersByUserId(allowRefreshingTokens: Boolean = true): GetAnswersState {
        val service = RetrofitInstance.getAnswersService

        try {
            val accessToken = DataStoreManager.accessToken.firstOrNull() ?: throw InvalidTokenException
            val response = service.getAnswersByUserId("BEARER $accessToken")
            if (response is GetAnswersState.AuthenticationError) throw InvalidTokenException
            return response
        } catch (e: InvalidTokenException) {
            if (allowRefreshingTokens) {
                return when (val response = UserRepository.refreshTokens()) {
                    is RefreshTokensState.Success -> getAnswersByUserId(false)
                    is RefreshTokensState.Error -> GetAnswersState.AuthenticationError(response.msg)
                }
            }
            throw e
        }
    }

    suspend fun createAnswer(body: AnswerRequestBody, allowRefreshingTokens: Boolean = true): CreateAnswerState {
        val service = RetrofitInstance.createAnswerService

        try {
            val accessToken = DataStoreManager.accessToken.firstOrNull() ?: throw InvalidTokenException
            val response = service.createAnswer("BEARER $accessToken", body)
            if (response is CreateAnswerState.AuthenticationError) throw InvalidTokenException
            return response
        } catch (e: InvalidTokenException) {
            if (allowRefreshingTokens) {
                return when (val response = UserRepository.refreshTokens()) {
                    is RefreshTokensState.Success -> createAnswer(body, false)
                    is RefreshTokensState.Error -> CreateAnswerState.AuthenticationError(response.msg)
                }
            }
            throw e
        }
    }

    suspend fun updateAnswer(id: Int, body: AnswerRequestBody, allowRefreshingTokens: Boolean = true): UpdateAnswerState {
        val service = RetrofitInstance.updateAnswerService

        try {
            val accessToken = DataStoreManager.accessToken.firstOrNull() ?: throw InvalidTokenException
            val response = service.updateAnswer("BEARER $accessToken", id, body)
            if (response is UpdateAnswerState.AuthenticationError) throw InvalidTokenException
            return response
        } catch (e: InvalidTokenException) {
            if (allowRefreshingTokens) {
                return when (val response = UserRepository.refreshTokens()) {
                    is RefreshTokensState.Success -> updateAnswer(id, body, false)
                    is RefreshTokensState.Error -> UpdateAnswerState.AuthenticationError(response.msg)
                }
            }
            throw e
        }
    }

    suspend fun deleteAnswer(id: Int, allowRefreshingTokens: Boolean = true): DeleteAnswerState {
        val service = RetrofitInstance.deleteAnswerService

        try {
            val accessToken = DataStoreManager.accessToken.firstOrNull() ?: throw InvalidTokenException
            val response = service.deleteAnswer("BEARER $accessToken", id)
            if (response is DeleteAnswerState.AuthenticationError) throw InvalidTokenException
            return response
        } catch (e: InvalidTokenException) {
            if (allowRefreshingTokens) {
                return when (val response = UserRepository.refreshTokens()) {
                    is RefreshTokensState.Success -> deleteAnswer(id, false)
                    is RefreshTokensState.Error -> DeleteAnswerState.AuthenticationError(response.msg)
                }
            }
            throw e
        }
    }

    suspend fun voteAnswer(id: Int, upvote: Boolean, allowRefreshingTokens: Boolean = true): VoteAnswerState {
        val service = RetrofitInstance.voteAnswerService

        try {
            val accessToken = DataStoreManager.accessToken.firstOrNull() ?: throw InvalidTokenException
            val response = if (upvote) {
                service.upvoteAnswer("BEARER $accessToken", id)
            } else {
                service.downvoteAnswer(  "BEARER $accessToken", id)
            }
            if (response is VoteAnswerState.AuthenticationError) throw InvalidTokenException
            return response
        } catch (e: InvalidTokenException) {
            if (allowRefreshingTokens) {
                return when (val response = UserRepository.refreshTokens()) {
                    is RefreshTokensState.Success -> voteAnswer(id, upvote, false)
                    is RefreshTokensState.Error -> VoteAnswerState.AuthenticationError(response.msg)
                }
            }
            throw e
        }
    }
}