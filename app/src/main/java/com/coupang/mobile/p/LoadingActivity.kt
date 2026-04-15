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
    private var controller: WindowInsetsControllerCompat? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        controller = WindowInsetsControllerCompat(window, window.decorView)
        controller?.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller?.hide(WindowInsetsCompat.Type.systemBars())
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
}