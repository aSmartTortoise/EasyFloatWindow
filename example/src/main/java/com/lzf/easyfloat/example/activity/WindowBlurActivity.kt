package com.lzf.easyfloat.example.activity

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.lzf.easyfloat.example.BlurWindowHelper
import com.lzf.easyfloat.example.R

class WindowBlurActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_window_blur)
        findViewById<Button>(R.id.btn_show_window).setOnClickListener {
            BlurWindowHelper(this).showWindow()
        }
    }
}