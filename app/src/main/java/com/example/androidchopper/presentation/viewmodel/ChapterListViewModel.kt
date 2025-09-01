package com.example.androidchopper.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.androidchopper.data.model.Question
import com.example.androidchopper.data.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChapterListViewModel(private val repository: QuestionRepository) : ViewModel() {
    // 所有问题的Flow（用于提取章节）
    private val allQuestionsFlow: Flow<List<Question>> = repository.allQuestions

    // 所有章节（去重）
    val allChapters: LiveData<List<String>> =
        allQuestionsFlow.map { questions -> questions.map { it.chapter }.distinct() }
            .asLiveData() // 转换为LiveData，方便Fragment观察
}

class ChapterListViewModelFactory(private val repository: QuestionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChapterListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChapterListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}