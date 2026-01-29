package com.example.clientchodientu.dto.user

import java.sql.Timestamp

data class DetailsUserResponse(
    val fullName : String,
    val email : String,
    val phone : String,
    val gender : Boolean,
    val provinceName : String,
    val wardName : String,
    val createAt : String,
    val avatarUrl: String?
)