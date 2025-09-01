package com.example.androidchopper.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidchopper.data.database.AppDatabase
import com.example.androidchopper.data.repository.QuestionRepository
import com.example.androidchopper.databinding.FragmentChapterListBinding
import com.example.androidchopper.presentation.adapter.ChapterAdapter
import com.example.androidchopper.presentation.viewmodel.ChapterListViewModel
import com.example.androidchopper.presentation.viewmodel.ChapterListViewModelFactory

class ChapterListFragment : Fragment() {
    private var _binding: FragmentChapterListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ChapterListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChapterListBinding.inflate(inflater, container, false)
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 初始化ViewModel（使用 ChapterListViewModel）
        val repository = QuestionRepository(AppDatabase.getInstance(requireContext()))
        viewModel = ViewModelProvider(
            this,
            ChapterListViewModelFactory(repository)
        ).get(ChapterListViewModel::class.java)
        // 设置RecyclerView
        val adapter = ChapterAdapter { chapter ->
            (activity as? MainActivity)?.navigateToQuestions(chapter)
        }
        binding.chapterRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.chapterRecyclerView.adapter = adapter
        // 观察“所有章节”数据
        viewModel.allChapters.observe(viewLifecycleOwner) { chapters ->
            adapter.submitList(chapters)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}