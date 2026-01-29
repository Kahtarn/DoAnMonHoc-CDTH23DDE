package com.example.clientchodientu

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.clientchodientu.ui.auth.FragmentProfileUser
import com.example.clientchodientu.ui.chat.FragmentChat
import com.example.clientchodientu.ui.home.FragmentHome
import com.example.clientchodientu.untils.token.TokenManager
import com.example.clientchodientu.ui.product.FragmentAddPost
import com.example.clientchodientu.ui.user.FragmentPostManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ProfileUser)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val BottomNavigatior=findViewById<BottomNavigationView>(R.id.bottomNavigation)
        replaceFragment(FragmentHome())
        TokenManager.init(this)
            BottomNavigatior.setOnItemSelectedListener {
                item ->
                when(item.itemId){
                    R.id.nav_home->replaceFragment(FragmentHome())
                    R.id.nav_add->replaceFragment(FragmentAddPost())
                    R.id.nav_save->replaceFragment(FragmentPostManager())
                    R.id.nav_chat->replaceFragment(FragmentChat())
                    R.id.nav_account->replaceFragment(FragmentProfileUser())
                    else -> false
                }
                true
            }
    }
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_Home, fragment)
            .commit()
    }
}