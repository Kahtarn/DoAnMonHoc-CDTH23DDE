package com.example.clientchodientu.untils.token

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.example.clientchodientu.dto.chat.ApiResponse
import com.example.clientchodientu.ui.auth.LoginActivity
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
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

    suspend fun getUserId(context: Context): Int {
        return withContext(Dispatchers.IO) { // Chạy trên luồng phụ
            val url = "http://10.0.2.2:8080/api/chat/get-user-id"
            val request = Request.Builder().url(url).get().build()

            try {
                val response = ApiClient.getClient(context).newCall(request)
                    .execute() // Dùng execute() thay vì enqueue
                if (response.isSuccessful) {
                    val jsonString = response.body?.string()
                    // Giả sử server trả về: {"data": 123, "message": "success"}
                    val apiResponse = Gson().fromJson(jsonString, ApiResponse::class.java)

                    // Ép kiểu data về Double rồi sang Int (Gson hay đọc số thành Double)
                    withContext(Dispatchers.Main) {
                        Log.d("Get User Id", apiResponse.data["userId"].toString())
                    }
                    apiResponse.data["userId"] ?: -1
                } else {
                    -1
                }
            } catch (e: Exception) {
                Log.e("User Id", "Lỗi: ${e.message}")
                -1
            }
        }
    }


    fun clear() {
        prefs.edit().clear().apply()
    }

    fun logout(context: Context) {
        TokenManager.clear() // Xóa sạch token cũ
        val intent = Intent(context, LoginActivity::class.java)
        // Xóa hết các màn hình cũ, chỉ giữ lại Login
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }
}