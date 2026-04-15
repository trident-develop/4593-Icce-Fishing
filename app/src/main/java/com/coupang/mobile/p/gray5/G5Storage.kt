package com.coupang.mobile.p.gray5

import android.annotation.SuppressLint
import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.RegistryConfiguration
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import kotlinx.coroutines.flow.first
import java.io.File

internal class G5Storage private constructor(context: Context) {

    private val appContext = context.applicationContext

    init { AeadConfig.register() }

    private val aead: Aead by lazy {
        AndroidKeysetManager.Builder()
            .withSharedPref(appContext, TINK_KEYSET, TINK_PREF_FILE)
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri("android-keystore://$MASTER_KEY_URI")
            .build()
            .keysetHandle
            .getPrimitive(RegistryConfiguration.get(), Aead::class.java)
    }

    private val dataStore = PreferenceDataStoreFactory.create(
        produceFile = { File(appContext.noBackupFilesDir, "$FILE_NAME.preferences_pb") }
    )

    suspend fun put(key: String, value: String) {
        try {
            val enc = aead.encrypt(value.toByteArray(), null)
            val b64 = Base64.encodeToString(enc, Base64.NO_WRAP)
            dataStore.edit { it[stringPreferencesKey(key)] = b64 }
        } catch (e: Exception) {
            Log.e("G5", "write error", e)
        }
    }

    suspend fun get(key: String): String? = try {
        val b64 = dataStore.data.first()[stringPreferencesKey(key)] ?: return null
        val dec = aead.decrypt(Base64.decode(b64, Base64.NO_WRAP), null)
        String(dec)
    } catch (e: Exception) {
        null
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile private var INSTANCE: G5Storage? = null

        fun getInstance(context: Context): G5Storage =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: G5Storage(context).also { INSTANCE = it }
            }
    }
}
