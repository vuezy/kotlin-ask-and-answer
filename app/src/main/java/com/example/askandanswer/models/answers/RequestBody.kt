package com.example.askandanswer.models.answers

import com.squareup.moshi.Json

data class AnswerRequestBody(
    val body: String,
    @Json(name = "question_id") val questionId: Int
)