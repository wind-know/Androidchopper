package com.example.androidchopper.data.repository

import android.util.Log
import com.example.androidchopper.data.database.AppDatabase
import com.example.androidchopper.data.database.toQuestion
import com.example.androidchopper.data.model.Question
import com.example.androidchopper.data.model.QuestionStatus
import com.example.androidchopper.data.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class QuestionRepository(private val database: AppDatabase) {

    val allQuestions: Flow<List<Question>> =
        database.questionDao().getAllQuestions()
            .map { entities -> entities.map { it.toQuestion() } }

    fun getQuestionsByChapter(chapter: String): Flow<List<Question>> =
        database.questionDao().getQuestionsByChapter(chapter)
            .map { entities ->
                Log.d("Repository", "Got ${entities.size} entities from DB")
                entities.map { it.toQuestion() }
            }

    suspend fun insertQuestions(questions: List<Question>) {
        database.questionDao().insertAll(questions.map { it.toEntity() })
    }

    suspend fun updateQuestionStatus(questionId: Int, isAnswered: Boolean, status: QuestionStatus?) {
        database.questionDao().updateQuestionStatus(questionId, isAnswered, status?.value)
    }
}
