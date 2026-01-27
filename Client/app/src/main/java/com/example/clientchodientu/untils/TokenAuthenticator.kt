package com.example.clientchodientu.untils

import android.content.Context
import android.content.Intent
import com.example.clientchodientu.dto.auth.login.LoginResponse
import com.example.clientchodientu.ui.auth.LoginActivity
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class TokenAuthenticator(private val context: Context) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) {
            android.util.Log.e("AUTH", "Thử lại quá nhiều lần. Dừng lại.")
            return null
        }

        synchronized(this) {
            val accessTokenInManager = TokenManager.getToken()

            if (response.request.header("Authorization") != "Bearer $accessTokenInManager") {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $accessTokenInManager")
                    .build()
            }

            val refreshToken = TokenManager.getRefreshToken() ?: run {
                logout()
                return null
            }

            val newTokenResponse = getNewToken(refreshToken)

            if (newTokenResponse != null && newTokenResponse.success) {
                val newAccess = newTokenResponse.data.accessToken
                val newRefresh = newTokenResponse.data.refreshToken ?: refreshToken

                TokenManager.saveTokens(newAccess, newRefresh, TokenManager.getFCMToken() ?: "")

                return response.request.newBuilder()
                    .header("Authorization", "Bearer $newAccess")
                    .build()
            } else {
                android.util.Log.e("AUTH", "Refresh Token thất bại hoặc hết hạn.")
                logout()
                return null
            }
        }
    }

    private fun getNewToken(refreshToken: String): LoginResponse? {
        val client = OkHttpClient()
        val json = "{\"refreshToken\": \"$refreshToken\"}"
        val body = json.toRequestBody("application/json".toMediaType())

        val oldAccessToken = TokenManager.getToken()

        val request = Request.Builder()
            .url("http://10.0.2.2:8080/api/auth/refresh-token")
            .header("Authorization", "Bearer $oldAccessToken")
            .post(body)
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                Gson().fromJson(responseBody, LoginResponse::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun logout() {
        TokenManager.clear()
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }
    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }
}