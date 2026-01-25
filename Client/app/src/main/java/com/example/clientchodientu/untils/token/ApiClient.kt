// File: api/ApiClient.kt
package com.example.clientchodientu.untils.token

import android.content.Context
import okhttp3.OkHttpClient

object ApiClient {
    private var client: OkHttpClient? = null

    fun getClient(context: Context): OkHttpClient {
        if (client == null) {
            client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor()) // Tự động gắn Token
                .authenticator(TokenAuthenticator(context)) // Tự động Refresh khi lỗi 401
                .build()
        }
        return client!!
    }
}