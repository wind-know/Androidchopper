package com.example.androidchopper.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.androidchopper.R
import com.example.androidchopper.data.database.AppDatabase
import com.example.androidchopper.data.model.Question
import com.example.androidchopper.data.repository.QuestionRepository
import com.example.androidchopper.databinding.FragmentQuestionBinding
import com.example.androidchopper.presentation.viewmodel.QuestionViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.Retrofit

class QuestionFragment : Fragment() {
    private var _binding: FragmentQuestionBinding? = null
    private val binding get() = _binding!!
    private var currentChapter: String = ""
    // 手动创建 Repository 和 ViewModel
    private val repository by lazy {
        QuestionRepository(
            database = AppDatabase.getInstance(requireContext())
        )
    }
    private val viewModel: QuestionViewModel by viewModels {
        QuestionViewModel.provideFactory(repository)
    }

    companion object {
        private const val ARG_CHAPTER = "chapter"  // 用于Bundle传参的key
        fun newInstance(chapter: String): QuestionFragment {
            // 1. 创建Fragment新实例
            return QuestionFragment().apply {
                // 2. 设置Fragment的初始参数
                arguments = Bundle().apply {
                    putString(ARG_CHAPTER, chapter)
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentChapter = it.getString(ARG_CHAPTER, "")
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 初始化章节
        viewModel.initChapter(currentChapter)
        // 观察数据流
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.questions.collect { questions ->
                        if (questions.isEmpty()) {
                            binding.emptyView.visibility = View.VISIBLE
                        } else {
                            binding.emptyView.visibility = View.GONE
                        }
                    }
                }
                launch {
                    viewModel.currentQuestion.collect { question ->
                        question?.let { updateQuestionUI(it) }
                    }
                }
                launch {
                    viewModel.isAnswerShown.collect { isShown ->
                        binding.answerContainer.visibility =
                            if (isShown) View.VISIBLE else View.INVISIBLE
                        binding.answerText.visibility = if (isShown) View.VISIBLE else View.INVISIBLE
                        binding.showAnswerText.text = if (isShown) "隐藏答案" else "显示答案"
                        binding.answerIcon.setImageResource(if (isShown) R.drawable.ic_answer else R.drawable.ic_noanswer)

                    }
                }
            }
        }
        // 按钮事件绑定
        binding.nextButton.setOnClickListener { viewModel.nextQuestion() }
        binding.prevButton.setOnClickListener { viewModel.prevQuestion() }
        binding.showAnswerButton.setOnClickListener {
            viewModel.toggleAnswerVisibility()
        }
//        binding.correctButton.setOnClickListener { viewModel.submitAnswer(true) }
//        binding.incorrectButton.setOnClickListener { viewModel.submitAnswer(false) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuestionBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun updateQuestionUI(question: Question) {
        binding.questionText.text = question.content
        binding.answerText.text = question.answer
        binding.answerText.visibility =
            if (viewModel.isAnswerShown.value) View.VISIBLE else View.GONE

        binding.pageIndicator.text =
            "${viewModel.currentQuestionIndex.value + 1}/${viewModel.questions.value.size}"
    }
}