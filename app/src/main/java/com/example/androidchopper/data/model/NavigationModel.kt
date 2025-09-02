package com.example.androidchopper.data.model

import com.example.androidchopper.presentation.view.ChapterListFragment
import com.example.androidchopper.presentation.view.QuestionFragment
import com.example.androidchopper.presentation.view.ReviewFragment
import com.example.androidchopper.presentation.view.study.StudyFragment
import com.example.androidchopper.presentation.viewmodel.NavigationInfo

class NavigationModel {
    fun getNavigationInfo(command: String): List<NavigationInfo> {
        return listOf(
            NavigationInfo("Study", ChapterListFragment()),          // 第1页：章节
            NavigationInfo("Ai", QuestionFragment.newInstance("")), // 第2页：问题（测试随便传个章节id）
            NavigationInfo("More", ReviewFragment()),                // 第3页：复习
//            NavigationInfo("Setting", SettingFragment())             // 第4页：设置
        )
    }
}