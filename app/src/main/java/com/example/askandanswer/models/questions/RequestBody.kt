package com.example.askandanswer.models.questions

import com.squareup.moshi.Json

data class QuestionRequestBody(
    val title: String,
    val body: String,
    @Json(name = "priority_level") val priorityLevel: Int
)