package com.example.clientchodientu.untils.token

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Lấy access token
        val accessToken = TokenManager.getToken()

        // Nếu chưa có chìa khóa (lúc chưa đăng nhập), cho qua
        if (accessToken == null) {
            return chain.proceed(originalRequest)
        }

        // Dán token vào Header
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()

        return chain.proceed(newRequest)
    }
}