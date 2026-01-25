package com.example.clientchodientu.ui.edit

import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clientchodientu.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class EditProduct : AppCompatActivity() {
    private lateinit var Tvtitle : TextInputEditText
    private lateinit var Actvcategory : AutoCompleteTextView
    private lateinit var Tvprice : TextInputEditText
    private lateinit var Tvdescription : TextInputEditText
    private lateinit var btnSave : Button
    private lateinit var btnHuy : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_product)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        Tvtitle = findViewById(R.id.inputTitle)
        Actvcategory = findViewById(R.id.actvCategory)
        Tvprice = findViewById(R.id.inputPrice)
        Tvdescription = findViewById(R.id.inputDesc)
        btnSave = findViewById(R.id.btnSave)

        val titleOld = intent.getStringExtra("title")
        val priceOld = intent.getStringExtra("price")
        val descOld = intent.getStringExtra("description")

        Tvtitle.setText(titleOld)
        Tvprice.setText(priceOld.toString())
        Tvdescription.setText(descOld)


        btnHuy = findViewById(R.id.btnCancel)
        btnHuy.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc chắn muốn hủy mọi thay đổi không?")
                .setPositiveButton("Đồng ý") { _, _ ->
                    finish()
                }
                .setNegativeButton("Không", null)
                .show()
        }
    }

}