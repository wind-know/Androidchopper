package com.example.androidchopper.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Outline
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.example.androidchopper.R

@SuppressLint("MissingInflatedId")
class FlexibleCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    enum class Mode {
        CARD_ICON_TEXT,         // 正方形模式
        LEFT_IMAGE_RIGHT_TEXT,  // 小长条模式
        LEFT_TEXT_RIGHT_IMAGE   // 全屏模式
    }

    // 尺寸控制
    private var squareSizeDp = 60
    private var smallRectHeightDp = 60
    private var fullScreenHeightDp = 60
    private var isFullScreenOverlay = false

    private val rootLayout: ConstraintLayout
    private val iconIv: ImageView
    private val singleTextTv: TextView
    private val titleTv: TextView
    private val descriptionTv: TextView
    private val constraintSet = ConstraintSet()

    private var currentMode = Mode.CARD_ICON_TEXT

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_flexible_card, this, false)
        addView(view)

        rootLayout = view.findViewById(R.id.root_constraint)
        iconIv = view.findViewById(R.id.icon_iv)

        singleTextTv = view.findViewById(R.id.single_text_tv)
        titleTv = view.findViewById(R.id.title_tv)
        descriptionTv = view.findViewById(R.id.description_tv)

        constraintSet.clone(rootLayout)

        radius = 8.dpToPx().toFloat()
        cardElevation = 4.dpToPx().toFloat()
        preventCornerOverlap = true
    }

    // ---------------- 外部方法 ----------------
    fun setMode(mode: Mode) {
        currentMode = mode
        resetVisibility()
        updateLayoutParamsByMode(mode)
        updateConstraintsByMode(mode)
        constraintSet.applyTo(rootLayout) // 确保应用约束
    }

    fun setSquareSize(sizeDp: Int) {
        squareSizeDp = sizeDp
        if (currentMode == Mode.CARD_ICON_TEXT) setMode(currentMode)
    }

    fun setCardHeight(heightDp: Int) {
        when (currentMode) {
            Mode.LEFT_IMAGE_RIGHT_TEXT -> smallRectHeightDp = heightDp
            Mode.LEFT_TEXT_RIGHT_IMAGE -> fullScreenHeightDp = heightDp
            else -> {}
        }
        setMode(currentMode)
    }

    fun setFullScreenOverlay(overlay: Boolean) {
        isFullScreenOverlay = overlay
        if (currentMode == Mode.LEFT_TEXT_RIGHT_IMAGE) setMode(currentMode)
    }

    fun setIconResource(resId: Int) { iconIv.setImageResource(resId) }
    fun setSingleText(text: String) { singleTextTv.text = text }
    fun setTexts(title: String, desc: String) {
        titleTv.text = title
        descriptionTv.text = desc
    }

    fun setCardCornerRadius(radiusDp: Float) {
        this.radius = radiusDp.dpToPx().toFloat()
    }

    // ---------------- 内部方法 ----------------
    private fun updateLayoutParamsByMode(mode: Mode) {
        val lp = layoutParams ?: LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        when (mode) {
            Mode.CARD_ICON_TEXT -> {
                val size = squareSizeDp.dpToPx()
                lp.width = size
                lp.height = size
            }
            Mode.LEFT_IMAGE_RIGHT_TEXT -> {
                val h = smallRectHeightDp.dpToPx()
                lp.height = h
                lp.width = h * 2
            }
            Mode.LEFT_TEXT_RIGHT_IMAGE -> {
                lp.width = LayoutParams.MATCH_PARENT
                lp.height = fullScreenHeightDp.dpToPx()
                val pad = if (isFullScreenOverlay) 0 else 8.dpToPx()
                setContentPadding(pad, pad, pad, pad)
            }
        }
        layoutParams = lp
    }

    private fun updateConstraintsByMode(mode: Mode) {
        constraintSet.clone(rootLayout)

        constraintSet.clear(iconIv.id)
        constraintSet.clear(singleTextTv.id)
        constraintSet.clear(titleTv.id)
        constraintSet.clear(descriptionTv.id)

        when (mode) {
            Mode.CARD_ICON_TEXT -> {
                iconIv.visibility = View.VISIBLE
                singleTextTv.visibility = View.VISIBLE

                // 图标居中
                constraintSet.centerHorizontally(iconIv.id, ConstraintSet.PARENT_ID)
                constraintSet.centerVertically(iconIv.id, ConstraintSet.PARENT_ID)
                constraintSet.setVerticalBias(iconIv.id, 0.3f) // 值越小越靠上
                constraintSet.constrainWidth(iconIv.id, (squareSizeDp * 0.5).dpToPx())
                constraintSet.constrainHeight(iconIv.id, (squareSizeDp * 0.5).dpToPx())
//                // 文字在图标下方
                constraintSet.connect(singleTextTv.id, ConstraintSet.TOP, iconIv.id, ConstraintSet.BOTTOM, 2.dpToPx())
                constraintSet.connect(singleTextTv.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 4.dpToPx())
                constraintSet.connect(singleTextTv.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 4.dpToPx())
                constraintSet.connect(singleTextTv.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
                constraintSet.setVerticalBias(singleTextTv.id, 1f)
            }

            Mode.LEFT_IMAGE_RIGHT_TEXT -> {
                iconIv.visibility = View.VISIBLE
                titleTv.visibility = View.VISIBLE
                descriptionTv.visibility = View.VISIBLE

                // 图标居左，高度撑满
                constraintSet.connect(iconIv.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 2.dpToPx())
                constraintSet.connect(iconIv.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 8.dpToPx())
                constraintSet.connect(iconIv.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 8.dpToPx())
                constraintSet.constrainWidth(iconIv.id, (smallRectHeightDp * 0.5).dpToPx())
                constraintSet.constrainHeight(iconIv.id, ConstraintSet.MATCH_CONSTRAINT)

// 标题
                constraintSet.constrainWidth(titleTv.id, ConstraintSet.MATCH_CONSTRAINT)
                constraintSet.connect(titleTv.id, ConstraintSet.START, iconIv.id, ConstraintSet.END, 8.dpToPx())
                constraintSet.connect(titleTv.id, ConstraintSet.TOP, iconIv.id, ConstraintSet.TOP)
                constraintSet.connect(titleTv.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 8.dpToPx())
                constraintSet.constrainHeight(titleTv.id, ConstraintSet.WRAP_CONTENT)

// 描述
                constraintSet.constrainWidth(descriptionTv.id, ConstraintSet.MATCH_CONSTRAINT)
                constraintSet.connect(descriptionTv.id, ConstraintSet.TOP, titleTv.id, ConstraintSet.BOTTOM, 4.dpToPx())
                constraintSet.connect(descriptionTv.id, ConstraintSet.START, titleTv.id, ConstraintSet.START)
                constraintSet.connect(descriptionTv.id, ConstraintSet.END, titleTv.id, ConstraintSet.END)
                constraintSet.constrainHeight(descriptionTv.id, ConstraintSet.WRAP_CONTENT)

            }

            Mode.LEFT_TEXT_RIGHT_IMAGE -> {
                iconIv.visibility = View.VISIBLE
                titleTv.visibility = View.VISIBLE
                descriptionTv.visibility = View.VISIBLE

                // 图标居右，高度撑满
                val rightMargin = if (isFullScreenOverlay) 0 else 8.dpToPx()
                constraintSet.connect(iconIv.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, rightMargin)
                constraintSet.connect(iconIv.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                constraintSet.connect(iconIv.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
                constraintSet.constrainWidth(iconIv.id, (fullScreenHeightDp * 0.8).dpToPx())
                constraintSet.constrainHeight(iconIv.id, ConstraintSet.MATCH_CONSTRAINT)

                // 标题
                val leftMargin = if (isFullScreenOverlay) 0 else 8.dpToPx()
                constraintSet.connect(titleTv.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, leftMargin)
                constraintSet.connect(titleTv.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 8.dpToPx())
                constraintSet.connect(titleTv.id, ConstraintSet.END, iconIv.id, ConstraintSet.START, 8.dpToPx())
                constraintSet.constrainHeight(titleTv.id, ConstraintSet.WRAP_CONTENT)

                // 描述
                constraintSet.connect(descriptionTv.id, ConstraintSet.TOP, titleTv.id, ConstraintSet.BOTTOM, 4.dpToPx())
                constraintSet.connect(descriptionTv.id, ConstraintSet.START, titleTv.id, ConstraintSet.START)
                constraintSet.connect(descriptionTv.id, ConstraintSet.END, titleTv.id, ConstraintSet.END)
                constraintSet.constrainHeight(descriptionTv.id, ConstraintSet.WRAP_CONTENT)
                iconIv.post {
                    iconIv.applyRoundCorner(radius)
                }

            }
        }
    }
    private fun ImageView.applyRoundCorner(radius: Float) {
        this.clipToOutline = true
        this.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, radius)
            }
        }
    }

    private fun resetVisibility() {
        iconIv.visibility = View.GONE
        singleTextTv.visibility = View.GONE
        titleTv.visibility = View.GONE
        descriptionTv.visibility = View.GONE
    }

    private fun Int.dpToPx(): Int = (this * context.resources.displayMetrics.density + 0.5f).toInt()
    private fun Double.dpToPx(): Int = (this * context.resources.displayMetrics.density + 0.5f).toInt()
    private fun Float.dpToPx(): Int =
        (this * context.resources.displayMetrics.density + 0.5f).toInt()
}