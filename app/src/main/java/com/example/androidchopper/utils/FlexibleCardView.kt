package com.example.androidchopper.utils

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.example.androidchopper.R

class FlexibleCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {
    // 三种显示模式
    enum class Mode {
        CARD_ICON_TEXT,         // 卡片式：图标 + 下方文字
        LEFT_IMAGE_RIGHT_TEXT,   // 左侧图片，右侧上下文字
        LEFT_TEXT_RIGHT_IMAGE    // 左侧上下文字，右侧图片
    }
    private val rootLayout: ConstraintLayout
    private val iconIv: ImageView
    private val singleTextTv: TextView
    private val titleTv: TextView
    private val descriptionTv: TextView
    private val constraintSet = ConstraintSet()
    init {
        // 加载布局
        val view = LayoutInflater.from(context).inflate(R.layout.layout_flexible_card, this, false)
        addView(view)

        // 初始化视图
        rootLayout = view.findViewById(R.id.root_constraint)
        iconIv = view.findViewById(R.id.icon_iv)
        singleTextTv = view.findViewById(R.id.single_text_tv)
        titleTv = view.findViewById(R.id.title_tv)
        descriptionTv = view.findViewById(R.id.description_tv)

        // 初始化约束集
        constraintSet.clone(rootLayout)
    }
    // 设置显示模式
    fun setMode(mode: Mode) {
        // 先重置所有视图的可见性
        resetVisibility()

        when (mode) {
            Mode.CARD_ICON_TEXT -> setupCardIconTextMode()
            Mode.LEFT_IMAGE_RIGHT_TEXT -> setupLeftImageRightTextMode()
            Mode.LEFT_TEXT_RIGHT_IMAGE -> setupLeftTextRightImageMode()
        }

        // 应用约束
        constraintSet.applyTo(rootLayout)
    }
    // 卡片式：图标 + 下方文字
    private fun setupCardIconTextMode() {
        iconIv.visibility = VISIBLE
        singleTextTv.visibility = VISIBLE
        // 图标居中
        constraintSet.centerHorizontally(iconIv.id, ConstraintSet.PARENT_ID)
        // 文字在图标下方并居中对齐
        constraintSet.centerHorizontally(singleTextTv.id, iconIv.id)
    }

    // 左侧图片，右侧上下文字
    private fun setupLeftImageRightTextMode() {
        iconIv.visibility = VISIBLE
        titleTv.visibility = VISIBLE
        descriptionTv.visibility = VISIBLE

        // 图标居左
        constraintSet.connect(iconIv.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSet.clear(iconIv.id, ConstraintSet.END)

        // 标题在图标右侧
        constraintSet.connect(titleTv.id, ConstraintSet.START, iconIv.id, ConstraintSet.END, 16)
        constraintSet.connect(titleTv.id, ConstraintSet.TOP, iconIv.id, ConstraintSet.TOP)
        constraintSet.connect(titleTv.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)

        // 描述在标题下方
        constraintSet.connect(descriptionTv.id, ConstraintSet.TOP, titleTv.id, ConstraintSet.BOTTOM, 4)
        constraintSet.connect(descriptionTv.id, ConstraintSet.START, titleTv.id, ConstraintSet.START)
        constraintSet.connect(descriptionTv.id, ConstraintSet.END, titleTv.id, ConstraintSet.END)
    }

    // 左侧上下文字，右侧图片
    private fun setupLeftTextRightImageMode() {
        iconIv.visibility = VISIBLE
        titleTv.visibility = VISIBLE
        descriptionTv.visibility = VISIBLE

        // 图标居右
        constraintSet.connect(iconIv.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        constraintSet.clear(iconIv.id, ConstraintSet.START)

        // 标题在图标左侧
        constraintSet.connect(titleTv.id, ConstraintSet.END, iconIv.id, ConstraintSet.START, 16)
        constraintSet.connect(titleTv.id, ConstraintSet.TOP, iconIv.id, ConstraintSet.TOP)
        constraintSet.connect(titleTv.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)

        // 描述在标题下方
        constraintSet.connect(descriptionTv.id, ConstraintSet.TOP, titleTv.id, ConstraintSet.BOTTOM, 4)
        constraintSet.connect(descriptionTv.id, ConstraintSet.START, titleTv.id, ConstraintSet.START)
        constraintSet.connect(descriptionTv.id, ConstraintSet.END, titleTv.id, ConstraintSet.END)
    }

    // 重置所有视图的可见性
    private fun resetVisibility() {
        iconIv.visibility = GONE
        singleTextTv.visibility = GONE
        titleTv.visibility = GONE
        descriptionTv.visibility = GONE
    }

    // 设置图标资源
    fun setIconResource(resId: Int) {
        iconIv.setImageResource(resId)
    }

    // 设置卡片模式下的文字
    fun setSingleText(text: String) {
        singleTextTv.text = text
    }

    // 设置双文本模式下的标题和描述
    fun setTexts(title: String, description: String) {
        titleTv.text = title
        descriptionTv.text = description
    }

    // 设置卡片圆角
    fun setCardCornerRadius(radius: Float) {
        this.radius = radius
    }
}
    