package com.example.clientchodientu.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.clientchodientu.R
import com.example.clientchodientu.dto.user.ResponseProfile
import com.example.clientchodientu.dto.user.ResquestEditProfile
import com.example.clientchodientu.entity.Province
import com.example.clientchodientu.entity.User
import com.example.clientchodientu.entity.Ward
import com.example.clientchodientu.untils.token.ApiClient
import com.example.clientchodientu.untils.token.TokenManager
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.jvm.java

class EditProfileUser : AppCompatActivity() {
    private lateinit var btnBackHome: ImageButton
    private lateinit var edtFullname: EditText
    private var idUser = 0
    private lateinit var sProvinceName: Spinner
    private lateinit var rgGenderEdit: RadioGroup
    private lateinit var sWardName: Spinner
    private var selectedProvinceCode: Int = 0
    private var selectedWardCode: Int = 0
    private var SelectedGender : Boolean = false
    private lateinit var edtPhone: EditText
    private val client = OkHttpClient()
    private lateinit var edtEmail: EditText
    private lateinit var rbtnNam: RadioButton
    private lateinit var rbtnNu: RadioButton
    private lateinit var btnAccessChange: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile_user)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.EditProfileUser)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        TokenManager.init(this)
        btnBackHome=findViewById(R.id.btnBackHome)
        btnAccessChange=findViewById(R.id.btnAccessEditProfileUser)
        edtFullname=findViewById(R.id.edtFullNameUser)
        sProvinceName=findViewById(R.id.sPositionProvinceName)
        sWardName=findViewById(R.id.sPositionWardName)
        rgGenderEdit=findViewById(R.id.rgGenderEdit)
        edtEmail=findViewById(R.id.edtViewEmail)
        edtPhone=findViewById(R.id.edtPhone)
        rbtnNam=findViewById(R.id.rbtnEditNam)
        rbtnNu=findViewById(R.id.rbtnEditNu)
        btnBackHome.setOnClickListener {
            finish()
        }
        lifecycleScope.launch {
            loadProviceData()
            getInfoUser()
            btnAccessChange.setOnClickListener {
                lifecycleScope.launch {
                    editInfoUser()
                }
                finish()
            }
        }
    }
    suspend fun editInfoUser(){
        val fullNameEdit = edtFullname.text.toString()
        val provinceNameEdit=sProvinceName.selectedItem.toString()
        val wardNameEdit=sWardName.selectedItem.toString()
        val genderEdit=SelectedGender
        val idTemp = idUser
        withContext(Dispatchers.IO){
            val putData= ResquestEditProfile(
                fullName = fullNameEdit,
                provinceName = provinceNameEdit,
                wardName = wardNameEdit,
                gender = genderEdit
            )
            val gson = Gson()
            val jsonString = gson.toJson(putData)
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = jsonString.toRequestBody(mediaType)
            val editUrl="http://10.0.2.2:8080/api/user/edit-profile"
            val request= Request.Builder()
                .url(editUrl)
                .put(body)
                .addHeader("Authorization", "Bearer ${TokenManager.getToken()}")
                .build()
            val response = ApiClient.getClient(this@EditProfileUser).newCall(request).execute()
            withContext(Dispatchers.Main){
                if(response.isSuccessful){
                    Toast.makeText(this@EditProfileUser,"Cập nhật tài khoản thành công", Toast.LENGTH_LONG).show()
                }
                else{
                    Log.e("Edit_Error","Code:${response.code}")
                }
            }
        }
    }
    suspend fun getInfoUser(){
        withContext(Dispatchers.IO){
            val urlProfile="http://10.0.2.2:8080/api/user/my-profile"
            val request = Request.
            Builder()
                .get()
                .url(urlProfile)
                .addHeader("Authorization", "Bearer ${TokenManager.getToken()}")
                .build()
            val response = ApiClient.getClient(this@EditProfileUser).newCall(request).execute()
                if (response.isSuccessful){
                    val responseBody=response.body?.string()
                    val gson= Gson()
                    val responseProfile=gson.fromJson(responseBody, ResponseProfile::class.java)
                    val data = responseProfile.data
                    Log.d("DataProfile",responseProfile.data.toString())
                        withContext(Dispatchers.Main){
                            idUser=data.id
                            edtFullname.setText(data.fullName)
                            edtEmail.setText(data.email)
                            edtPhone.setText(data.phone)
                            rgGenderEdit.setOnCheckedChangeListener {
                                    group,checkId-> when(checkId){
                                R.id.rbtnEditNam->SelectedGender=false
                                R.id.rbtnEditNu->SelectedGender=true
                            }
                            }
                            if(data.gender){
                                rgGenderEdit.check(R.id.rbtnEditNu)
                            }else{
                                rgGenderEdit.check(R.id.rbtnEditNam)
                            }

                            sProvinceName.post {
                                selectSpinnerValue(sProvinceName,data.provinceName)
                            }
                        }
            }
        }
    }
    private fun selectSpinnerValue(spinner: Spinner, valueToSelect: String?) {
        if (valueToSelect == null) return
        val adapter = spinner.adapter
        for (i in 0 until adapter.count) {
            if (adapter.getItem(i).toString() == valueToSelect) {
                spinner.setSelection(i)
                break
            }
        }
    }
    suspend fun loadProviceData() {
        withContext(Dispatchers.IO) {
            try {
                val url = "https://provinces.open-api.vn/api/v2/p/"
                val request = Request.Builder().url(url).get().build()

                client.newCall(request).execute().use { response ->
                    val responseData = response.body?.string()

                    if (response.isSuccessful && responseData != null) {
                        val data =
                            Gson().fromJson(responseData, Array<Province>::class.java).toList()
//                        val provinceName = data.map { it.name }
                        // Chuyển về Main Thread để Log và Update Spinner
                        withContext(Dispatchers.Main) {
                            // Bind dữ liệu vào Spinner luôn
                            val adapter = ArrayAdapter(
                                this@EditProfileUser,
                                android.R.layout.simple_spinner_item,
                                data
                            )
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            sProvinceName.adapter = adapter

                            sProvinceName.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>?,
                                        view: View?,
                                        position: Int,
                                        id: Long
                                    ) {
                                        // Bây giờ dòng này mới chạy đúng vì adapter chứa Object Province
                                        val item = parent?.getItemAtPosition(position) as Province
                                        selectedProvinceCode = item.code
                                        Log.d("province_code", item.code.toString())
                                        // Gọi load Huyện với mã tỉnh vừa chọn
                                        lifecycleScope.launch {
                                            loadWardData(selectedProvinceCode)
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                                }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Log.e("Load Address", "Response failed")
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("Load Address", "Error: ${e.message}")
                }
            }
        }
    }
    suspend fun loadWardData(provinceCode: Int) {
        withContext(Dispatchers.IO) {
            try {
                val url = "https://provinces.open-api.vn/api/v2/p/${provinceCode}?depth=2"
                val request = Request.Builder().url(url).get().build()

                client.newCall(request).execute().use { response ->
                    val responseData = response.body?.string()

                    if (response.isSuccessful && responseData != null) {
                        val province = Gson().fromJson(responseData, Province::class.java)
                        val listWard = province.ward ?: listOf()

                        // Chuyển về Main Thread để Log và Update Spinner
                        withContext(Dispatchers.Main) {
                            Log.d("Load Address", "Districts size: ${listWard.size}")

                            // Bind dữ liệu vào Spinner luôn
                            val adapter = ArrayAdapter(
                                this@EditProfileUser,
                                android.R.layout.simple_spinner_item,
                                listWard
                            )
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            sWardName.adapter = adapter
                            sWardName.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>?,
                                        view: View?,
                                        position: Int,
                                        id: Long
                                    ) {
                                        val item = parent?.getItemAtPosition(position) as Ward
                                        selectedWardCode = item.code
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                                }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Log.e("Load Address", "Response failed")
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("Load Address", "Error: ${e.message}")
                }
            }
        }
    }

}