package com.example.androidchopper.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<QuestionEntity>): List<Long>

    @Query("SELECT * FROM questions ORDER BY id")
    fun getAllQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE chapter = :chapter ORDER BY id")
    fun getQuestionsByChapter(chapter: String): Flow<List<QuestionEntity>>

    @Query("UPDATE questions SET isAnswered = :isAnswered, status = :status WHERE id = :questionId")
    suspend fun updateQuestionStatus(questionId: Int, isAnswered: Boolean, status: Int?)
}
