package com.example.askandanswer.models.questions

import com.squareup.moshi.Json

data class Question(
    val id: Int,
    val title: String,
    val body: String,
    @Json(name = "priority_level") val priorityLevel: Int,
    val closed: Boolean,
    @Json(name = "updated_at") val updatedAt: String,
    @Json(name = "user_id") val userId: Int?,
    @Json(name = "name") val userName: String?,
    @Json(name = "email") val userEmail: String?,
)

data class QuestionValidationError(
    val title: String?,
    val body: String?,
    @Json(name = "priority_level") val priorityLevel: String?,
)

sealed class GetQuestionsState {
    data class Success(val questions: List<Question>) : GetQuestionsState()
    data class Error(val msg: String) : GetQuestionsState()
    data class AuthenticationError(val msg: String) : GetQuestionsState()
    object Loading : GetQuestionsState()
}

sealed class GetQuestionByIdState {
    data class Success(val question: Question) : GetQuestionByIdState()
    data class Error(val msg: String) : GetQuestionByIdState()
    data class AuthenticationError(val msg: String) : GetQuestionByIdState()
    object Loading : GetQuestionByIdState()
}

sealed class SaveQuestionState {
    data class Success(val msg: String) : SaveQuestionState()
    data class Error(val msg: String) : SaveQuestionState()
    data class AuthenticationError(val msg: String) : SaveQuestionState()
    data class ValidationError(@Json(name = "msg") val err: QuestionValidationError) : SaveQuestionState()
    object Loading : SaveQuestionState()
}

sealed class QuestionOperationState {
    data class Success(val msg: String) : QuestionOperationState()
    data class Error(val msg: String) : QuestionOperationState()
    data class AuthenticationError(val msg: String) : QuestionOperationState()
    object Loading : QuestionOperationState()
}