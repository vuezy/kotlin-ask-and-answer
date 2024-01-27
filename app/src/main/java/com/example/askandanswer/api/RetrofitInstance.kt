package com.example.askandanswer.api

import com.example.askandanswer.MyApplication
import com.example.askandanswer.api.answers.AnswerService
import com.example.askandanswer.api.answers.AnswerServiceAdapter
import com.example.askandanswer.api.questions.QuestionService
import com.example.askandanswer.api.questions.QuestionServiceAdapter
import com.example.askandanswer.api.users.UserServiceAdapter
import com.example.askandanswer.api.users.UserService
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstance {
    private val BASE_URL = MyApplication.env["SERVER_BASE_URL"]

    private object StatusCodeInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val response = chain.proceed(request)

            if (!response.isSuccessful) {
                return response.newBuilder().code(200).build()
            }
            return response
        }
    }

    private fun <T : Any> createService(serviceClass: Class<T>, adapter: JsonAdapter.Factory): T {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .client(
                OkHttpClient.Builder().addInterceptor(StatusCodeInterceptor).build()
            )
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder()
                        .add(adapter)
                        .addLast(KotlinJsonAdapterFactory())
                        .build()
                )
            )
            .build()
            .create(serviceClass)
    }

    val authService: UserService.Auth by lazy {
        createService(UserService.Auth::class.java, UserServiceAdapter.polymorphicAuthState)
    }

    val refreshTokensService: UserService.RefreshTokens by lazy {
        createService(UserService.RefreshTokens::class.java, UserServiceAdapter.polymorphicRefreshTokensState)
    }

    val getPointsAndCreditsService: UserService.GetPointsAndCredits by lazy {
        createService(UserService.GetPointsAndCredits::class.java, UserServiceAdapter.polymorphicGetPointsAndCreditsState)
    }

    val getQuestionsService: QuestionService.GetQuestions by lazy {
        createService(QuestionService.GetQuestions::class.java, QuestionServiceAdapter.polymorphicGetQuestionsState)
    }

    val getQuestionByIdService: QuestionService.GetQuestionById by lazy {
        createService(QuestionService.GetQuestionById::class.java, QuestionServiceAdapter.polymorphicGetQuestionByIdState)
    }

    val saveQuestionService: QuestionService.SaveQuestion by lazy {
        createService(QuestionService.SaveQuestion::class.java, QuestionServiceAdapter.polymorphicSaveQuestionState)
    }

    val questionOperationService: QuestionService.QuestionOperation by lazy {
        createService(QuestionService.QuestionOperation::class.java, QuestionServiceAdapter.polymorphicQuestionOperationState)
    }

    val getAnswersService: AnswerService.GetAnswers by lazy {
        createService(AnswerService.GetAnswers::class.java, AnswerServiceAdapter.polymorphicGetAnswersState)
    }

    val createAnswerService: AnswerService.CreateAnswer by lazy {
        createService(AnswerService.CreateAnswer::class.java, AnswerServiceAdapter.polymorphicCreateAnswerState)
    }

    val updateAnswerService: AnswerService.UpdateAnswer by lazy {
        createService(AnswerService.UpdateAnswer::class.java, AnswerServiceAdapter.polymorphicUpdateAnswerState)
    }

    val deleteAnswerService: AnswerService.DeleteAnswer by lazy {
        createService(AnswerService.DeleteAnswer::class.java, AnswerServiceAdapter.polymorphicDeleteAnswerState)
    }

    val voteAnswerService: AnswerService.VoteAnswer by lazy {
        createService(AnswerService.VoteAnswer::class.java, AnswerServiceAdapter.polymorphicVoteAnswerState)
    }
}
