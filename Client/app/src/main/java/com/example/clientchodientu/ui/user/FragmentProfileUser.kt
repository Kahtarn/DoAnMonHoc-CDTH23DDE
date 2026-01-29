package com.example.clientchodientu.ui.auth

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.clientchodientu.R
import com.example.clientchodientu.untils.FileUtils.getFileFromUri
import com.example.clientchodientu.untils.token.ApiClient
import com.example.clientchodientu.untils.token.TokenManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import com.example.clientchodientu.dto.user.ResponseProfile

class FragmentProfileUser : Fragment() {
    private lateinit var tvActivityEditProfileUser: TextView
    private lateinit var tvActivityLogout: TextView
    private lateinit var tvActivityChangePassword: TextView
    private lateinit var imgProfile: ImageView

    private lateinit var tvFullNameProfile: TextView

    private lateinit var btnChangeAvatar: FloatingActionButton

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                // Hiển thị ảnh vừa chọn lên ImageView
                Glide.with(this)
                    .load(it)
                    .circleCrop()
                    .into(imgProfile)

                uploadAvatarToServer(it)
            }
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_user,container,false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        setUpEvent()
    }

    private fun init(view:View) {
        tvActivityEditProfileUser =view.findViewById(R.id.tvActivityEditProfile)
        tvActivityChangePassword = view.findViewById(R.id.tvActivityChangePassword)
        tvActivityLogout = view.findViewById(R.id.tvActivityLogout)
        btnChangeAvatar = view.findViewById(R.id.btnChangeAvatar)
        imgProfile = view.findViewById(R.id.imgProfile)
        tvFullNameProfile = view.findViewById(R.id.tvUsername)
        lifecycleScope.launch {
            getInfoUser()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun setUpEvent() {
        tvActivityEditProfileUser.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileUser::class.java))
        }
        tvActivityChangePassword.setOnClickListener {
            startActivity(Intent(requireContext(), ForgotPasswordActivity::class.java))
        }
        tvActivityLogout.setOnClickListener {
            TokenManager.logout(requireContext())
        }


        btnChangeAvatar.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun uploadAvatarToServer(uri: Uri) {
        // 1. Chuyển URI thành File thật (Dùng hàm getFileFromUri tôi đã đưa lúc trước)
        val file = getFileFromUri(requireContext(), uri) ?: return

        // 2. Tạo RequestBody từ file
        val fileRequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("avatarUrl", file.name, fileRequestBody)
            .build()

        // 4. Bắn API
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("http://10.0.2.2:8080/api/user/update-avatar")
                    .post(requestBody)
                    .build()
                val response = ApiClient.getClient(requireContext()).newCall(request).execute()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val updatedUser = response.body?.string()
                        Toast.makeText(
                            requireContext(),
                            "Cập nhật ảnh thành công!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Log.e(
                            "API_ERROR",
                            "Code: ${response.code}}"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("UPLOAD_ERROR", "Exception: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    suspend fun getInfoUser() {
        withContext(Dispatchers.IO) {
            val urlProfile = "http://10.0.2.2:8080/api/user/my-profile"
            val request = Request.Builder()
                .get()
                .url(urlProfile)
                .addHeader("Authorization", "Bearer ${TokenManager.getToken()}")
                .build()
            val response = ApiClient.getClient(requireContext()).newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val gson = Gson()
                val responseProfile = gson.fromJson(responseBody, ResponseProfile::class.java)
                val data = responseProfile.data
                val BASE_URL = "http://10.0.2.2:8080"
                val path = data.avatarUrl
                val imgUrl = if (path!= null && path.startsWith("http"))
                    path
                else if (!path.isNullOrBlank())
                {
                    BASE_URL + path
                }
                else{
                    null
                }
                withContext(Dispatchers.Main) {
                    tvFullNameProfile.setText(data.fullName)
                    Glide.with(requireContext())
                        .load(imgUrl)
                        .placeholder(R.drawable.ic_user_placeholder)
                        .error(R.drawable.ic_user_placeholder)
                        .centerCrop()
                        .into(imgProfile)
                }
            }
        }
    }
}