package com.coupang.mobile.p.gray5.webview

import android.content.Intent
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.net.toUri
import com.coupang.mobile.p.gray5.G5Storage
import com.coupang.mobile.p.gray5.BASE_URL_STRICT
import com.coupang.mobile.p.gray5.k
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class CustomWebViewClient(
    private val activity: ComponentActivity,
    private val onStubRequired: () -> Unit = {}
) : WebViewClient() {

    private val storage by lazy { G5Storage.getInstance(activity.applicationContext) }
    private val linkKey = k("lx5")
    private val storageMutex = Mutex()

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val uri = request?.url ?: return false
        if (uri.scheme == "about") return false

        val intent = when (uri.scheme) {
            "intent" -> runCatching {
                Intent.parseUri(uri.toString(), Intent.URI_INTENT_SCHEME)
            }.getOrNull()
            "mailto" -> Intent(Intent.ACTION_SENDTO, uri)
            "tel"    -> Intent(Intent.ACTION_DIAL, uri)
            "http", "https", "blob", "data" -> null
            else     -> Intent(Intent.ACTION_VIEW, uri)
        }

        intent?.let {
            try { activity.startActivity(it); return true } catch (_: Throwable) {}

            val packageName = intent.`package` ?: intent.component?.packageName
            if (packageName == null) {
                Toast.makeText(activity, "No application found!", Toast.LENGTH_LONG).show()
                return true
            }

            try {
                val googlePlayIntent = Intent(
                    Intent.ACTION_VIEW,
                    "market://details?id=$packageName".toUri()
                )
                googlePlayIntent.setPackage("com.android.vending")
                activity.startActivity(googlePlayIntent)
                return true
            } catch (_: Throwable) {}

            try {
                activity.startActivity(
                    Intent(Intent.ACTION_VIEW, "market://details?id=$packageName".toUri())
                )
                return true
            } catch (_: Throwable) {}

            try {
                activity.startActivity(
                    Intent(Intent.ACTION_VIEW, "https://play.google.com/store/apps/details?id=$packageName".toUri())
                )
            } catch (_: Throwable) {
                Toast.makeText(activity, "No application found!", Toast.LENGTH_LONG).show()
            }
            return true
        }
        return false
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        CoroutineScope(Dispatchers.IO).launch { CookieManager.getInstance().flush() }

        if (url == null) return

        
        if (url.startsWith("https://$BASE_URL_STRICT")) {
            
            onStubRequired()
        } else {
            
            saveUrlIfNeeded(url)
            (view as? CustomWebView)?.showWebView()
        }
    }

    private fun saveUrlIfNeeded(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            storageMutex.withLock {
                if (storage.get(linkKey) == null) {
                    
                    storage.put(linkKey, url)
                }
            }
        }
    }
}
