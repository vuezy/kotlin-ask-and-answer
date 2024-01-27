package com.example.askandanswer.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.askandanswer.MyApplication
import com.example.askandanswer.models.users.AuthState
import com.example.askandanswer.models.users.GetPointsAndCreditsState
import com.example.askandanswer.models.users.GetUserDataState
import com.example.askandanswer.models.InvalidTokenException
import com.example.askandanswer.models.users.IsLoggedInState
import com.example.askandanswer.models.users.LoginRequestBody
import com.example.askandanswer.models.users.LogoutState
import com.example.askandanswer.models.users.RegisterRequestBody
import com.example.askandanswer.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> get() = _authState

    private val _getUserDataState = MutableLiveData<GetUserDataState>()
    val getUserDataState: LiveData<GetUserDataState> get() = _getUserDataState

    private val _getPointsAndCreditsState = MutableLiveData<GetPointsAndCreditsState>()
    val getPointsAndCreditsState: LiveData<GetPointsAndCreditsState> get() = _getPointsAndCreditsState

    private val _isLoggedInState = MutableLiveData<IsLoggedInState>()
    val isLoggedInState: LiveData<IsLoggedInState> get() = _isLoggedInState

    private val _logoutState = MutableLiveData<LogoutState>()
    val logoutState: LiveData<LogoutState> get() = _logoutState

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading
        val body = LoginRequestBody(email, password)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _authState.postValue(UserRepository.login(body))
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error from login method.", e)
                _authState.postValue(AuthState.Error(MyApplication.getInstance().errMsg))
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        _authState.value = AuthState.Loading
        val body = RegisterRequestBody(name, email, password)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _authState.postValue(UserRepository.register(body))
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error from register method.", e)
                _authState.postValue(AuthState.Error(MyApplication.getInstance().errMsg))
            }
        }
    }

    fun getUserData() {
        _getUserDataState.value = GetUserDataState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _getUserDataState.postValue(UserRepository.getUserData())
            } catch (e: InvalidTokenException) {
                _getUserDataState.postValue(
                    GetUserDataState.AuthenticationError(MyApplication.getInstance().invalidTokenMsg)
                )
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error from getUserData method.", e)
                _getUserDataState.postValue(GetUserDataState.Error(MyApplication.getInstance().errMsg))
            }
        }
    }

    fun getPointsAndCredits() {
        _getPointsAndCreditsState.value = GetPointsAndCreditsState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _getPointsAndCreditsState.postValue(UserRepository.getPointsAndCredits())
            } catch (e: InvalidTokenException) {
                _getPointsAndCreditsState.postValue(
                    GetPointsAndCreditsState.AuthenticationError(MyApplication.getInstance().invalidTokenMsg)
                )
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error from getPointsAndCredits method.", e)
                _getPointsAndCreditsState.postValue(GetPointsAndCreditsState.Error(MyApplication.getInstance().errMsg))
            }
        }
    }

    fun isLoggedIn() {
        _isLoggedInState.value = IsLoggedInState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoggedInState.postValue(UserRepository.isLoggedIn())
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error from isLoggedIn method.", e)
                _isLoggedInState.postValue(IsLoggedInState.Error)
            }
        }
    }

    fun logout() {
        _logoutState.value = LogoutState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                UserRepository.logout()
                _logoutState.postValue(LogoutState.Success)
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error from logout method.", e)
                _logoutState.postValue(LogoutState.Error(MyApplication.getInstance().errMsg))
            }
        }
    }
}