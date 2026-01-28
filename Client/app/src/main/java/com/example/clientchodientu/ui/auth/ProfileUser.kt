package com.example.clientchodientu.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clientchodientu.R
import com.example.clientchodientu.untils.TokenAuthenticator
import com.example.clientchodientu.untils.TokenManager

class ProfileUser : AppCompatActivity() {
    private lateinit var tvActivityEditProfileUser: TextView
    private lateinit var tvActivityLogout: TextView
    private lateinit var tvActivityChangePassword: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_profile_user)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ProfileUser)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        tvActivityEditProfileUser=findViewById(R.id.tvActivityEditProfile)
        tvActivityChangePassword=findViewById(R.id.tvActivityChangePassword)
        tvActivityLogout=findViewById(R.id.tvActivityLogout)

        tvActivityEditProfileUser.setOnClickListener {
            startActivity(Intent(this, EditProfileUser::class.java))
        }
        tvActivityChangePassword.setOnClickListener {
            startActivity(Intent(this, ChangePassword::class.java))
        }
        tvActivityLogout.setOnClickListener {
            TokenManager.logout(this)
        }
    }
}