package com.example.askandanswer.api.questions

import com.example.askandanswer.models.questions.GetQuestionByIdState
import com.example.askandanswer.models.questions.GetQuestionsState
import com.example.askandanswer.models.questions.QuestionOperationState
import com.example.askandanswer.models.questions.QuestionRequestBody
import com.example.askandanswer.models.questions.SaveQuestionState
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

object QuestionService {

    interface GetQuestions {
        @GET("questions")
        suspend fun getQuestions(
            @Header("Authorization") token: String,
            @Query("title") title: String? = null,
            @Query("user_id") userId: Int? = null
        ): GetQuestionsState
    }

    interface GetQuestionById {
        @GET("question/{id}")
        suspend fun getQuestionById(
            @Header("Authorization") token: String,
            @Path("id") id: Int
        ): GetQuestionByIdState
    }

    interface SaveQuestion {
        @POST("question")
        suspend fun createQuestion(
            @Header("Authorization") token: String,
            @Body body: QuestionRequestBody
        ): SaveQuestionState

        @PUT("question/{id}")
        suspend fun updateQuestion(
            @Header("Authorization") token: String,
            @Path("id") id: Int,
            @Body body: QuestionRequestBody
        ): SaveQuestionState
    }

    interface QuestionOperation {
        @DELETE("question/{id}")
        suspend fun deleteQuestion(
            @Header("Authorization") token: String,
            @Path("id") id: Int
        ): QuestionOperationState

        @PATCH("question/{id}")
        suspend fun closeQuestion(
            @Header("Authorization") token: String,
            @Path("id") id: Int
        ): QuestionOperationState
    }
}