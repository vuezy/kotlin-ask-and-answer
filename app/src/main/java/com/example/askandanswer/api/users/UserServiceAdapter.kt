package com.example.askandanswer.api.users

import com.example.askandanswer.models.users.AuthState
import com.example.askandanswer.models.users.GetPointsAndCreditsState
import com.example.askandanswer.models.users.RefreshTokensState
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory

object UserServiceAdapter {
    val polymorphicAuthState: PolymorphicJsonAdapterFactory<AuthState> by lazy {
        PolymorphicJsonAdapterFactory.of(AuthState::class.java, "type")
            .withSubtype(AuthState.Success::class.java, "success")
            .withSubtype(AuthState.Error::class.java, "error")
            .withSubtype(AuthState.ValidationError::class.java, "validation_error")
    }

    val polymorphicRefreshTokensState: PolymorphicJsonAdapterFactory<RefreshTokensState> by lazy {
        PolymorphicJsonAdapterFactory.of(RefreshTokensState::class.java, "type")
            .withSubtype(RefreshTokensState.Success::class.java, "success")
            .withSubtype(RefreshTokensState.Error::class.java, "error")
            .withSubtype(RefreshTokensState.Error::class.java, "authentication_error")
    }

    val polymorphicGetPointsAndCreditsState: PolymorphicJsonAdapterFactory<GetPointsAndCreditsState> by lazy {
        PolymorphicJsonAdapterFactory.of(GetPointsAndCreditsState::class.java, "type")
            .withSubtype(GetPointsAndCreditsState.Success::class.java, "success")
            .withSubtype(GetPointsAndCreditsState.Error::class.java, "error")
            .withSubtype(GetPointsAndCreditsState.AuthenticationError::class.java, "authentication_error")
    }
}