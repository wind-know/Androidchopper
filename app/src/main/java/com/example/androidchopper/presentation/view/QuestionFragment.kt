// QuestionFragment.kt
package com.example.androidchopper.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
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

class QuestionFragment : Fragment() {
    private var _binding: FragmentQuestionBinding? = null
    private val binding get() = _binding!!
    private var currentChapter: String = ""

    private val repository by lazy {
        QuestionRepository(
            database = AppDatabase.getInstance(requireContext())
        )
    }

    private val viewModel: QuestionViewModel by viewModels {
        QuestionViewModel.provideFactory(repository)
    }

    companion object {
        private const val ARG_CHAPTER = "chapter"

        fun newInstance(chapter: String): QuestionFragment {
            return QuestionFragment().apply {
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuestionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUIState()
        viewModel.initChapter(currentChapter)
        setupButtonListeners()
        observeViewModel()
    }

    private fun initUIState() {
        resetUIForNewQuestion()
    }

    private fun setupButtonListeners() {
        binding.nextButton.setOnClickListener {
            viewModel.nextQuestion()
            resetUIForNewQuestion()
            viewModel.resetAnswerVisibility()
        }

        binding.prevButton.setOnClickListener {
            viewModel.prevQuestion()
            resetUIForNewQuestion()
            viewModel.resetAnswerVisibility()
        }

        binding.showAnswerButton.setOnClickListener {
            viewModel.toggleAnswerVisibility()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.questions.collect { questions ->
                        updateQuestionsUI(questions)
                    }
                }

                launch {
                    viewModel.currentQuestion.collect { question ->
                        question?.let { updateQuestionUI(it) }
                    }
                }

                launch {
                    viewModel.isAnswerShown.collect { isShown ->
                        updateAnswerVisibilityUI(isShown)
                    }
                }

                launch {
                    viewModel.currentQuestionIndex.collect { index ->
                        updatePageIndicatorUI(index)
                    }
                }
            }
        }
    }

    private fun resetUIForNewQuestion() {
        binding.nextButton.visibility = View.GONE
        binding.prevButton.visibility = View.GONE
        binding.answerContainer.visibility = View.INVISIBLE
        binding.answerText.visibility = View.INVISIBLE
        binding.showAnswerText.text = "显示答案"
        binding.answerIcon.setImageResource(R.drawable.ic_noanswer)
    }

    private fun updateQuestionsUI(questions: List<Question>) {
        binding.emptyView.visibility = if (questions.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun updateQuestionUI(question: Question) {
        binding.questionText.text = question.content
        binding.answerText.text = question.answer
    }

    private fun updateAnswerVisibilityUI(isShown: Boolean) {
        binding.answerContainer.visibility = if (isShown) View.VISIBLE else View.INVISIBLE
        binding.answerText.visibility = if (isShown) View.VISIBLE else View.INVISIBLE
        binding.showAnswerText.text = if (isShown) "隐藏答案" else "显示答案"
        binding.answerIcon.setImageResource(
            if (isShown) R.drawable.ic_answer else R.drawable.ic_noanswer
        )
        binding.nextButton.visibility = if (isShown) View.VISIBLE else View.GONE
        binding.prevButton.visibility = if (isShown) View.VISIBLE else View.GONE
    }

    private fun updatePageIndicatorUI(currentIndex: Int) {
        val questions = viewModel.questions.value
        if (questions.isNotEmpty()) {
            binding.pageIndicator.text = "${currentIndex + 1}/${questions.size}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}