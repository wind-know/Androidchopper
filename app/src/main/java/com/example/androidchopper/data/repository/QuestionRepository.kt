package com.example.androidchopper.data.repository
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.androidchopper.data.database.AppDatabase
import com.example.androidchopper.data.database.toQuestion
import com.example.androidchopper.data.model.Question
import com.example.androidchopper.data.model.toEntity

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class QuestionRepository(private val database: AppDatabase) {
    // 获取所有问题的Flow
    val allQuestions: Flow<List<Question>> =
        database.questionDao().getAllQuestions()
            .map { entities -> entities.map { it.toQuestion() } }
    fun getQuestionsByChapter(chapter: String): Flow<List<Question>> =
        // 1. 调用DAO层获取原始数据流
        database.questionDao().getQuestionsByChapter(chapter)
            // 2. 映射转换数据
            .map { entities ->
                // 3. 将每个QuestionEntity转换为Question领域模型
                Log.d("Repository", "Got ${entities.size} entities from DB")
                entities.map { it.toQuestion() }
            }
    // 插入问题
    suspend fun insertQuestions(questions: List<Question>) {
        database.questionDao().insertAll(questions.map { it.toEntity() })

    }

    // 更新问题状态
    suspend fun updateQuestionStatus(questionId: Int, isAnswered: Boolean, isCorrect: Boolean?) {
        database.questionDao().updateQuestionStatus(questionId, isAnswered, isCorrect)
    }
}

