package com.example.askandanswer.interfaces

interface AnswerItemUtils {
    val userId: Int
    fun startEdit(id: Int)
    fun cancelEdit()
    fun save(id: Int, body: String, questionId: Int, saveAnswerListener: SaveAnswerListener)
    fun delete(id: Int)
    fun vote(id: Int, upvote: Boolean)
}