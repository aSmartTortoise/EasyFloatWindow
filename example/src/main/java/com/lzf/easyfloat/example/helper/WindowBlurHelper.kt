package com.lzf.easyfloat.example.helper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.util.Log
import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.view.WindowManager
import java.util.function.Consumer

/**
 *  author : jie wang
 *  date : 2024/9/29 16:05
 *  description :
 *
 *  参考文章：https://blog.csdn.net/abc6368765/article/details/127657069
 *  https://blog.csdn.net/abc6368765/article/details/127967681
 */
class WindowBlurHelper(val context: Context) {

    companion object {
        const val TAG = "WindowBlurHelper"
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun initBlur(windowManager: WindowManager, floatingView: View, drawableRes: Int) {
        val backgroundDrawable = context.getDrawable(drawableRes)
        floatingView.background = backgroundDrawable
        setupWindowBlurListener(windowManager, floatingView)
    }

    private fun setupWindowBlurListener(windowManager: WindowManager, floatingView: View) {
        val windowBlurEnabledListener = Consumer { blursEnabled: Boolean? ->
            this.updateWindowForBlurs(blursEnabled!!, floatingView)
        }
        floatingView.addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                windowManager.addCrossWindowBlurEnabledListener(windowBlurEnabledListener)
            }

            override fun onViewDetachedFromWindow(v: View) {
                windowManager.removeCrossWindowBlurEnabledListener(windowBlurEnabledListener)
            }
        })
    }

    private fun updateWindowForBlurs(blursEnabled: Boolean, floatingView: View) {
        Log.d(TAG, "updateWindowForBlurs: blursEnabled:$blursEnabled")
        val alphaWithBlur = 170
        val alphaNoBlur = 255
        // 根据窗口高斯模糊功能是否开启来为窗口设置不同的不透明度
        floatingView.background.alpha = if (blursEnabled) alphaWithBlur else alphaNoBlur
        setBackgroundBlurRadius(floatingView)
    }

    /**
     * 为View设置高斯模糊背景
     *
     * @param view
     */
    private fun setBackgroundBlurRadius(view: View) {
        val target = view.parent
        val backgroundBlurDrawable: Drawable? = getBackgroundBlurDrawableByReflect(target)
        if (backgroundBlurDrawable != null) {
            val originDrawable = view.background
            val destDrawable: Drawable = LayerDrawable(arrayOf(backgroundBlurDrawable, originDrawable))
            view.background = destDrawable
        }
    }

    /**
     * 通过反射获取BackgroundBlurDrawable实例对象
     *
     * @param viewRootImpl
     * @return
     */
    private fun getBackgroundBlurDrawableByReflect(viewRootImpl: Any): Drawable? {
        val backgroundBlurRadius = dp2px(10f)
        val backgroundCornersRadius = dp2px(20f)
        var drawable: Drawable? = null
        try {
            //调用ViewRootImpl的createBackgroundBlurDrawable方法创建实例
            val method_createBackgroundBlurDrawable = viewRootImpl.javaClass
                .getDeclaredMethod("createBackgroundBlurDrawable")
            method_createBackgroundBlurDrawable.isAccessible = true
            drawable = method_createBackgroundBlurDrawable.invoke(viewRootImpl) as Drawable
            //调用BackgroundBlurDrawable的setBlurRadius方法
            val method_setBlurRadius = drawable!!.javaClass.getDeclaredMethod(
                "setBlurRadius",
                Int::class.javaPrimitiveType
            )
            method_setBlurRadius.isAccessible = true
            method_setBlurRadius.invoke(drawable, backgroundBlurRadius)
            //调用BackgroundBlurDrawable的setCornerRadius方法
            val method_setCornerRadius = drawable!!.javaClass.getDeclaredMethod(
                "setCornerRadius",
                Float::class.javaPrimitiveType
            )
            method_setCornerRadius.isAccessible = true
            method_setCornerRadius.invoke(drawable, backgroundCornersRadius)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return drawable
    }

    private fun dp2px(dpValue: Float): Int {
        val scale: Float = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}