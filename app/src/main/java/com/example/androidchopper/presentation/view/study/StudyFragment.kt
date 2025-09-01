package com.example.androidchopper.presentation.view.study

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.androidchopper.R
import com.example.androidchopper.utils.FlexibleCardView

class StudyFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // 处理传递的参数（如果有的话）
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 加载布局并返回视图
        return inflater.inflate(R.layout.fragment_study, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 关键修复：通过onCreateView返回的view对象查找控件
        // 1. 卡片式：图标 + 下方文字
        val card1 = view.findViewById<FlexibleCardView>(R.id.card1)
        card1.setMode(FlexibleCardView.Mode.CARD_ICON_TEXT)
        card1.setIconResource(R.drawable.bg) // 确保资源存在
        card1.setSingleText("个人中心")
        card1.setCardCornerRadius(12f)

        // 2. 左侧图片，右侧上下文字
        val card2 = view.findViewById<FlexibleCardView>(R.id.card2)
        card2.setMode(FlexibleCardView.Mode.LEFT_IMAGE_RIGHT_TEXT)
        card2.setIconResource(R.drawable.bg4) // 确保资源存在
        card2.setTexts("新消息通知", "您有3条未读消息")
        card2.setCardCornerRadius(8f)

        // 3. 左侧文字，右侧图片
        val card3 = view.findViewById<FlexibleCardView>(R.id.card3)
        card3.setMode(FlexibleCardView.Mode.LEFT_TEXT_RIGHT_IMAGE)
        card3.setIconResource(R.drawable.android) // 确保资源存在
        card3.setTexts("系统设置", "账号管理、隐私设置等")
    }

    companion object {
        // 用于创建Fragment实例的静态方法（如果需要传递参数）
        @JvmStatic
        fun newInstance() = StudyFragment()
    }
}
