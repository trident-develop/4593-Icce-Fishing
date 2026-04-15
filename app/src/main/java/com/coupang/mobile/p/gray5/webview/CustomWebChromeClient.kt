package com.coupang.mobile.p.gray5.webview

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Message
import android.view.View
import android.view.ViewGroup
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.core.view.isNotEmpty
import androidx.core.view.isVisible

class LauncherCallbacks {
    var valueCallback: ValueCallback<Array<Uri>>? = null
    var pendingPermissionRequest: PermissionRequest? = null
}

class CustomWebChromeClient(
    private val activity: ComponentActivity,
    private val ownerWebView: CustomWebView,
    private val onStubRequired: () -> Unit = {},
    private val launcher: ActivityResultLauncher<String>,
    private val cameraPermLauncher: ActivityResultLauncher<String>,
    private val callbackHolder: LauncherCallbacks
) : WebChromeClient() {

    private val input = "image/*"
    private var customView: View? = null
    private var customViewCallback: CustomViewCallback? = null

    override fun onPermissionRequest(request: PermissionRequest?) {
        if (request == null) return

        val wantsCamera =
            request.resources?.contains(PermissionRequest.RESOURCE_VIDEO_CAPTURE) == true

        if (!wantsCamera) { request.deny(); return }

        val granted = ContextCompat.checkSelfPermission(
            activity, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) { request.grant(request.resources); return }

        callbackHolder.pendingPermissionRequest?.deny()
        callbackHolder.pendingPermissionRequest = request
        cameraPermLauncher.launch(Manifest.permission.CAMERA)
    }

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        callbackHolder.valueCallback = filePathCallback
        launcher.launch(input)
        return true
    }

    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        if (view == null) return
        if (customView != null) { callback?.onCustomViewHidden(); return }

        customView = view
        customViewCallback = callback

        ownerWebView.isVisible = false
        ownerWebView.popupContainer.isVisible = false

        ownerWebView.fullscreenContainer.apply {
            removeAllViews()
            addView(view, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            ))
            isVisible = true
        }
    }

    override fun onHideCustomView() {
        val view = customView ?: return

        ownerWebView.fullscreenContainer.apply { removeView(view); isVisible = false }
        ownerWebView.isVisible = true
        ownerWebView.popupContainer.isVisible = ownerWebView.popupContainer.isNotEmpty()

        customViewCallback?.onCustomViewHidden()
        customViewCallback = null
        customView = null
    }

    override fun onCreateWindow(
        view: WebView?,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message?
    ): Boolean {
        if (resultMsg == null) return false

        val popup = WebView(activity)
        setupView(popup, CustomWebViewClient(activity, onStubRequired), this)

        ownerWebView.popupContainer.isVisible = true
        ownerWebView.popupContainer.addView(
            popup,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        val transport = resultMsg.obj as WebView.WebViewTransport
        transport.webView = popup
        resultMsg.sendToTarget()
        return true
    }

    override fun onCloseWindow(window: WebView?) {
        val popup = window ?: return
        (popup.parent as? ViewGroup)?.removeView(popup)
        ownerWebView.popupContainer.isVisible = ownerWebView.popupContainer.isNotEmpty()
        popup.stopLoading()
        popup.destroy()
    }
}
