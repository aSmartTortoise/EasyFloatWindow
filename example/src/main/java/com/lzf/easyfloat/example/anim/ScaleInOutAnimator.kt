package com.lzf.easyfloat.example.anim

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.view.View
import android.view.WindowManager
import com.lzf.easyfloat.enums.SidePattern
import com.lzf.easyfloat.interfaces.OnFloatAnimator

/**
 *  author : jie wang
 *  date : 2024/10/17 10:11
 *  description :
 */
class ScaleInOutAnimator : OnFloatAnimator {

    override fun enterAnim(
        view: View,
        params: WindowManager.LayoutParams,
        windowManager: WindowManager,
        sidePattern: SidePattern
    ): Animator? {
        return getAnimator(view, params, windowManager, sidePattern, false)
    }

    override fun exitAnim(
        view: View,
        params: WindowManager.LayoutParams,
        windowManager: WindowManager,
        sidePattern: SidePattern
    ): Animator? {
        return getAnimator(view, params, windowManager, sidePattern, true)
    }

    @SuppressLint("Recycle")
    private fun getAnimator(
        view: View,
        params: WindowManager.LayoutParams,
        windowManager: WindowManager,
        sidePattern: SidePattern,
        isExit: Boolean
    ): Animator {
        val startValue = if (isExit) 1f else 0f
        val endValue = if (isExit) 0f else 1f
        return ObjectAnimator.ofFloat(view, "alpha", startValue, endValue).apply {
            duration = 200L
        }
    }
}