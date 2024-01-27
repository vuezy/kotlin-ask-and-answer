package com.example.askandanswer.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.askandanswer.MyApplication
import com.example.askandanswer.models.InvalidTokenException
import com.example.askandanswer.models.answers.AnswerRequestBody
import com.example.askandanswer.models.answers.CreateAnswerState
import com.example.askandanswer.models.answers.DeleteAnswerState
import com.example.askandanswer.models.answers.GetAnswersState
import com.example.askandanswer.models.answers.UpdateAnswerState
import com.example.askandanswer.models.answers.VoteAnswerState
import com.example.askandanswer.repositories.AnswerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AnswerViewModel : ViewModel() {
    private val _getAnswersState = MutableLiveData<GetAnswersState>()
    val getAnswersState: LiveData<GetAnswersState> get() = _getAnswersState

    private val _createAnswerState = MutableLiveData<CreateAnswerState>()
    val createAnswerState: LiveData<CreateAnswerState> get() = _createAnswerState

    private val _updateAnswerState = MutableLiveData<UpdateAnswerState>()
    val updateAnswerState: LiveData<UpdateAnswerState> get() = _updateAnswerState

    private val _deleteAnswerState = MutableLiveData<DeleteAnswerState>()
    val deleteAnswerState: LiveData<DeleteAnswerState> get() = _deleteAnswerState

    private val _voteAnswerState = MutableLiveData<VoteAnswerState>()
    val voteAnswerState: LiveData<VoteAnswerState> get() = _voteAnswerState

    fun getAnswersByQuestionId(questionId: Int) {
        _getAnswersState.value = GetAnswersState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _getAnswersState.postValue(AnswerRepository.getAnswersByQuestionId(questionId))
            } catch (e: InvalidTokenException) {
                _getAnswersState.postValue(
                    GetAnswersState.AuthenticationError(MyApplication.getInstance().invalidTokenMsg)
                )
            } catch (e: Exception) {
                Log.e("AnswerViewModel", "Error from getAnswersByQuestionId method.", e)
                _getAnswersState.postValue(GetAnswersState.Error(MyApplication.getInstance().errMsg))
            }
        }
    }

    fun getAnswersByUserId() {
        _getAnswersState.value = GetAnswersState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _getAnswersState.postValue(AnswerRepository.getAnswersByUserId())
            } catch (e: InvalidTokenException) {
                _getAnswersState.postValue(
                    GetAnswersState.AuthenticationError(MyApplication.getInstance().invalidTokenMsg)
                )
            } catch (e: Exception) {
                Log.e("AnswerViewModel", "Error from getAnswersByUserId method.", e)
                _getAnswersState.postValue(GetAnswersState.Error(MyApplication.getInstance().errMsg))
            }
        }
    }

    fun createAnswer(body: String, questionId: Int) {
        _createAnswerState.value = CreateAnswerState.Loading
        val requestBody = AnswerRequestBody(body, questionId)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _createAnswerState.postValue(AnswerRepository.createAnswer(requestBody))
            } catch (e: InvalidTokenException) {
                _createAnswerState.postValue(
                    CreateAnswerState.AuthenticationError(MyApplication.getInstance().invalidTokenMsg)
                )
            } catch (e: Exception) {
                Log.e("AnswerViewModel", "Error from createAnswer method.", e)
                _createAnswerState.postValue(CreateAnswerState.Error(MyApplication.getInstance().errMsg))
            }
        }
    }

    fun updateAnswer(id: Int, body: String, questionId: Int) {
        _updateAnswerState.value = UpdateAnswerState.Loading
        val requestBody = AnswerRequestBody(body, questionId)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _updateAnswerState.postValue(AnswerRepository.updateAnswer(id, requestBody))
            } catch (e: InvalidTokenException) {
                _updateAnswerState.postValue(
                    UpdateAnswerState.AuthenticationError(MyApplication.getInstance().invalidTokenMsg)
                )
            } catch (e: Exception) {
                Log.e("AnswerViewModel", "Error from updateAnswer method.", e)
                _updateAnswerState.postValue(UpdateAnswerState.Error(MyApplication.getInstance().errMsg))
            }
        }
    }

    fun deleteAnswer(id: Int) {
        _deleteAnswerState.value = DeleteAnswerState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _deleteAnswerState.postValue(AnswerRepository.deleteAnswer(id))
            } catch (e: InvalidTokenException) {
                _deleteAnswerState.postValue(
                    DeleteAnswerState.AuthenticationError(MyApplication.getInstance().invalidTokenMsg)
                )
            } catch (e: Exception) {
                Log.e("AnswerViewModel", "Error from deleteAnswer method.", e)
                _deleteAnswerState.postValue(DeleteAnswerState.Error(MyApplication.getInstance().errMsg))
            }
        }
    }

    fun voteAnswer(id: Int, upvote: Boolean) {
        _voteAnswerState.value = VoteAnswerState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _voteAnswerState.postValue(AnswerRepository.voteAnswer(id, upvote))
            } catch (e: InvalidTokenException) {
                _voteAnswerState.postValue(
                    VoteAnswerState.AuthenticationError(MyApplication.getInstance().invalidTokenMsg)
                )
            } catch (e: Exception) {
                Log.e("AnswerViewModel", "Error from voteAnswer method.", e)
                _voteAnswerState.postValue(VoteAnswerState.Error(MyApplication.getInstance().errMsg))
            }
        }
    }
}