package com.coupang.mobile.p.gray5

import android.Manifest
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.coupang.mobile.p.gray5.G5DmProvider
import com.coupang.mobile.p.gray5.G5Engine
import com.coupang.mobile.p.gray5.G5GaProvider
import com.coupang.mobile.p.gray5.G5Push
import com.coupang.mobile.p.gray5.G5ReProvider
import com.coupang.mobile.p.gray5.G5Router
import com.coupang.mobile.p.gray5.G5State
import com.coupang.mobile.p.gray5.G5Storage
import com.coupang.mobile.p.gray5.PUSH_STORAGE_VALUE_TRUE
import com.coupang.mobile.p.gray5.ROUTE_NET
import com.coupang.mobile.p.gray5.ROUTE_STUB
import com.coupang.mobile.p.gray5.k
import com.coupang.mobile.p.gray5.launchWebView
import com.coupang.mobile.p.gray5.webview.LauncherCallbacks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Gray5(
    toStub: () -> Unit,
    toNoInternet: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    val storage = remember { G5Storage.Companion.getInstance(context) }
    val push = remember { G5Push(context) }
    val re = remember { G5ReProvider(context) }
    val ga = remember { G5GaProvider(context) }
    val dm = remember { G5DmProvider() }
    val router = remember { G5Router(mapOf(ROUTE_STUB to toStub, ROUTE_NET to toNoInternet)) }
    val engine = remember { G5Engine(context, storage, push, re, ga, dm, router) }
    val callbacks = remember { LauncherCallbacks() }

    val fileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris -> callbacks.valueCallback?.onReceiveValue(uris.toTypedArray()) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        val req = callbacks.pendingPermissionRequest ?: return@rememberLauncherForActivityResult
        callbacks.pendingPermissionRequest = null
        if (granted) req.grant(req.resources) else req.deny()
    }

    G5NotificationPermission(storage)

    val state by engine.state.collectAsState()

    LaunchedEffect(Unit) { engine.start() }

    if (state is G5State.Resolved) {
        val url = (state as G5State.Resolved).url
        LaunchedEffect(url) {
            withContext(Dispatchers.Main) {
                activity?.let {
                    launchWebView(it, url, storage, toStub, fileLauncher, cameraLauncher, callbacks)
                }
            }
        }
    }
}

@Composable
private fun G5NotificationPermission(storage: G5Storage) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            CoroutineScope(Dispatchers.IO).launch {
                storage.put(k("px5"), PUSH_STORAGE_VALUE_TRUE)
            }
        }
    }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            storage.get(k("px5")) == null
        ) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
