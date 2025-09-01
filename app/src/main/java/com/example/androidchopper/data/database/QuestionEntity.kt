package com.example.androidchopper.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.androidchopper.data.model.Question

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: Int,
    val chapter: String,
    val section: String,
    val subTopic: String,
    val content: String,
    val answer: String,
    val isAnswered: Boolean = false,
    val isCorrect: Boolean? = null
)

// 转换函数放在实体类定义之后
fun QuestionEntity.toQuestion() = Question(
    id = id,
    chapter = chapter,
    section = section,
    subTopic = subTopic,
    content = content,
    answer = answer,
    isAnswered = isAnswered,
    isCorrect = isCorrect
)