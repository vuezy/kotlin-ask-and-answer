package com.example.askandanswer.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.askandanswer.MyApplication
import com.example.askandanswer.models.InvalidTokenException
import com.example.askandanswer.models.questions.GetQuestionByIdState
import com.example.askandanswer.models.questions.GetQuestionsState
import com.example.askandanswer.models.questions.QuestionOperationState
import com.example.askandanswer.models.questions.QuestionRequestBody
import com.example.askandanswer.models.questions.SaveQuestionState
import com.example.askandanswer.repositories.QuestionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuestionViewModel : ViewModel() {
    private val _getQuestionsState = MutableLiveData<GetQuestionsState>()
    val getQuestionsState: LiveData<GetQuestionsState> get() = _getQuestionsState

    private val _getQuestionByIdState = MutableLiveData<GetQuestionByIdState>()
    val getQuestionByIdState: LiveData<GetQuestionByIdState> get() = _getQuestionByIdState

    private val _saveQuestionState = MutableLiveData<SaveQuestionState>()
    val saveQuestionState: LiveData<SaveQuestionState> get() = _saveQuestionState

    private val _questionOperationState = MutableLiveData<QuestionOperationState>()
    val questionOperationState: LiveData<QuestionOperationState> get() = _questionOperationState

    fun getQuestions(title: String? = null) {
        _getQuestionsState.value = GetQuestionsState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _getQuestionsState.postValue(QuestionRepository.getQuestions(title))
            } catch (e: InvalidTokenException) {
                _getQuestionsState.postValue(
                    GetQuestionsState.AuthenticationError(MyApplication.getInstance().invalidTokenMsg)
                )
            } catch (e: Exception) {
                Log.e("QuestionViewModel", "Error from getQuestions method.", e)
                _getQuestionsState.postValue(GetQuestionsState.Error(MyApplication.getInstance().errMsg))
            }
        }
    }

    fun getQuestionsByUserId() {
        _getQuestionsState.value = GetQuestionsState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _getQuestionsState.postValue(QuestionRepository.getQuestionsByUserId())
            } catch (e: InvalidTokenException) {
                _getQuestionsState.postValue(
                    GetQuestionsState.AuthenticationError(MyApplication.getInstance().invalidTokenMsg)
                )
            } catch (e: Exception) {
                Log.e("QuestionViewModel", "Error from getQuestionsByUserId method.", e)
                _getQuestionsState.postValue(GetQuestionsState.Error(MyApplication.getInstance().errMsg))
            }
        }
    }

    fun getQuestionById(id: Int) {
        _getQuestionByIdState.value = GetQuestionByIdState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _getQuestionByIdState.postValue(QuestionRepository.getQuestionById(id))
            } catch (e: InvalidTokenException) {
                _getQuestionByIdState.postValue(
                    GetQuestionByIdState.AuthenticationError(MyApplication.getInstance().invalidTokenMsg)
                )
            } catch (e: Exception) {
                Log.e("QuestionViewModel", "Error from getQuestionById method.", e)
                _getQuestionByIdState.postValue(GetQuestionByIdState.Error(MyApplication.getInstance().errMsg))
            }
        }
    }

    fun createQuestion(title: String, body: String, priorityLevel: Int) {
        _saveQuestionState.value = SaveQuestionState.Loading
        val requestBody = QuestionRequestBody(title, body, priorityLevel)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _saveQuestionState.postValue(QuestionRepository.createQuestion(requestBody))
            } catch (e: InvalidTokenException) {
                _saveQuestionState.postValue(
                    SaveQuestionState.AuthenticationError(MyApplication.getInstance().invalidTokenMsg)
                )
            } catch (e: Exception) {
                Log.e("QuestionViewModel", "Error from createQuestion method.", e)
                _saveQuestionState.postValue(SaveQuestionState.Error(MyApplication.getInstance().errMsg))
            }
        }
    }

    fun updateQuestion(id: Int, title: String, body: String, priorityLevel: Int) {
        _saveQuestionState.value = SaveQuestionState.Loading
        val requestBody = QuestionRequestBody(title, body, priorityLevel)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _saveQuestionState.postValue(QuestionRepository.updateQuestion(id, requestBody))
            } catch (e: InvalidTokenException) {
                _saveQuestionState.postValue(
                    SaveQuestionState.AuthenticationError(MyApplication.getInstance().invalidTokenMsg)
                )
            } catch (e: Exception) {
                Log.e("QuestionViewModel", "Error from updateQuestion method.", e)
                _saveQuestionState.postValue(SaveQuestionState.Error(MyApplication.getInstance().errMsg))
            }
        }
    }

    fun deleteQuestion(id: Int) {
        _questionOperationState.value = QuestionOperationState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _questionOperationState.postValue(QuestionRepository.deleteQuestion(id))
            } catch (e: InvalidTokenException) {
                _questionOperationState.postValue(
                    QuestionOperationState.AuthenticationError(MyApplication.getInstance().invalidTokenMsg)
                )
            } catch (e: Exception) {
                Log.e("QuestionViewModel", "Error from deleteQuestion method.", e)
                _questionOperationState.postValue(QuestionOperationState.Error(MyApplication.getInstance().errMsg))
            }
        }
    }

    fun closeQuestion(id: Int) {
        _questionOperationState.value = QuestionOperationState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _questionOperationState.postValue(QuestionRepository.closeQuestion(id))
            } catch (e: InvalidTokenException) {
                _questionOperationState.postValue(
                    QuestionOperationState.AuthenticationError(MyApplication.getInstance().invalidTokenMsg)
                )
            } catch (e: Exception) {
                Log.e("QuestionViewModel", "Error from closeQuestion method.", e)
                _questionOperationState.postValue(QuestionOperationState.Error(MyApplication.getInstance().errMsg))
            }
        }
    }
}