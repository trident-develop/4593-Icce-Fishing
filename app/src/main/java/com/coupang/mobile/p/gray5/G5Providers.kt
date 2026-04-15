package com.coupang.mobile.p.gray5

import android.content.Context
import android.os.Build
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.coroutines.resume

internal class G5ReProvider(private val context: Context) {
    @OptIn(ExperimentalAtomicApi::class)
    suspend fun fetch(): String = suspendCancellableCoroutine { continuation ->
        val client = InstallReferrerClient.newBuilder(context).build()
        val hasResumed = AtomicBoolean(false)


        continuation.invokeOnCancellation {
            if (client.isReady) client.endConnection()
        }


        client.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                try {
                    if (!hasResumed.compareAndSet(false, true)) return


                    val result = if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {
                        client.installReferrer.installReferrer ?: "null"
                    } else {
                        "null"
                    }
                    // TODO Change
//                    continuation.resume("cmpgn=sub1_TEST-Deeplink_sub3_sub4_sub5_sub6")
                    continuation.resume(result)
                } catch (e: Exception) {
                    if (hasResumed.get()) return
                    continuation.resume("null")
                } finally {
                    client.endConnection()
                }
            }


            override fun onInstallReferrerServiceDisconnected() {
                if (hasResumed.compareAndSet(false, true)) {
                    continuation.resume("null")
                }
            }
        })
    }
}


internal class G5GaProvider(private val context: Context) {
    suspend fun fetch(): String = withContext(context = Dispatchers.IO) {
        try {
            val info = AdvertisingIdClient.getAdvertisingIdInfo(context)
            if (!info.isLimitAdTrackingEnabled) {
                info.id ?: "000000000000-0000-0000-0000-00000000"
            } else {
                "000000000000-0000-0000-0000-00000000"
            }
        } catch (_: Exception) {
            "000000000000-0000-0000-0000-00000000"
        }
    }
}

internal class G5DmProvider {
    fun fetch() = Build.BRAND.replaceFirstChar { it.titlecase(Locale.getDefault())} + " " + Build.MODEL
}

