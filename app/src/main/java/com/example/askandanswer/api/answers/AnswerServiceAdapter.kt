package com.example.askandanswer.api.answers

import com.example.askandanswer.models.answers.CreateAnswerState
import com.example.askandanswer.models.answers.DeleteAnswerState
import com.example.askandanswer.models.answers.GetAnswersState
import com.example.askandanswer.models.answers.UpdateAnswerState
import com.example.askandanswer.models.answers.VoteAnswerState
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory

object AnswerServiceAdapter {
    val polymorphicGetAnswersState: PolymorphicJsonAdapterFactory<GetAnswersState> by lazy {
        PolymorphicJsonAdapterFactory.of(GetAnswersState::class.java, "type")
            .withSubtype(GetAnswersState.Success::class.java, "success")
            .withSubtype(GetAnswersState.Error::class.java, "error")
            .withSubtype(GetAnswersState.AuthenticationError::class.java, "authentication_error")
    }

    val polymorphicCreateAnswerState: PolymorphicJsonAdapterFactory<CreateAnswerState> by lazy {
        PolymorphicJsonAdapterFactory.of(CreateAnswerState::class.java, "type")
            .withSubtype(CreateAnswerState.Success::class.java, "success")
            .withSubtype(CreateAnswerState.Error::class.java, "error")
            .withSubtype(CreateAnswerState.AuthenticationError::class.java, "authentication_error")
            .withSubtype(CreateAnswerState.ValidationError::class.java, "validation_error")
    }

    val polymorphicUpdateAnswerState: PolymorphicJsonAdapterFactory<UpdateAnswerState> by lazy {
        PolymorphicJsonAdapterFactory.of(UpdateAnswerState::class.java, "type")
            .withSubtype(UpdateAnswerState.Success::class.java, "success")
            .withSubtype(UpdateAnswerState.Error::class.java, "error")
            .withSubtype(UpdateAnswerState.AuthenticationError::class.java, "authentication_error")
            .withSubtype(UpdateAnswerState.ValidationError::class.java, "validation_error")
    }

    val polymorphicDeleteAnswerState: PolymorphicJsonAdapterFactory<DeleteAnswerState> by lazy {
        PolymorphicJsonAdapterFactory.of(DeleteAnswerState::class.java, "type")
            .withSubtype(DeleteAnswerState.Success::class.java, "success")
            .withSubtype(DeleteAnswerState.Error::class.java, "error")
            .withSubtype(DeleteAnswerState.AuthenticationError::class.java, "authentication_error")
    }

    val polymorphicVoteAnswerState: PolymorphicJsonAdapterFactory<VoteAnswerState> by lazy {
        PolymorphicJsonAdapterFactory.of(VoteAnswerState::class.java, "type")
            .withSubtype(VoteAnswerState.Success::class.java, "success")
            .withSubtype(VoteAnswerState.Error::class.java, "error")
            .withSubtype(VoteAnswerState.AuthenticationError::class.java, "authentication_error")
    }
}