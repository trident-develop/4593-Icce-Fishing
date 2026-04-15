package com.coupang.mobile.p.gray5

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import com.coupang.mobile.p.gray5.webview.CustomWebChromeClient
import com.coupang.mobile.p.gray5.webview.CustomWebView
import com.coupang.mobile.p.gray5.webview.CustomWebViewClient
import com.coupang.mobile.p.gray5.webview.LauncherCallbacks
import com.coupang.mobile.p.gray5.webview.setupView

internal fun launchWebView(
    activity: ComponentActivity,
    url: String,
    storage: G5Storage,
    onStub: () -> Unit,
    fileLauncher: ActivityResultLauncher<String>,
    cameraLauncher: ActivityResultLauncher<String>,
    callbacks: LauncherCallbacks
) {
    val wv     = CustomWebView(activity)
    val client = CustomWebViewClient(activity, onStubRequired = onStub)
    val chrome = CustomWebChromeClient(
        activity, wv,
        onStubRequired = onStub,
        launcher = fileLauncher,
        cameraPermLauncher = cameraLauncher,
        callbackHolder = callbacks
    )
    setupView(wv, client, chrome)
    
    wv.loadUrl(url)
}
