package com.example.clientchodientu.untils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject

object TokenManager {
    private const val PREF_NAME = "TokenValues"
    private const val KEY_ACCESS_TOKEN = "accessToken"
    private const val KEY_REFRESH_TOKEN = "refreshToken"
    private const val KEY_FCM_TOKEN = "fcmToken"

    private const val FIREBASE_TOKEN = "firebaseToken"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveTokens(
        accessToken: String = "",
        refreshToken: String = "",
        fcmToken: String? = "",
        firebaseToken: String = ""
    ) {
        prefs.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putString(KEY_FCM_TOKEN, fcmToken)
            putString(FIREBASE_TOKEN, firebaseToken)
            apply()
        }
    }

    fun getToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)
    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)

    fun getFCMToken(): String? = prefs.getString(KEY_FCM_TOKEN, null)

    fun getFirebaseToken(): String? = prefs.getString(FIREBASE_TOKEN, null)

    fun updateFCMToken(context: Context, token: String) {
        val client = OkHttpClient()
        val url = "http://10.0.2.2:8080/api/chat/set-fcm-token"

        // Tạo JSON chuẩn
        val jsonObject = JSONObject()
        jsonObject.put("token", token)

        val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder().url(url).post(requestBody).build()

        // Chạy bất đồng bộ để không treo UI
        ApiClient.getClient(context).newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: okio.IOException) {
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

    fun getUserId(context: Context, token: String) {
        val client = OkHttpClient()
        val url = "http://10.0.2.2:8080/api/chat/set-fcm-token"

        // Tạo JSON chuẩn
        val jsonObject = JSONObject()
        jsonObject.put("token", token)

        val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder().url(url).post(requestBody).build()

        // Chạy bất đồng bộ để không treo UI
        ApiClient.getClient(context).newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: okio.IOException) {
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


    fun clear() {
        prefs.edit().clear().apply()
    }
}