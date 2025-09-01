package com.example.androidchopper.presentation.view.study

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.androidchopper.R
import com.example.androidchopper.utils.FlexibleCardView

class StudyFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_study, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. 正方形模式（CARD_ICON_TEXT）
        val card1 = view.findViewById<FlexibleCardView>(R.id.card1)
        card1.apply {
            setMode(FlexibleCardView.Mode.CARD_ICON_TEXT)
            setSquareSize(80) // 自定义正方形尺寸为80dp（宽高相等）
            setIconResource(R.drawable.android) // 替换为实际图标资源
            setSingleText("个人中心") // 文字会加粗显示
            setCardCornerRadius(12f) // 圆角
            setCardBackgroundColor(resources.getColor(R.color.white)) // 确保背景与文字颜色对比明显
        }

        // 2. 小长方形模式（LEFT_IMAGE_RIGHT_TEXT）
        val card2 = view.findViewById<FlexibleCardView>(R.id.card2)
        card2.apply {
            setMode(FlexibleCardView.Mode.LEFT_IMAGE_RIGHT_TEXT)
            setCardHeight(60) // 固定高度60dp，宽度会自动设为120dp（2倍高度）
            setIconResource(R.drawable.android) // 左侧图标（完全展示）
            setTexts("新消息通知", "您有3条未读消息") // 标题加粗
            setCardCornerRadius(8f)
            setCardBackgroundColor(resources.getColor(R.color.white))
        }

        // 3. 全屏模式（LEFT_TEXT_RIGHT_IMAGE）
        val card3 = view.findViewById<FlexibleCardView>(R.id.card3)
        card3.apply {
            setMode(FlexibleCardView.Mode.LEFT_TEXT_RIGHT_IMAGE)
            setCardHeight(70) // 自定义高度为70dp
            setFullScreenOverlay(false) // 非覆盖模式（有间距）
            setIconResource(R.drawable.android) // 右侧图标（铺满高度）
            setTexts("系统设置", "账号管理、隐私设置等") // 标题加粗
            setCardCornerRadius(0f) // 直角
            setCardBackgroundColor(resources.getColor(R.color.white))
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = StudyFragment()
    }
}