package com.ghopghop.anime

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

class SplashActivity : Activity() {
    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)

        val fl = FrameLayout(this)
        fl.setBackgroundColor(Color.BLACK)

        val iv = ImageView(this)
        try {
            val bm = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            iv.setImageBitmap(bm)
        } catch (e: Exception) {}
        iv.scaleType = ImageView.ScaleType.FIT_CENTER
        val sz = (128 * resources.displayMetrics.density).toInt()
        val lp = FrameLayout.LayoutParams(sz, sz).apply {
            gravity = Gravity.CENTER
            bottomMargin = (80 * resources.displayMetrics.density).toInt()
        }
        fl.addView(iv, lp)

        val tv = TextView(this)
        tv.text = "GHOP GHOP ANIME"
        tv.setTextColor(Color.WHITE)
        tv.textSize = 20f
        tv.letterSpacing = 0.15f
        val tlp = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER
            topMargin = (80 * resources.displayMetrics.density).toInt()
        }
        fl.addView(tv, tlp)

        setContentView(fl)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 1800)
    }
}
