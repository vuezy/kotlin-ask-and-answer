package com.example.askandanswer.interfaces

interface SaveAnswerListener {
    fun onSuccess()
    fun onError(msg: String)
}