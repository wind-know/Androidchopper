package com.example.androidchopper.data.model
import com.example.androidchopper.data.database.QuestionEntity

data class Question(
    val id: Int,
    val chapter: String,       // 大章节（如"Android基础"）
    val section: String,      // 小节（如"四大组件"）
    val subTopic: String,     // 子主题（如"Activity"）
    val content: String,      // 题干（如"进程模式"）
    val answer: String,       // 答案详情
    var isAnswered: Boolean = false,
    var isCorrect: Boolean? = null
)

// 转换函数必须在类外部（或作为扩展函数）
fun Question.toEntity() = QuestionEntity(
    id = id,
    chapter = chapter,
    section = section,
    subTopic = subTopic,
    content = content,
    answer = answer,
    isAnswered = isAnswered,
    isCorrect = isCorrect
)