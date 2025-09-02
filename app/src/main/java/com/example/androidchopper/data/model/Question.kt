package com.example.androidchopper.data.model
import com.example.androidchopper.data.database.QuestionEntity
enum class QuestionStatus(val value: Int) {
    FORGOT(0),      // 忘记了
    VAGUE(1),       // 模糊
    KNOWN(2),       // 认识
    MASTERED(3);    // 完全掌握

    companion object {
        fun fromValue(value: Int?): QuestionStatus? {
            return values().find { it.value == value }
        }
    }
}


data class Question(
    val id: Int,
    val chapter: String,
    val section: String,
    val subTopic: String,
    val content: String,
    val answer: String,
    var isAnswered: Boolean = false,
    val status: QuestionStatus = QuestionStatus.FORGOT  // 默认忘记
)

// 转换函数
fun Question.toEntity() = QuestionEntity(
    id = id,
    chapter = chapter,
    section = section,
    subTopic = subTopic,
    content = content,
    answer = answer,
    isAnswered = isAnswered,
    status = status?.value
)