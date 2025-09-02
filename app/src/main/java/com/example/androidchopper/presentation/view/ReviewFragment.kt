package com.example.androidchopper.presentation.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.androidchopper.R
import com.example.androidchopper.data.database.AppDatabase
import com.example.androidchopper.data.model.Question
import com.example.androidchopper.data.model.QuestionStatus
import com.example.androidchopper.data.repository.QuestionRepository
import com.example.androidchopper.databinding.FragmentReviewBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ReviewFragment : Fragment() {

    private var _binding: FragmentReviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: QuestionRepository
    private var questions: List<Question> = emptyList()
    private var currentIndex = 0
    private var correctCount = 0
    private var currentAnswered = false

    companion object {
        private const val TAG = "ReviewFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewBinding.inflate(inflater, container, false)
        repository = QuestionRepository(AppDatabase.getInstance(requireContext()))

        setupListeners()
        loadQuestionsOnce()  // 改为只初始化时收集题目

        return binding.root
    }

    /** 只在 Fragment 初始化时收集题目 */
    private fun loadQuestionsOnce() {
        lifecycleScope.launch {
            val list = repository.allQuestions.first() // 只收集一次，不订阅实时更新
            Log.d(TAG, "Loaded ${list.size} questions from database")

            val filtered = list.filter { it.status != QuestionStatus.MASTERED }
            questions = if (filtered.isNotEmpty()) filtered else list

            Log.d(TAG, "Filtered questions count: ${questions.size}")
            if (questions.isNotEmpty()) {
                showQuestion(0)
            } else {
                Toast.makeText(requireContext(), "没有题目可复习", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        binding.tvForgot.setOnClickListener { selectStatus(QuestionStatus.FORGOT) }
        binding.tvVague.setOnClickListener { selectStatus(QuestionStatus.VAGUE) }
        binding.tvKnown.setOnClickListener { selectStatus(QuestionStatus.KNOWN) }
        binding.tvMastered.setOnClickListener { selectStatus(QuestionStatus.MASTERED) }

        binding.tvPrev.setOnClickListener { showPrevious() }
        binding.tvNext.setOnClickListener { showNext() }
        binding.tvMarkForgot.setOnClickListener { markForgot() }
    }

    private fun selectStatus(status: QuestionStatus) {
        val question = questions.getOrNull(currentIndex) ?: return
        currentAnswered = true
        Log.d(TAG, "Selecting status $status for question ${question.id} at index $currentIndex")

        lifecycleScope.launch {
            repository.updateQuestionStatus(question.id, true, status)
        }

        // 显示答案和状态
        binding.tvAnswer.apply {
            visibility = View.VISIBLE
            text = question.answer
        }
        showStatus(status)

        // 隐藏状态按钮，显示导航
        binding.layoutState.visibility = View.GONE
        binding.layoutNavigation.visibility = View.VISIBLE

        // 统计答对
        if (status == QuestionStatus.KNOWN || status == QuestionStatus.MASTERED) {
            correctCount++
            Log.d(TAG, "Correct count updated: $correctCount")
            if (correctCount >= 20) {
                Toast.makeText(requireContext(), "复习完了！", Toast.LENGTH_LONG).show()
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun showQuestion(index: Int) {
        currentIndex = index
        currentAnswered = false
        val question = questions[index]

        Log.d(TAG, "Showing question ${question.id} at index $currentIndex")

        binding.tvQuestionContent.text = question.content
        binding.tvAnswer.visibility = View.GONE
        binding.tvAnswer.text = ""

        binding.layoutState.visibility = View.VISIBLE
        binding.layoutNavigation.visibility = View.GONE

        showStatus(question.status)
        updateNavButtons()
    }

    private fun showStatus(status: QuestionStatus?) {
        when (status) {
            QuestionStatus.FORGOT -> {
                binding.ivStatusIcon.setImageResource(R.drawable.ic_forgot)
                binding.tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                binding.tvStatus.text = "忘记"
            }
            QuestionStatus.VAGUE -> {
                binding.ivStatusIcon.setImageResource(R.drawable.ic_vague)
                binding.tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
                binding.tvStatus.text = "模糊"
            }
            QuestionStatus.KNOWN -> {
                binding.ivStatusIcon.setImageResource(R.drawable.ic_known)
                binding.tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
                binding.tvStatus.text = "认识"
            }
            QuestionStatus.MASTERED -> {
                binding.ivStatusIcon.setImageResource(R.drawable.ic_mastered)
                binding.tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                binding.tvStatus.text = "完全掌握"
            }
            else -> {
                binding.ivStatusIcon.setImageResource(0)
                binding.tvStatus.text = ""
            }
        }
    }

    private fun showPrevious() {
        if (currentIndex > 0) {
            Log.d(TAG, "Navigating to previous question at index ${currentIndex - 1}")
            showQuestion(currentIndex - 1)
        } else {
            Toast.makeText(requireContext(), "已经是第一题", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showNext() {
        if (currentIndex < questions.lastIndex) {
            Log.d(TAG, "Navigating to next question at index ${currentIndex + 1}")
            showQuestion(currentIndex + 1)
        } else {
            Toast.makeText(requireContext(), "已经是最后一题", Toast.LENGTH_SHORT).show()
        }
    }


    private fun markForgot() {
        if (!currentAnswered) return
        val question = questions.getOrNull(currentIndex) ?: return
        Log.d(TAG, "Marking question ${question.id} as forgotten")

        lifecycleScope.launch {
            repository.updateQuestionStatus(question.id, true, QuestionStatus.FORGOT)
        }
        Toast.makeText(requireContext(), "已标记为忘记", Toast.LENGTH_SHORT).show()

        binding.tvAnswer.apply {
            visibility = View.VISIBLE
            text = question.answer
        }
        showStatus(QuestionStatus.FORGOT)
    }

    private fun updateNavButtons() {
        val enabledColor = ContextCompat.getColor(requireContext(), R.color.black)
        val disabledColor = ContextCompat.getColor(requireContext(), R.color.home_gray)

        binding.tvPrev.isEnabled = currentIndex != 0
        binding.tvPrev.setTextColor(if (binding.tvPrev.isEnabled) enabledColor else disabledColor)

        binding.tvNext.isEnabled = currentIndex != questions.lastIndex
        binding.tvNext.setTextColor(if (binding.tvNext.isEnabled) enabledColor else disabledColor)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
