package com.example.clientchodientu.untils.token

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.clientchodientu.dto.auth.login.LoginResponse
import com.example.clientchodientu.ui.auth.LoginActivity
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class TokenAuthenticator(private val context: Context) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) {
            Log.e("AUTH", "Thử lại quá nhiều lần. Dừng lại.")
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
                TokenManager.logout(context)
                return null
            }

        //  Thực hiện gọi API xin cấp lại token
        // Lưu ý: Phải tạo client mới để không bị dính Interceptor cũ
        val newTokenResponse = getNewToken(refreshToken)

        // 4. Xử lý kết quả
        if (newTokenResponse != null && newTokenResponse.success) {
            // A. THÀNH CÔNG: Cấp được chìa khóa mới
            val newAccessToken = newTokenResponse.data.accessToken
            val newRefreshToken = newTokenResponse.data.refreshToken // Server thường cấp luôn refresh token mới


            TokenManager.saveTokens(newAccessToken, newRefreshToken, "")

                // Trả về request cũ nhưng thay Header bằng token MỚI
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $newAccessToken")
                    .build()
            } else {
                // B. THẤT BẠI: Refresh Token cũng hết hạn (sau 7 ngày) -> Về trang đăng nhập
                TokenManager.logout(context)
                return null
            }
        }
    }

    // Hàm gọi API Refresh Token
    private fun getNewToken(refreshToken: String): LoginResponse? {
        val client = OkHttpClient() // Client sạch, không Interceptor
        val json = "{\"refreshToken\": \"$refreshToken\"}" // Body JSON gửi lên
        val body = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("https://uncondensable-diplopic-gibson.ngrok-free.dev/api/auth/refresh-token") // Đảm bảo API này đúng
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

    // Hàm Logout


    // Đếm số lần request đã bị thử lại
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
