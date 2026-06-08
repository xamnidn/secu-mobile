package com.secu.app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.ComponentActivity

class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = FrameLayout(this).apply {
            setBackgroundColor(Color.parseColor("#1A3D6B"))
        }

        val text = TextView(this).apply {
            text = "secu.my.id"
            setTextColor(Color.WHITE)
            textSize = 20f
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).also { it.gravity = android.view.Gravity.CENTER }
        }

        layout.addView(text)
        setContentView(layout)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 800)
    }
}
