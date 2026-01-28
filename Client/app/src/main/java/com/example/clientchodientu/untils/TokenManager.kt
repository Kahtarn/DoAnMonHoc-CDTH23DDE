package com.example.clientchodientu.untils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.example.clientchodientu.ui.auth.LoginActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

object TokenManager {
    private const val PREF_NAME = "TokenValues"
    private const val KEY_ACCESS_TOKEN = "accessToken"
    private const val KEY_REFRESH_TOKEN = "refreshToken"
    private const val KEY_FCM_TOKEN = "fcmToken"
    private const val KEY_USER_ID="userId"
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }


    fun saveTokens(accessToken: String, refreshToken: String, fcmToken: String,userId: Int) {
        prefs.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putString(KEY_FCM_TOKEN, fcmToken)
            putInt(KEY_USER_ID,userId)
            apply()
        }
    }
    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)
    fun getToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)
    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)

    fun getFCMToken(): String? = prefs.getString(KEY_FCM_TOKEN, null)

    fun updateFCMToken(context: Context,userId: Int, token: String) {
        val client = OkHttpClient()
        val url = "http://10.0.2.2:8080/api/chat/set-fcm-token"

        // Tạo JSON chuẩn
        val jsonObject = JSONObject()
        jsonObject.put("userId", userId)
        jsonObject.put("token", token)

        val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder().url(url).post(requestBody).build()

        // Chạy bất đồng bộ để không treo UI
        ApiClient.getClient(context).newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("FCM", "Lỗi mạng khi gửi token", e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.d("FCM", "Đã cập nhật token lên Server thành công!")
                } else {
                    Log.e("FCM", "Server trả về lỗi: ${response.code}")
                }
                response.close()
            }
        })
    }
    fun logout(context: Context) {
        TokenManager.clear() // Xóa sạch token cũ
        val intent = Intent(context, LoginActivity::class.java)
        // Xóa hết các màn hình cũ, chỉ giữ lại Login
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }
    fun clear() {
        prefs.edit().clear().apply()
    }
}