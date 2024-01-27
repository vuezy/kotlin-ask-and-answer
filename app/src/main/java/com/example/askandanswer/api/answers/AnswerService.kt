package com.example.askandanswer.api.answers

import com.example.askandanswer.models.answers.AnswerRequestBody
import com.example.askandanswer.models.answers.CreateAnswerState
import com.example.askandanswer.models.answers.DeleteAnswerState
import com.example.askandanswer.models.answers.GetAnswersState
import com.example.askandanswer.models.answers.UpdateAnswerState
import com.example.askandanswer.models.answers.VoteAnswerState
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

object AnswerService {

    interface GetAnswers {
        @GET("question/{id}/answers")
        suspend fun getAnswersByQuestionId(
            @Header("Authorization") token: String,
            @Path("id") questionId: Int
        ): GetAnswersState

        @GET("answers")
        suspend fun getAnswersByUserId(
            @Header("Authorization") token: String
        ): GetAnswersState
    }

    interface CreateAnswer {
        @POST("answer")
        suspend fun createAnswer(
            @Header("Authorization") token: String,
            @Body body: AnswerRequestBody
        ): CreateAnswerState
    }

    interface UpdateAnswer {
        @PATCH("answer/{id}")
        suspend fun updateAnswer(
            @Header("Authorization") token: String,
            @Path("id") id: Int,
            @Body body: AnswerRequestBody
        ): UpdateAnswerState
    }

    interface DeleteAnswer {
        @DELETE("answer/{id}")
        suspend fun deleteAnswer(
            @Header("Authorization") token: String,
            @Path("id") id: Int
        ): DeleteAnswerState
    }


    interface VoteAnswer {
        @PATCH("answer/{id}/upvote")
        suspend fun upvoteAnswer(
            @Header("Authorization") token: String,
            @Path("id") id: Int
        ): VoteAnswerState

        @PATCH("answer/{id}/downvote")
        suspend fun downvoteAnswer(
            @Header("Authorization") token: String,
            @Path("id") id: Int
        ): VoteAnswerState
    }
}