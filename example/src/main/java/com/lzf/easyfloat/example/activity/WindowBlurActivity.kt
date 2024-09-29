package com.lzf.easyfloat.example.activity

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.enums.SidePattern
import com.lzf.easyfloat.example.BlurWindowHelper
import com.lzf.easyfloat.example.R
import com.lzf.easyfloat.example.helper.WindowBlurHelper
import com.lzf.easyfloat.interfaces.OnInvokeView
import com.lzf.easyfloat.interfaces.OnTouchRangeListener
import com.lzf.easyfloat.utils.DragUtils
import com.lzf.easyfloat.widget.BaseSwitchView

class WindowBlurActivity : BaseActivity() {

    companion object {
        const val TAG = "WindowBlurActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_window_blur)
        findViewById<Button>(R.id.btn_show_window).setOnClickListener {
            BlurWindowHelper(this).showWindow()
        }

        findViewById<Button>(R.id.btn_show_easy_float).setOnClickListener {
            showAppFloat()
        }
    }

    private fun showAppFloat() {
        EasyFloat.with(this.applicationContext)
            .setShowPattern(ShowPattern.ALL_TIME)
            .setSidePattern(SidePattern.DEFAULT)
            .setImmersionStatusBar(true)
            .setBlurEnable(true)
            .setGravity(Gravity.CENTER_HORIZONTAL, 0, 100)
            .setBlurCallback { windowManager, view ->
                Log.d(TAG, "showAppFloat: init blur...")
                WindowBlurHelper(this).initBlur(windowManager, view, R.drawable.card_bg)
            }
            .setLayout(R.layout.float_app_blur, object : OnInvokeView{
                override fun invoke(view: View?) {
                    Log.d(TAG, "invoke")
                    view?.findViewById<ImageView>(R.id.ivClose)?.setOnClickListener {
                        EasyFloat.dismiss()
                    }
                }

            })
            .registerCallback {
                drag { _, motionEvent ->
                    DragUtils.registerDragClose(motionEvent, object : OnTouchRangeListener {
                        override fun touchInRange(inRange: Boolean, view: BaseSwitchView) {
                            setVibrator(inRange)
                            view.findViewById<TextView>(R.id.tv_delete).text =
                                if (inRange) "松手删除" else "删除浮窗"

                            view.findViewById<ImageView>(R.id.iv_delete)
                                .setImageResource(
                                    if (inRange) R.drawable.icon_delete_selected
                                    else R.drawable.icon_delete_normal
                                )
                        }

                        override fun touchUpInRange() {
                            EasyFloat.dismiss()
                        }
                    }, showPattern = ShowPattern.ALL_TIME)
                }

                show {
                    Log.d(TAG, "showAppFloat: show floating window.")
                }
            }
            .show()
    }


}