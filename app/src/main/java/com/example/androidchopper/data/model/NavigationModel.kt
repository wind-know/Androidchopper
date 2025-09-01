package com.example.androidchopper.data.model

import com.example.androidchopper.presentation.view.study.StudyFragment
import com.example.androidchopper.presentation.viewmodel.NavigationInfo

class NavigationModel {
    fun getNavigationInfo(command: String): List<NavigationInfo> {
        return listOf(
//            NavigationInfo("Study", StudyFragment()),
//            NavigationInfo("Ai", AiFragment()),
            NavigationInfo("More", StudyFragment()),
//            NavigationInfo("Setting", SettingFragment())
        )
    }
}