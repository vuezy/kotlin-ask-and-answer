package com.example.askandanswer.api.questions

import com.example.askandanswer.models.questions.GetQuestionByIdState
import com.example.askandanswer.models.questions.GetQuestionsState
import com.example.askandanswer.models.questions.QuestionOperationState
import com.example.askandanswer.models.questions.SaveQuestionState
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory

object QuestionServiceAdapter {
    val polymorphicGetQuestionsState: PolymorphicJsonAdapterFactory<GetQuestionsState> by lazy {
        PolymorphicJsonAdapterFactory.of(GetQuestionsState::class.java, "type")
            .withSubtype(GetQuestionsState.Success::class.java, "success")
            .withSubtype(GetQuestionsState.Error::class.java, "error")
            .withSubtype(GetQuestionsState.AuthenticationError::class.java, "authentication_error")
    }

    val polymorphicGetQuestionByIdState: PolymorphicJsonAdapterFactory<GetQuestionByIdState> by lazy {
        PolymorphicJsonAdapterFactory.of(GetQuestionByIdState::class.java, "type")
            .withSubtype(GetQuestionByIdState.Success::class.java, "success")
            .withSubtype(GetQuestionByIdState.Error::class.java, "error")
            .withSubtype(GetQuestionByIdState.AuthenticationError::class.java, "authentication_error")
    }

    val polymorphicSaveQuestionState: PolymorphicJsonAdapterFactory<SaveQuestionState> by lazy {
        PolymorphicJsonAdapterFactory.of(SaveQuestionState::class.java, "type")
            .withSubtype(SaveQuestionState.Success::class.java, "success")
            .withSubtype(SaveQuestionState.Error::class.java, "error")
            .withSubtype(SaveQuestionState.AuthenticationError::class.java, "authentication_error")
            .withSubtype(SaveQuestionState.ValidationError::class.java, "validation_error")
    }

    val polymorphicQuestionOperationState: PolymorphicJsonAdapterFactory<QuestionOperationState> by lazy {
        PolymorphicJsonAdapterFactory.of(QuestionOperationState::class.java, "type")
            .withSubtype(QuestionOperationState.Success::class.java, "success")
            .withSubtype(QuestionOperationState.Error::class.java, "error")
            .withSubtype(QuestionOperationState.AuthenticationError::class.java, "authentication_error")
    }
}