package com.example.clientchodientu.ui.auth

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clientchodientu.R
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.example.clientchodientu.adapter.FullScreenImageAdapter

class ImageViewerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_image_viewer)
        window.setFlags(
            android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
            android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()
        val images = intent.getStringArrayListExtra("IMAGES") ?: arrayListOf()
        val position = intent.getIntExtra("POSITION", 0)

        val viewPager = findViewById<ViewPager2>(R.id.viewPagerFull)
        val tvCounter = findViewById<TextView>(R.id.tvCounterFull)
        val btnBack = findViewById<ImageView>(R.id.btnBackViewer)

        val adapter = FullScreenImageAdapter(images)
        viewPager.adapter = adapter
        viewPager.setCurrentItem(position, false)
        tvCounter.text = "${position + 1} / ${images.size}"
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tvCounter.text = "${position + 1} / ${images.size}"
            }
        })

        btnBack.setOnClickListener { finish() }
    }
}