package com.lzf.easyfloat.example;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 * author : jie wang
 * date : 2024/9/27 17:35
 * description :
 */

public class BlurWindowHelper {

    private static final String TAG = "BlurWindowHelper";

    private WindowManager mWindowManager;
    //窗口背景高斯模糊程度
    private int mBackgroundBlurRadius;
    private int mBackgroundCornersRadius;

    // 根据窗口高斯模糊功能是否开启来为窗口设置不同的不透明度
    private final int mWindowBackgroundAlphaWithBlur = 170;
    private final int mWindowBackgroundAlphaNoBlur = 255;

    //使用一个矩形drawable文件作为窗口背景，这个矩形的轮廓和圆角确定了窗口高斯模糊的区域
    private Context mContext;
    private View mView;
    private Drawable mWindowBackgroundDrawable;

    public BlurWindowHelper(Context context) {
        this.mContext = context;
    }

    public void showWindow() {
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mView = LayoutInflater.from(mContext).inflate(R.layout.window_blur, null, false);
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindowManager.removeView(mView);
            }
        });
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH,
//                PixelFormat.TRANSLUCENT);//半透明
                PixelFormat.TRANSPARENT);//全透明
        layoutParams.setTitle("LeapMotorNavigationBar");
        layoutParams.windowAnimations = 0;
        layoutParams.gravity = Gravity.CENTER;
        mWindowManager.addView(mView, layoutParams);
        initBlur();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initBlur() {
        mBackgroundBlurRadius = dp2px(40);
        mBackgroundCornersRadius = dp2px(20);
        mWindowBackgroundDrawable = mContext.getDrawable(R.drawable.window_background);
        mView.setBackground(mWindowBackgroundDrawable);
        setupWindowBlurListener();
    }

    private void setupWindowBlurListener() {
        Consumer<Boolean> windowBlurEnabledListener = this::updateWindowForBlurs;
        mView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(@NonNull View v) {
                mWindowManager.addCrossWindowBlurEnabledListener(windowBlurEnabledListener);
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull View v) {
                mWindowManager.removeCrossWindowBlurEnabledListener(windowBlurEnabledListener);
            }
        });
    }


    private void updateWindowForBlurs(boolean blursEnabled) {
        Log.d(TAG, "updateWindowForBlurs: blursEnabled:" + blursEnabled);
        // 根据窗口高斯模糊功能是否开启来为窗口设置不同的不透明度
        mWindowBackgroundDrawable.setAlpha(
                blursEnabled ? mWindowBackgroundAlphaWithBlur : mWindowBackgroundAlphaNoBlur);
        setBackgroundBlurRadius(mView);
    }

    /**
     * 为View设置高斯模糊背景
     *
     * @param view
     */
    private void setBackgroundBlurRadius(View view) {
        if (view == null) {
            return;
        }
        ViewParent target = view.getParent();
        Drawable backgroundBlurDrawable = getBackgroundBlurDrawableByReflect(target);
        Drawable originDrawable = view.getBackground();
        Drawable destDrawable = new LayerDrawable(new Drawable[]{backgroundBlurDrawable, originDrawable});
        view.setBackground(destDrawable);
    }

    /**
     * 通过添加framework.jar依赖获取BackgroundBlurDrawable实例对象
     *
     * @param target
     * @return
     */
//    private BackgroundBlurDrawable getBackgroundBlurDrawableByFramework(ViewRootImpl target) {
//        BackgroundBlurDrawable backgroundBlurDrawable = ((ViewRootImpl)target).createBackgroundBlurDrawable();
//        backgroundBlurDrawable.setBlurRadius(mBackgroundBlurRadius);
//        backgroundBlurDrawable.setCornerRadius(mBackgroundCornersRadius);
//        return backgroundBlurDrawable;
//    }

    /**
     * 通过反射获取BackgroundBlurDrawable实例对象
     *
     * @param viewRootImpl
     * @return
     */
    private Drawable getBackgroundBlurDrawableByReflect(Object viewRootImpl) {
        Drawable drawable = null;
        try {
            //调用ViewRootImpl的createBackgroundBlurDrawable方法创建实例
            Method method_createBackgroundBlurDrawable = viewRootImpl.getClass()
                    .getDeclaredMethod("createBackgroundBlurDrawable");
            method_createBackgroundBlurDrawable.setAccessible(true);
            drawable = (Drawable) method_createBackgroundBlurDrawable.invoke(viewRootImpl);
            //调用BackgroundBlurDrawable的setBlurRadius方法
            Method method_setBlurRadius = drawable.getClass().getDeclaredMethod("setBlurRadius", int.class);
            method_setBlurRadius.setAccessible(true);
            method_setBlurRadius.invoke(drawable, mBackgroundBlurRadius);
            //调用BackgroundBlurDrawable的setCornerRadius方法
            Method method_setCornerRadius = drawable.getClass().getDeclaredMethod("setCornerRadius", float.class);
            method_setCornerRadius.setAccessible(true);
            method_setCornerRadius.invoke(drawable, mBackgroundCornersRadius);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return drawable;
    }

    /**
     * dip转换成px
     */
    private int dp2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}

