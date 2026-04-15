package com.coupang.mobile.p.gray5

import androidx.core.net.toUri

internal class G5UrlBuilder(baseUrl: String) {
    private val builder = baseUrl.toUri().buildUpon()

    fun param(key: String, value: String?): G5UrlBuilder {
        if (!value.isNullOrEmpty()) builder.appendQueryParameter(key, value)
        return this
    }

    fun build(): String = builder.build().toString()
}
