package com.lzf.easyfloat.example.activity

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.enums.SidePattern
import com.lzf.easyfloat.example.R
import com.lzf.easyfloat.example.helper.WindowBlurHelper
import com.lzf.easyfloat.example.widget.RoundProgressBar
import com.lzf.easyfloat.interfaces.OnInvokeView
import com.lzf.easyfloat.interfaces.OnTouchRangeListener
import com.lzf.easyfloat.utils.DragUtils
import com.lzf.easyfloat.widget.BaseSwitchView

class WindowShadowActivity : BaseActivity() {

    companion object {
        const val TAG = "WindowShadowActivity"
    }

    private val dp200: Int by lazy(LazyThreadSafetyMode.NONE) {
        resources.getDimensionPixelSize(R.dimen.dp_200)
    }

    private val dp210: Int by lazy(LazyThreadSafetyMode.NONE) {
        resources.getDimensionPixelSize(R.dimen.dp_210)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_window_shadow)

        findViewById<Button>(R.id.btn_show_float).setOnClickListener {
            showAppFloat()
        }

        Log.d(TAG, "onCreate: dp200 $dp200, dp210:$dp210")
        findViewById<LinearLayout>(R.id.ll_shape).apply {
            post {
                Log.d(TAG, "onCreate: shadow layout width $width , height:$height")
            }
        }

        findViewById<FrameLayout>(R.id.fl_container).apply {
            post {
                Log.d(TAG, "onCreate: container width $width , height:$height")
            }
        }
    }

    private fun showAppFloat() {
        EasyFloat.with(this.applicationContext)
            .setShowPattern(ShowPattern.ALL_TIME)
            .setImmersionStatusBar(true)
            .setGravity(Gravity.END, -20, 200)
            .setLayout(R.layout.float_window_shadow, object : OnInvokeView{
                override fun invoke(view: View?) {
                    Log.d(WindowBlurActivity.TAG, "invoke")
                    view?.findViewById<ImageView>(R.id.ivClose)?.setOnClickListener {
                        EasyFloat.dismiss()
                    }
                }

            })
            .show()
    }
}