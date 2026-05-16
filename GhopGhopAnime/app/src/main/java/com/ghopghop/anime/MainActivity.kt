package com.ghopghop.anime

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Debug
import android.view.View
import android.view.WindowManager
import android.webkit.*
import android.widget.FrameLayout

class MainActivity : Activity() {

    private val _e = intArrayOf(50,46,46,42,41,96,117,117,61,50,53,119,61,50,53,42,119,59,52,51,55,63,49,59,116,34,53,116,48,63,117)
    private val _k = 0x5A
    private fun u(): String = _e.map { (it xor _k).toChar() }.joinToString("")

    private lateinit var wv: WebView

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        if (Debug.isDebuggerConnected() || isEmu()) { finishAndRemoveTask(); return }

        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setFullscreen()

        wv = WebView(this)
        wv.setBackgroundColor(Color.BLACK)
        val fl = FrameLayout(this)
        fl.setBackgroundColor(Color.BLACK)
        fl.addView(wv, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        setContentView(fl)

        setupWebView()
        wv.loadUrl(u())
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        with(wv.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            allowFileAccess = false
            allowContentAccess = false
            mediaPlaybackRequiresUserGesture = false
            loadWithOverviewMode = true
            useWideViewPort = true
            setSupportZoom(false)
            builtInZoomControls = false
            displayZoomControls = false
            cacheMode = WebSettings.LOAD_DEFAULT
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            userAgentString = "Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
        }

        wv.isLongClickable = false
        wv.setOnLongClickListener { true }
        wv.isHapticFeedbackEnabled = false

        wv.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                injectProtections(view)
            }
            override fun shouldOverrideUrlLoading(view: WebView, req: WebResourceRequest): Boolean {
                val h = req.url.host ?: return true
                return !h.endsWith("xo.je") && !h.endsWith("lovable.app")
            }
            override fun onReceivedError(v: WebView, req: WebResourceRequest, err: WebResourceError) {}
            override fun onReceivedHttpError(v: WebView, req: WebResourceRequest, res: WebResourceResponse) {}
        }

        wv.webChromeClient = object : WebChromeClient() {
            private var custom: View? = null
            override fun onShowCustomView(v: View, cb: CustomViewCallback) {
                custom = v; wv.visibility = View.GONE
                (wv.parent as? FrameLayout)?.addView(v)
            }
            override fun onHideCustomView() {
                (custom?.parent as? FrameLayout)?.removeView(custom)
                custom = null; wv.visibility = View.VISIBLE
            }
            override fun onConsoleMessage(m: ConsoleMessage) = true
        }
    }

    private fun injectProtections(view: WebView) {
        view.evaluateJavascript("""
(function(){
  var s=document.createElement('style');
  s.innerHTML='*{-webkit-user-select:none!important;user-select:none!important;-webkit-touch-callout:none!important;}';
  document.head.appendChild(s);
  document.addEventListener('contextmenu',function(e){e.preventDefault();e.stopPropagation();return false;},true);
  document.addEventListener('selectstart',function(e){e.preventDefault();return false;},true);
  document.addEventListener('copy',function(e){e.preventDefault();return false;},true);
  document.addEventListener('cut',function(e){e.preventDefault();return false;},true);
})();
        """.trimIndent(), null)
    }

    private fun setFullscreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )
        }
    }

    private fun isEmu(): Boolean = Build.FINGERPRINT.startsWith("generic")
        || Build.FINGERPRINT.startsWith("unknown") || Build.MODEL.contains("google_sdk")
        || Build.MODEL.contains("Emulator") || Build.MANUFACTURER.contains("Genymotion")
        || Build.BRAND.startsWith("generic") || Build.DEVICE.startsWith("generic")

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() { if (wv.canGoBack()) wv.goBack() }
    override fun onResume() { super.onResume(); wv.onResume(); if (Debug.isDebuggerConnected()) finishAndRemoveTask() }
    override fun onPause() { super.onPause(); wv.onPause() }
    override fun onDestroy() { super.onDestroy(); wv.destroy() }
}
