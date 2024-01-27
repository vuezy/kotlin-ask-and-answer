package com.example.askandanswer.models.answers

import com.squareup.moshi.Json

data class Answer(
    val id: Int,
    var body: String,
    var votes: Int,
    @Json(name = "question_id") val questionId: Int,
    @Json(name = "updated_at") val updatedAt: String,
    @Json(name = "user_id") val userId: Int,
    @Json(name = "name") val userName: String?,
    @Json(name = "email") val userEmail: String?,
)

data class AnswerValidationError(
    val body: String?,
    @Json(name = "question_id") val questionId: String?
)

sealed class GetAnswersState {
    data class Success(val answers: List<Answer>) : GetAnswersState()
    data class Error(val msg: String) : GetAnswersState()
    data class AuthenticationError(val msg: String) : GetAnswersState()
    object Loading : GetAnswersState()
}

sealed class CreateAnswerState {
    data class Success(val msg: String) : CreateAnswerState()
    data class Error(val msg: String) : CreateAnswerState()
    data class AuthenticationError(val msg: String) : CreateAnswerState()
    data class ValidationError(@Json(name = "msg") val err: AnswerValidationError) : CreateAnswerState()
    object Loading : CreateAnswerState()
}

sealed class UpdateAnswerState {
    data class Success(val msg: String) : UpdateAnswerState()
    data class Error(val msg: String) : UpdateAnswerState()
    data class AuthenticationError(val msg: String) : UpdateAnswerState()
    data class ValidationError(@Json(name = "msg") val err: AnswerValidationError) : UpdateAnswerState()
    object Loading : UpdateAnswerState()
}

sealed class DeleteAnswerState {
    data class Success(val msg: String) : DeleteAnswerState()
    data class Error(val msg: String) : DeleteAnswerState()
    data class AuthenticationError(val msg: String) : DeleteAnswerState()
    object Loading : DeleteAnswerState()
}

sealed class VoteAnswerState {
    data class Success(val msg: String, val votes: Int) : VoteAnswerState()
    data class Error(val msg: String) : VoteAnswerState()
    data class AuthenticationError(val msg: String) : VoteAnswerState()
    object Loading : VoteAnswerState()
}