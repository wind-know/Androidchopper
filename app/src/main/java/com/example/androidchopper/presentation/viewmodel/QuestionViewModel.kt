package com.example.androidchopper.presentation.viewmodel
import android.util.Log
import androidx.lifecycle.*
import com.example.androidchopper.data.model.Question
import com.example.androidchopper.data.repository.QuestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import retrofit2.Retrofit

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class QuestionViewModel(private val repository: QuestionRepository) : ViewModel() {
    companion object {
        fun provideFactory(
            repository: QuestionRepository
        ): ViewModelProvider.Factory
        = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return QuestionViewModel(repository) as T
            }
        }
    }
    //========== 状态容器 ==========//
    private val _selectedChapter: MutableStateFlow<String> = MutableStateFlow("")
    // 对外暴露只读的当前章节名称（StateFlow）
    val selectedChapter: StateFlow<String> get()= _selectedChapter.asStateFlow()

    // 私有可变状态：当前问题索引（初始值为0）
    private val _currentQuestionIndex = MutableStateFlow(0)
    // 对外暴露只读的当前索引（StateFlow）
    val currentQuestionIndex: StateFlow<Int> get() = _currentQuestionIndex.asStateFlow()

    private val _isAnswerShown = MutableStateFlow(false)

    //========== 公开数据流 ==========//
    // 当前章节对应的问题列表（StateFlow）
    val questions: StateFlow<List<Question>> = selectedChapter
        // 当章节变化时，立即取消旧请求，切换为新章节的数据流
        .flatMapLatest { chapter ->
            // 从仓库获取问题列表，出错时发射空列表避免崩溃
            repository.getQuestionsByChapter(chapter).catch { emit(emptyList()) }
        }
        // 转换为热流（StateFlow），绑定ViewModel生命周期
        .stateIn(
            viewModelScope,          // 协程作用域
            SharingStarted.Lazily,   // 延迟启动（有订阅者时才收集）
            emptyList()              // 初始空列表
        )

    // 当前显示的问题（StateFlow，可能为null）
    val currentQuestion: StateFlow<Question?> = combine(
        questions,                  // 问题列表流
        _currentQuestionIndex       // 当前索引流
    ) { questions, index ->
        // 安全获取当前索引的问题（越界返回null）
        questions.getOrNull(index)
    }
        // 转换为热流（StateFlow）
        .stateIn(
            viewModelScope,             // 协程作用域
            SharingStarted.Lazily,      // 延迟启动
            null                        // 初始null（表示未加载）
        )

    val isAnswerShown: StateFlow<Boolean> = _isAnswerShown.asStateFlow()

    // 添加这个方法
    fun resetAnswerVisibility() {
        _isAnswerShown.value = false
    }
    //========== 操作方法 ==========//
    fun initChapter(chapter: String) {
        _selectedChapter.value = chapter
    }
// 处理"下一题"操作的函数
    fun nextQuestion() {
        // 计算新索引：当前索引+1，并确保不超过问题列表的最大索引
        val newIndex = (_currentQuestionIndex.value + 1)  // 获取当前索引并+1
            .coerceAtMost(questions.value.lastIndex)     // 限制最大值不超过列表最后索引
        // 打印调试日志：显示索引变化（当前值 -> 新值）
        Log.d("ViewModel", "Next: ${_currentQuestionIndex.value} -> $newIndex")
        // 更新当前问题索引状态
        _currentQuestionIndex.value = newIndex  // 通过MutableStateFlow的value属性更新状态
    }

    fun prevQuestion() {
        val newIndex = (_currentQuestionIndex.value - 1).coerceAtLeast(0)
        Log.d("ViewModel", "Prev: ${_currentQuestionIndex.value} -> $newIndex")
        _currentQuestionIndex.value = newIndex
    }
    fun toggleAnswerVisibility() {
        _isAnswerShown.update { !it }
    }
    // 提交答案方法
    fun submitAnswer(isCorrect: Boolean) {
        viewModelScope.launch {

            currentQuestion.value?.let { question ->
                repository.updateQuestionStatus(
                    questionId = question.id,
                    isAnswered = true,
                    isCorrect = isCorrect
                )
            }
        }
    }
}