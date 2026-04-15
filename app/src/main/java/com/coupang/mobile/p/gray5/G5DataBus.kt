package com.coupang.mobile.p.gray5

import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val K_RE  = "r"
private const val K_GAD = "g"
private const val K_DM  = "d"
private const val K_FID = "f"

internal data class G5Payload(
    val ref: String?,
    val gad: String?,
    val dm: String?,
    val fid: String?
)

internal suspend fun collectPayload(
    re: G5ReProvider,
    ga: G5GaProvider,
    dm: G5DmProvider
): G5Payload = coroutineScope {
    val ch = Channel<Pair<String, String?>>(4)

    launch { ch.send(K_RE  to re.fetch()) }
    launch { ch.send(K_GAD to ga.fetch()) }
    launch { ch.send(K_DM  to dm.fetch()) }
    launch {
        ch.send(K_FID to try {
            Firebase.analytics.appInstanceId.await()
        } catch (_: Exception) { "error" })
    }

    val map = mutableMapOf<String, String?>()
    repeat(4) { ch.receive().also { map[it.first] = it.second } }
    ch.close()

    G5Payload(ref = map[K_RE], gad = map[K_GAD], dm = map[K_DM], fid = map[K_FID])
}
