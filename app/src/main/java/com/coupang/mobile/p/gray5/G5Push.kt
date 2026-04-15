package com.coupang.mobile.p.gray5

import android.content.Context
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import java.util.Locale

internal class G5Push(private val context: Context) {

    private val client = OkHttpClient()

    suspend fun registerDevice() {
        try {
            val gadid = try {
                FirebaseAnalytics.getInstance(context).appInstanceId.await()
            } catch (_: Exception) { "error" }

            val rawToken = try {
                FirebaseMessaging.getInstance().token.await()
            } catch (_: Exception) { "error" }

            val token = withContext(Dispatchers.IO) { URLEncoder.encode(rawToken, "UTF-8") }

            val url = "https://$PUSH_NOTIFICATION_API_URL?$PUSH_NOTIFICATION_API_GADID_KEY=$gadid&$PUSH_NOTIFICATION_API_FCM_TOKEN_KEY=$token"

            val req = Request.Builder()
                .url(url)
                .addHeader("Accept-Language", Locale.getDefault().toLanguageTag())
                .get()
                .build()

            withContext(Dispatchers.IO) { client.newCall(req).execute() }
        } catch (e: Exception) {
            Log.e("G5", "push error ${e.message}")
        }
    }

    suspend fun postback(trackingId: String) {
        try {
            val rawToken = try {
                FirebaseMessaging.getInstance().token.await()
            } catch (_: Exception) { "error" }

            val token = withContext(Dispatchers.IO) { URLEncoder.encode(rawToken, "UTF-8") }
            val url = "https://$POSTBACK_API_URL?$POSTBACK_TRACKING_ID_KEY=$trackingId&$POSTBACK_FCM_TOKEN_KEY=$token"

            val req = Request.Builder().url(url).get().build()
            withContext(Dispatchers.IO) { client.newCall(req).execute() }
        } catch (e: Exception) {
            Log.e("G5", "postback error ${e.message}")
        }
    }
}
