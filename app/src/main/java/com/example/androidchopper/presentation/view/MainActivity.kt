package com.example.androidchopper.presentation.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Outline
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewOutlineProvider
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.example.androidchopper.R
import com.example.androidchopper.databinding.ActivityMainBinding
import com.example.androidchopper.presentation.viewmodel.NavigationInfo
import com.example.androidchopper.presentation.viewmodel.NavigationState
import com.example.androidchopper.presentation.viewmodel.NavigationViewModel
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {

    companion object {
        private const val ANIMATION_DELAY = 200L
        private const val SUB_BUTTON_SIZE_DP = 100
        private const val EXIT_ANIM_DURATION = 500L
        private const val COORDINATOR_ANIM_DURATION = 500L
        private const val RETURN_ANIM_DURATION = 700L
    }

    private lateinit var binding: ActivityMainBinding
    private val viewModel: NavigationViewModel by viewModels()
    private var floatingActionMenu: FloatingActionMenu? = null
    private lateinit var fragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fragmentManager = supportFragmentManager

        setupWindowInsets(binding.root)
        setupStatusBar()
        setupNavigation()
        setupBackPressHandler()
        setupFabBaseListener()

//        if (savedInstanceState == null) {
//            showChapterList()
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        floatingActionMenu?.close(true)
    }

    // region [导航相关功能]
    private fun setupNavigation() {
        viewModel.loadNavigationInfo("开！")

        lifecycleScope.launch {
            viewModel.navigationState.collect { state ->
                when (state) {
                    is NavigationState.Loading -> showLoading()
                    is NavigationState.Success -> showNavigationInformation(state.info)
                    is NavigationState.Error -> showError(state.message)
                    NavigationState.Idle -> Unit
                }
            }
        }
    }

    private fun showLoading() {
        // 显示加载状态
    }

    private fun showNavigationInformation(navigationInfos: List<NavigationInfo>) {
        fragmentManager.beginTransaction().apply {
            navigationInfos.forEachIndexed { index, info ->
                add(binding.fcvNavigation.id, info.fragment)
                if (index != 0) hide(info.fragment) // 默认显示第一个
            }
            commit()
        }

        setupBottomNavigation(navigationInfos)
        if (floatingActionMenu == null) initFloatActionButton()
    }

    private fun setupBottomNavigation(navigationInfos: List<NavigationInfo>) {
        binding.cnbNavigation.setOnItemSelectedListener { id ->
            handleMenuItemSelection(id, navigationInfos)
            true
        }
        binding.cnbNavigation.setItemSelected(R.id.study, true)
    }

    private fun handleMenuItemSelection(itemId: Int, navigationInfos: List<NavigationInfo>) {
        navigationInfos.find { it.fragmentName == getTitleForItemId(itemId) }?.let {
            showSelectedFragment(it.fragment)
        }
        floatingActionMenu?.takeIf { it.isOpen }?.close(true)
    }

    private fun getTitleForItemId(itemId: Int): String {
        return when (itemId) {
            R.id.study -> "Study"
            R.id.ai -> "Ai"
            R.id.more -> "More"
            R.id.setting -> "Setting"
            else -> ""
        }
    }

    private fun showSelectedFragment(fragment: Fragment) {
        fragmentManager.beginTransaction().apply {
            fragmentManager.fragments.forEach { hide(it) }
            show(fragment)
            commit()
        }
    }

    private fun showChapterList() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fcv_navigation, ChapterListFragment())
            .commit()
    }

    fun navigateToQuestions(chapter: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fcv_navigation, QuestionFragment.newInstance(chapter))
            .addToBackStack(null)
            .commit()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    // endregion

    // region [浮动按钮功能]
    private fun setupFabBaseListener() {
        binding.fabNavigation.setOnClickListener {
            startFabScaleAnimation(1f, 1.2f)
            handler.postDelayed({ startFabScaleAnimation(1.2f, 1f) }, 150L)
            floatingActionMenu?.let { menu ->
                if (menu.isOpen) menu.close(true) else menu.open(true)
            } ?: run { initFloatActionButton() }
        }
    }

    private fun initFloatActionButton() {
        if (floatingActionMenu != null) return

        val builder = SubActionButton.Builder(this)
        val recordButton = createCircleImageSubActionButton(builder, R.drawable.ic_setting) {
            handleActionButtonClick("心情录")
        }
        val bottleButton = createCircleImageSubActionButton(builder, R.drawable.ic_setting) {
            handleActionButtonClick("留声瓶")
        }
        val wishingButton = createCircleImageSubActionButton(builder, R.drawable.ic_setting) {
            handleActionButtonClick("许愿")
        }

        floatingActionMenu = FloatingActionMenu.Builder(this)
            .setStartAngle(220)
            .setEndAngle(320)
            .setRadius(dpToPx(120))
            .addSubActionView(wishingButton)
            .addSubActionView(recordButton)
            .addSubActionView(bottleButton)
            .attachTo(binding.fabNavigation)
            .build()
    }

    private fun createCircleImageSubActionButton(
        builder: SubActionButton.Builder,
        imageRes: Int,
        clickListener: () -> Unit
    ): SubActionButton {
        val imageView = ImageView(this).apply {
            setImageResource(imageRes)
            scaleType = ImageView.ScaleType.FIT_XY
            background = null
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    outline.setOval(0, 0, view.width, view.height)
                }
            }
            clipToOutline = true
        }

        return builder.setContentView(imageView)
            .setLayoutParams(
                FrameLayout.LayoutParams(
                    dpToPx(SUB_BUTTON_SIZE_DP),
                    dpToPx(SUB_BUTTON_SIZE_DP)
                )
            )
            .setBackgroundDrawable(null)
            .build()
            .apply {
                setOnClickListener { clickListener() }
                setOnTouchListener { view, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> view.alpha = 0.7f
                        MotionEvent.ACTION_UP -> view.alpha = 1f
                    }
                    false
                }
            }
    }

    private fun handleActionButtonClick(actionName: String) {
        floatingActionMenu?.close(true)
        startExitAnimation {
            when (actionName) {
                "心情录" -> { /* TODO */ }
                "许愿" -> { /* TODO */ }
                "留声瓶" -> { /* TODO */ }
            }
            handler.postDelayed({ startReturnAnimation() }, ANIMATION_DELAY)
        }
    }
    // endregion

    // region [动画效果]
    private fun startExitAnimation(onEnd: (() -> Unit)? = null) {
        val fcvAnim = ObjectAnimator.ofFloat(
            binding.fcvNavigation, "translationY", 0f, -binding.fcvNavigation.height.toFloat()
        ).apply {
            duration = EXIT_ANIM_DURATION
            interpolator = AccelerateDecelerateInterpolator()
        }

        val coordinatorAnim = ObjectAnimator.ofFloat(
            binding.coordinatorLayout,
            "translationY",
            0f,
            binding.coordinatorLayout.height.toFloat()
        ).apply {
            duration = COORDINATOR_ANIM_DURATION
            interpolator = AccelerateDecelerateInterpolator()
        }

        fcvAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onEnd?.invoke()
            }
        })

        fcvAnim.start()
        coordinatorAnim.start()
    }

    private fun startReturnAnimation() {
        val coordinatorReturn = ObjectAnimator.ofFloat(
            binding.coordinatorLayout, "translationY",
            binding.coordinatorLayout.height.toFloat(), 0f
        ).apply {
            duration = RETURN_ANIM_DURATION
            interpolator = OvershootInterpolator(1.2f)
        }

        coordinatorReturn.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                startFcvReturnAnimation()
            }
        })

        coordinatorReturn.start()
    }

    private fun startFcvReturnAnimation() {
        ObjectAnimator.ofFloat(
            binding.fcvNavigation, "translationY",
            -binding.fcvNavigation.height.toFloat(), 0f
        ).apply {
            duration = RETURN_ANIM_DURATION
            interpolator = OvershootInterpolator(1.2f)
            start()
        }
    }

    private fun startFabScaleAnimation(fromScale: Float, toScale: Float) {
        ValueAnimator.ofFloat(fromScale, toScale).apply {
            duration = 300L
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                val scale = animation.animatedValue as Float
                binding.fabNavigation.scaleX = scale
                binding.fabNavigation.scaleY = scale
            }
            start()
        }
    }
    // endregion
}
