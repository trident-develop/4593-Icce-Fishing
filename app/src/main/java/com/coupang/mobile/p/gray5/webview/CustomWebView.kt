package com.coupang.mobile.p.gray5.webview

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.core.view.children
import androidx.core.view.isNotEmpty
import androidx.core.view.isVisible

@SuppressLint("ViewConstructor")
class CustomWebView(
    private val activity: ComponentActivity
) : WebView(activity) {

    private val contentRoot: FrameLayout = FrameLayout(activity)

    val popupContainer: FrameLayout = FrameLayout(activity).apply {
        isVisible = false
        layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
    }

    val fullscreenContainer: FrameLayout = FrameLayout(activity).apply {
        isVisible = false
        layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (popupContainer.isNotEmpty()) {
                val top = popupContainer.getChildAt(popupContainer.childCount - 1) as WebView
                if (top.canGoBack()) {
                    top.goBack()
                } else {
                    top.stopLoading()
                    (top.parent as? ViewGroup)?.removeView(top)
                    top.destroy()
                    popupContainer.isVisible = popupContainer.isNotEmpty()
                }
                return
            }
            if (canGoBack()) goBack()
        }
    }

    init {
        val content: ViewGroup = activity.findViewById(android.R.id.content)
        content.addView(contentRoot)
        contentRoot.addView(
            this,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
        contentRoot.addView(popupContainer)
        contentRoot.addView(fullscreenContainer)
        contentRoot.isVisible = false
    }

    fun showWebView() {
        if (!contentRoot.isVisible) {
            activity.onBackPressedDispatcher.addCallback(activity, backPressedCallback)
            activity.findViewById<ViewGroup>(android.R.id.content).children.forEach {
                it.isVisible = false
            }
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            contentRoot.isVisible = true
        }
    }
}
