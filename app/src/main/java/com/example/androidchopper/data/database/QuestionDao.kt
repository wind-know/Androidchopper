package com.example.androidchopper.data.database
import androidx.room.*
import kotlinx.coroutines.flow.Flow
/**
 * 数据访问对象 (DAO) 接口，定义对 questions 表的操作
 * 关键特性：
 * 1. 使用 Kotlin Flow 实现响应式查询
 * 2. 使用 suspend 函数支持协程异步操作
 */
@Dao // Room 注解，标记为数据访问对象
interface QuestionDao {

    /**
     * 批量插入问题数据
     * @param questions 要插入的问题实体列表
     *
     * 特性：
     * - suspend 函数：必须在协程或另一个 suspend 函数中调用
     * - 冲突解决策略：如果主键冲突，替换旧数据 (REPLACE)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<QuestionEntity>): List<Long>

    /**
     * 获取所有问题（按 id 排序）
     * @return Flow<List<QuestionEntity>> 持续观察查询结果的 Flow
     *
     * 原理：
     * - 当 questions 表数据变化时，Flow 会自动发射新结果
     * - 无需手动刷新，适合 Jetpack Compose/LiveData 直接使用
     */
    @Query("SELECT * FROM questions ORDER BY id")
    fun getAllQuestions(): Flow<List<QuestionEntity>>

    /**
     * 按章节获取问题
     * @param chapter 章节名称
     * @return Flow<List<QuestionEntity>> 该章节的问题列表流
     *
     * 注意：
     * - 参数通过 :chapter 绑定到 SQL 查询
     * - Room 会自动防止 SQL 注入
     */
    @Query("SELECT * FROM questions WHERE chapter = :chapter ORDER BY id")
    fun getQuestionsByChapter(chapter: String): Flow<List<QuestionEntity>>

    /**
     * 更新问题状态
     * @param questionId 问题ID
     * @param isAnswered 是否已回答
     * @param isCorrect 回答是否正确（可为null）
     *
     * 事务说明：
     * - 此操作会自动在事务中执行
     * - suspend 保证线程安全（不会阻塞主线程）
     */
    @Query("UPDATE questions SET isAnswered = :isAnswered, isCorrect = :isCorrect WHERE id = :questionId")
    suspend fun updateQuestionStatus(questionId: Int, isAnswered: Boolean, isCorrect: Boolean?)
}