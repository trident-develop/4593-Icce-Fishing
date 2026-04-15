package com.coupang.mobile.p

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.coupang.mobile.p.gray5.G5Push
import com.coupang.mobile.p.gray5.TRACKING_ID
import com.coupang.mobile.p.navigation.LoadingGraph
import kotlinx.coroutines.launch

class LoadingActivity : ComponentActivity() {
    private val windowController by lazy {
        WindowInsetsControllerCompat(window, window.decorView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val trakId = this.intent.getStringExtra(TRACKING_ID)
        if(trakId != null) {
            val pushRegistrationManager = G5Push(this)
            lifecycleScope.launch {
                pushRegistrationManager.postback(trakId)
            }
        }
        setContent {
            LoadingGraph()
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }

    private fun hideSystemBars() {
        windowController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowController.hide(WindowInsetsCompat.Type.systemBars())
    }
}