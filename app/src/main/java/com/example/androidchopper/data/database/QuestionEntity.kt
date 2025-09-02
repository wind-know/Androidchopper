package com.example.androidchopper.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.androidchopper.data.model.Question
import com.example.androidchopper.data.model.QuestionStatus

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: Int,
    val chapter: String,
    val section: String,
    val subTopic: String,
    val content: String,
    val answer: String,
    val isAnswered: Boolean = false,
    val status: Int? = null   // 用 Int 存储状态
)

// 转换函数
fun QuestionEntity.toQuestion() = Question(
    id = id,
    chapter = chapter,
    section = section,
    subTopic = subTopic,
    content = content,
    answer = answer,
    isAnswered = isAnswered,
    status = QuestionStatus.fromValue(status) ?: QuestionStatus.FORGOT
)
