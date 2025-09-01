package com.example.androidchopper.presentation.view
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.androidchopper.R
import com.example.androidchopper.data.database.AppDatabase
import com.example.androidchopper.data.database.toQuestion
import com.example.androidchopper.databinding.ActivityMainBinding
import com.example.androidchopper.presentation.viewmodel.MainViewModel
import kotlinx.coroutines.launch
abstract class BaseActivity : AppCompatActivity() {
    protected var firstBackPressedTime: Long = 0
    protected val handler = Handler(Looper.getMainLooper())

    companion object {
        const val DOUBLE_CLICK_TIME_DELAY = 2000L
    }

    protected fun setupWindowInsets(rootView: View) {
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    protected fun setupStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.transparent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
        }
    }

    protected fun setupBackPressHandler(callback: (() -> Unit)? = null) {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                callback?.invoke() ?: handleDoubleBackPress()
            }
        })
    }

    protected open fun handleDoubleBackPress() {
        val currentTime = System.currentTimeMillis()
        if (firstBackPressedTime == 0L) {
            firstBackPressedTime = currentTime
            showToast("再按一次退出")
        } else {
            if (currentTime - firstBackPressedTime < DOUBLE_CLICK_TIME_DELAY) {
                finish()
            } else {
                firstBackPressedTime = 0
                showToast("再按一次退出")
            }
        }
    }

    protected fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    protected fun dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density + 0.5f).toInt()

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}