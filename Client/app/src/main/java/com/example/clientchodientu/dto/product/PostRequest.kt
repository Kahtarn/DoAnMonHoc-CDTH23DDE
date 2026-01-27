package com.example.clientchodientu.dto.product

import android.net.Uri
import java.math.BigDecimal

data class PostRequest(
    private val title : String,
    private val description : String,
    private val price : BigDecimal,
    private val categoryId : Int,
    private val imageUri : List<Uri>
)
